package com.abrigo.controller;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.abrigo.model.NavigationManager;
import com.abrigo.service.RelatorioExportador;
import com.abrigo.service.RelatorioService;
import com.abrigo.service.RelatorioService.Coluna;
import com.abrigo.service.RelatorioService.Indicador;
import com.abrigo.service.RelatorioService.Relatorio;
import com.abrigo.service.RelatorioService.Secao;

import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;

public abstract class RelatorioBaseController {

    private static final String URL_GOOGLE_PLANILHAS = "https://sheets.new";
    private static final DateTimeFormatter DATA_ARQUIVO = DateTimeFormatter.BASIC_ISO_DATE;

    @FXML protected DatePicker dataInicio;
    @FXML protected DatePicker dataFim;
    @FXML protected ComboBox<String> cbSituacao;
    @FXML protected HBox boxPeriodo;
    @FXML protected HBox boxSituacao;
    @FXML protected Label lblTitulo;
    @FXML protected Label lblSubtitulo;
    @FXML protected Label lblStatus;
    @FXML protected HBox boxIndicadores;
    @FXML protected VBox areaTabelas;
    @FXML protected Button btnGerar;
    @FXML protected Button btnExcel;
    @FXML protected Button btnPdf;
    @FXML protected Button btnGoogle;

    protected final RelatorioService service = new RelatorioService();
    protected Relatorio atual;

    private int geracaoAtual;

    protected abstract String titulo();

    protected abstract String descricao();

    protected abstract String prefixoArquivo();

    protected abstract Relatorio gerarDados(LocalDate inicio, LocalDate fim, String situacao);

    protected boolean usaPeriodo() {
        return true;
    }

    protected List<String> opcoesSituacao() {
        return null;
    }

    @FXML
    public void initialize() {
        lblTitulo.setText(titulo());
        lblSubtitulo.setText(descricao());

        // Configuração para desabilitar datas futuras nos DatePickers
        Callback<DatePicker, DateCell> dayCellFactory = new Callback<>() {
            @Override
            public DateCell call(final DatePicker datePicker) {
                return new DateCell() {
                    @Override
                    public void updateItem(LocalDate item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item.isAfter(LocalDate.now())) {
                            setDisable(true);
                            setStyle("-fx-background-color: #ffc4c4;");
                        }
                    }
                };
            }
        };

        if (dataInicio != null) {
            dataInicio.setDayCellFactory(dayCellFactory);
        }
        if (dataFim != null) {
            dataFim.setDayCellFactory(dayCellFactory);
        }

        dataInicio.setValue(LocalDate.now().withDayOfMonth(1));
        dataFim.setValue(LocalDate.now());
        setVisivel(boxPeriodo, usaPeriodo());

        List<String> opcoes = opcoesSituacao();
        setVisivel(boxSituacao, opcoes != null);
        if (opcoes != null) {
            cbSituacao.setItems(FXCollections.observableArrayList(opcoes));
            cbSituacao.getSelectionModel().selectFirst();
            cbSituacao.setOnAction(e -> gerar());
        }

        habilitarExportacao(false);
        gerar();
    }

    @FXML
    public void gerar() {
        final LocalDate inicio = dataInicio.getValue();
        final LocalDate fim = dataFim.getValue();
        final String situacao = cbSituacao != null ? cbSituacao.getValue() : null;

        if (usaPeriodo() && (inicio == null || fim == null)) {
            mostrarAlerta(AlertType.WARNING, "Período inválido", "Informe a data inicial e a data final.");
            return;
        }
        if (usaPeriodo() && inicio.isAfter(fim)) {
            mostrarAlerta(AlertType.WARNING, "Período inválido",
                    "A data inicial não pode ser posterior à data final.");
            return;
        }
        if (usaPeriodo() && (inicio.isAfter(LocalDate.now()) || fim.isAfter(LocalDate.now()))) {
            mostrarAlerta(AlertType.WARNING, "Data inválida", "Não é permitido selecionar datas futuras para o relatório.");
            return;
        }

        final int minhaGeracao = ++geracaoAtual;
        btnGerar.setDisable(true);
        habilitarExportacao(false);
        lblStatus.setText("Atualizando relatório...");
        areaTabelas.getChildren().setAll(criarPlaceholderCarregando());

        Task<Relatorio> task = new Task<>() {
            @Override
            protected Relatorio call() {
                return gerarDados(inicio, fim, situacao);
            }
        };

        task.setOnSucceeded(e -> {
            if (minhaGeracao != geracaoAtual) {
                return;
            }
            atual = task.getValue();
            exibir(atual);
            btnGerar.setDisable(false);
            habilitarExportacao(true);
            lblStatus.setText("Relatório atualizado.");
        });

        task.setOnFailed(e -> {
            if (minhaGeracao != geracaoAtual) {
                return;
            }
            Throwable ex = task.getException();
            if (ex != null) {
                ex.printStackTrace();
            }
            atual = null;
            areaTabelas.getChildren().setAll(criarPlaceholderVazio("Não foi possível gerar o relatório."));
            boxIndicadores.getChildren().clear();
            btnGerar.setDisable(false);
            String msg = ex == null ? "Erro desconhecido." : mensagemAmigavel(ex);
            lblStatus.setText("Falha ao gerar o relatório.");
            mostrarAlerta(AlertType.ERROR, "Erro", "Não foi possível gerar o relatório.\n" + msg);
        });

        Thread t = new Thread(task, "gerar-" + prefixoArquivo());
        t.setDaemon(true);
        t.start();
    }

    private void exibir(Relatorio rel) {
        boxIndicadores.getChildren().clear();
        for (Indicador k : rel.indicadores()) {
            boxIndicadores.getChildren().add(criarCard(k));
        }

        if (rel.secoes().size() == 1) {
            TableView<List<Object>> tabela = criarTabela(rel.secoes().get(0));
            VBox.setVgrow(tabela, Priority.ALWAYS);
            areaTabelas.getChildren().setAll(tabela);
        } else {
            TabPane abas = new TabPane();
            abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
            abas.getStyleClass().add("relatorio-abas");
            for (Secao s : rel.secoes()) {
                Tab aba = new Tab(s.nome() + " (" + s.linhas().size() + ")", criarTabela(s));
                abas.getTabs().add(aba);
            }
            VBox.setVgrow(abas, Priority.ALWAYS);
            areaTabelas.getChildren().setAll(abas);
        }
    }

    private VBox criarCard(Indicador k) {
        Label rotulo = new Label(k.rotulo());
        rotulo.getStyleClass().add("kpi-rotulo");
        Label valor = new Label(RelatorioExportador.formatar(k.valor(), k.tipo()));
        valor.getStyleClass().add("kpi-valor");

        if ("Saldo".equals(k.rotulo()) && k.valor() instanceof Number n) {
            valor.getStyleClass().add(n.doubleValue() < 0 ? "kpi-negativo" : "kpi-positivo");
        }

        VBox card = new VBox(2, rotulo, valor);
        card.getStyleClass().add("kpi-card");
        HBox.setHgrow(card, Priority.ALWAYS);
        card.setMaxWidth(Double.MAX_VALUE);
        return card;
    }

    private TableView<List<Object>> criarTabela(Secao secao) {
        TableView<List<Object>> tabela = new TableView<>();
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tabela.setStyle("-fx-background-color: transparent; -fx-border-color: transparent;");

        List<Coluna> colunas = secao.colunas();
        for (int i = 0; i < colunas.size(); i++) {
            final int idx = i;
            final Coluna coluna = colunas.get(i);
            TableColumn<List<Object>, String> c = new TableColumn<>(coluna.titulo());
            c.setCellValueFactory(v -> new SimpleStringProperty(
                    idx < v.getValue().size()
                            ? RelatorioExportador.formatar(v.getValue().get(idx), coluna.tipo())
                            : ""));
            switch (coluna.tipo()) {
                case MOEDA, DECIMAL, INTEIRO -> c.setStyle("-fx-alignment: CENTER-RIGHT;");
                case DATA -> c.setStyle("-fx-alignment: CENTER;");
                default -> c.setStyle("-fx-alignment: CENTER-LEFT;");
            }
            tabela.getColumns().add(c);
        }
        tabela.setItems(FXCollections.observableArrayList(secao.linhas()));
        tabela.setPlaceholder(criarPlaceholderVazio("Nenhum registro encontrado."));
        return tabela;
    }

    private Node criarPlaceholderCarregando() {
        Label pata = new Label("🐾");
        pata.getStyleClass().add("loading-paw");

        ScaleTransition pulso = new ScaleTransition(Duration.millis(650), pata);
        pulso.setFromX(0.85);
        pulso.setFromY(0.85);
        pulso.setToX(1.25);
        pulso.setToY(1.25);
        pulso.setCycleCount(Animation.INDEFINITE);
        pulso.setAutoReverse(true);
        pulso.play();

        Label texto = new Label("Farejando os dados...");
        texto.getStyleClass().add("loading-text");

        VBox box = new VBox(10, pata, texto);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("loading-placeholder");
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }

    private Node criarPlaceholderVazio(String mensagem) {
        Label pata = new Label("🐾");
        pata.getStyleClass().add("empty-paw");

        Label texto = new Label(mensagem);
        texto.getStyleClass().add("loading-text");

        VBox box = new VBox(10, pata, texto);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("loading-placeholder");
        VBox.setVgrow(box, Priority.ALWAYS);
        return box;
    }

    @FXML
    public void exportarExcel() {
        File arquivo = escolherArquivo("Salvar relatório em Excel", ".xlsx",
                "Planilha Excel (*.xlsx)");
        if (arquivo == null) {
            return;
        }
        try {
            RelatorioExportador.exportarExcel(atual, arquivo);
            lblStatus.setText("Excel salvo: " + arquivo.getName());
            mostrarAlerta(AlertType.INFORMATION, "Exportação concluída",
                    "Planilha Excel salva em:\n" + arquivo.getAbsolutePath());
        } catch (Exception e) {
            falhaExportacao("Excel", e);
        }
    }

    @FXML
    public void exportarPdf() {
        File arquivo = escolherArquivo("Salvar relatório em PDF", ".pdf", "Documento PDF (*.pdf)");
        if (arquivo == null) {
            return;
        }
        try {
            RelatorioExportador.exportarPdf(atual, arquivo);
            lblStatus.setText("PDF salvo: " + arquivo.getName());
            mostrarAlerta(AlertType.INFORMATION, "Exportação concluída",
                    "PDF salvo em:\n" + arquivo.getAbsolutePath());
        } catch (Exception e) {
            falhaExportacao("PDF", e);
        }
    }

    @FXML
    public void exportarGoogle() {
        File arquivo = escolherArquivo("Salvar planilha para o Google Planilhas", ".xlsx",
                "Planilha Excel (*.xlsx)");
        if (arquivo == null) {
            return;
        }
        try {
            RelatorioExportador.exportarExcel(atual, arquivo);
            lblStatus.setText("Planilha salva: " + arquivo.getName() + " — pronta para o Google Planilhas.");
        } catch (Exception e) {
            falhaExportacao("Google Planilhas", e);
            return;
        }

        boolean abriuNavegador = abrir(() -> Desktop.getDesktop().browse(URI.create(URL_GOOGLE_PLANILHAS)));
        abrir(() -> Desktop.getDesktop().open(arquivo.getParentFile()));

        String passos = "A planilha foi salva em:\n" + arquivo.getAbsolutePath() + "\n\n"
                + (abriuNavegador
                        ? "O Google Planilhas foi aberto no navegador.\n"
                        : "Abra o Google Planilhas em: " + URL_GOOGLE_PLANILHAS + "\n")
                + "\nPara importar:\n"
                + "1. Na planilha em branco, clique em Arquivo > Importar.\n"
                + "2. Aba \"Fazer upload\": arraste o arquivo .xlsx (a pasta dele foi aberta para você).\n"
                + "3. Escolha \"Criar nova planilha\" e confirme.";
        mostrarAlerta(AlertType.INFORMATION, "Google Planilhas", passos);
    }

    private File escolherArquivo(String titulo, String extensao, String descricaoFiltro) {
        if (atual == null) {
            mostrarAlerta(AlertType.WARNING, "Exportar", "Gere o relatório antes de exportar.");
            return null;
        }
        FileChooser fc = new FileChooser();
        fc.setTitle(titulo);
        fc.setInitialFileName(prefixoArquivo() + "_" + LocalDate.now().format(DATA_ARQUIVO) + extensao);
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter(descricaoFiltro, "*" + extensao));
        File arquivo = fc.showSaveDialog(lblTitulo.getScene().getWindow());
        if (arquivo != null && !arquivo.getName().toLowerCase().endsWith(extensao)) {
            arquivo = new File(arquivo.getParentFile(), arquivo.getName() + extensao);
        }
        return arquivo;
    }

    private void falhaExportacao(String formato, Exception e) {
        e.printStackTrace();
        lblStatus.setText("Falha ao exportar para " + formato + ".");
        mostrarAlerta(AlertType.ERROR, "Erro ao exportar",
                "Não foi possível exportar para " + formato + ".\n" + mensagemAmigavel(e)
                        + "\n\nSe o arquivo estiver aberto em outro programa, feche-o e tente novamente.");
    }

    private interface AcaoDesktop {
        void executar() throws Exception;
    }

    private boolean abrir(AcaoDesktop acao) {
        try {
            if (!Desktop.isDesktopSupported()) {
                return false;
            }
            acao.executar();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @FXML
    public void voltar() {
        NavigationManager.getInstance().navegarMenu("relatorios");
        NavigationManager.getInstance().navegarConteudo("boas-vindas");
    }

    private void habilitarExportacao(boolean habilitar) {
        btnExcel.setDisable(!habilitar);
        btnPdf.setDisable(!habilitar);
        btnGoogle.setDisable(!habilitar);
    }

    private static void setVisivel(Node node, boolean visivel) {
        node.setVisible(visivel);
        node.setManaged(visivel);
    }

    private static String mensagemAmigavel(Throwable e) {
        Throwable causa = e;
        while (causa.getCause() != null && causa.getCause() != causa) {
            causa = causa.getCause();
        }
        String msg = causa.getMessage();
        return msg == null || msg.isBlank() ? causa.getClass().getSimpleName() : msg;
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}