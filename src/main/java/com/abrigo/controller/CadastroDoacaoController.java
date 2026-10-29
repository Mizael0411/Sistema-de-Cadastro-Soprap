package com.abrigo.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;

import com.abrigo.dao.DoacaoDAO;
import com.abrigo.model.Doacao;
import com.abrigo.model.ItemDoacao;
import com.abrigo.model.NavigationManager;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

public class CadastroDoacaoController {

    @FXML
    private TextField txtNomeDoador;

    @FXML
    private ComboBox<String> cbTipoDoacao;

    @FXML
    private DatePicker dpDataDoacao;

    @FXML
    private TextField txtValor;

    @FXML
    private TextArea txtDescricao;

    @FXML
    private Label lblDescricao;

    @FXML
    private VBox boxValor;

    @FXML
    private VBox boxDescricao;

    @FXML
    private VBox boxItens;

    @FXML
    private VBox listaItens;

    private final DoacaoDAO doacaoDAO = new DoacaoDAO();

    private Doacao doacaoEmEdicao;

    private final List<LinhaItem> linhasItens = new ArrayList<>();

    private boolean doacaoLegadaSemItens = false;

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu")
                    .withResolverStyle(ResolverStyle.STRICT);

    private static class LinhaItem {

        private final HBox linha;
        private final TextField txtNome;
        private final TextField txtQuantidade;
        private final DatePicker dpValidade;

        private LinhaItem(
                HBox linha,
                TextField txtNome,
                TextField txtQuantidade,
                DatePicker dpValidade) {

            this.linha = linha;
            this.txtNome = txtNome;
            this.txtQuantidade = txtQuantidade;
            this.dpValidade = dpValidade;
        }
    }

    @FXML
    public void initialize() {

        cbTipoDoacao.setItems(
                FXCollections.observableArrayList(
                        "FINANCEIRA",
                        "MATERIAL"
                )
        );

        cbTipoDoacao.setPromptText("Selecione...");

        dpDataDoacao.setValue(LocalDate.now());

        dpDataDoacao.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date != null && date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #FFEBEE;");
                }
            }
        });

        handleTipoChange();

        Doacao doacaoPendente =
                NavigationManager.getInstance()
                        .getDoacaoParaEdicao();

        if (doacaoPendente != null) {
            carregarDoacaoParaEdicao(doacaoPendente);
        }
    }

    @FXML
    private void handleTipoChange() {

        String tipo = cbTipoDoacao.getValue();

        boolean financeira =
                "FINANCEIRA".equalsIgnoreCase(tipo);

        boolean material =
                "MATERIAL".equalsIgnoreCase(tipo);

        if (boxValor != null) {
            boxValor.setVisible(financeira);
            boxValor.setManaged(financeira);
        }

        if (boxItens != null) {
            boxItens.setVisible(material);
            boxItens.setManaged(material);
        }

        if (lblDescricao != null) {
            if (financeira) {
                lblDescricao.setText(
                        "OBSERVAÇÃO / DETALHES (OPCIONAL)"
                );
            } else {
                lblDescricao.setText(
                        "OBSERVAÇÃO / DESCRIÇÃO (OPCIONAL)"
                );
            }
        }
    }

    @FXML
    private void adicionarItem() {
        criarLinhaItem(null, null, null);
    }

    private void criarLinhaItem(
            String nome,
            String quantidade,
            LocalDate validade) {

        HBox linha = new HBox(8);

        linha.setPadding(new Insets(5, 0, 5, 0));
        linha.setAlignment(Pos.CENTER_LEFT);

        TextField txtNome = new TextField();
        txtNome.setPromptText("Nome do item");
        txtNome.setPrefWidth(200);
        txtNome.setMaxWidth(Double.MAX_VALUE);

        HBox.setHgrow(txtNome, Priority.ALWAYS);

        TextField txtQuantidade = new TextField();
        txtQuantidade.setPromptText("Quantidade");
        txtQuantidade.setPrefWidth(100);

        DatePicker dpValidade = new DatePicker();
        dpValidade.setPrefWidth(150);

        configurarDatePickerValidade(dpValidade);

        Button btnRemover = new Button("Remover");

        btnRemover.setOnAction(event -> {
            listaItens.getChildren().remove(linha);

            linhasItens.removeIf(
                    item -> item.linha == linha
            );
        });

        if (nome != null) {
            txtNome.setText(nome);
        }

        if (quantidade != null) {
            txtQuantidade.setText(quantidade);
        }

        if (validade != null) {
            dpValidade.setValue(validade);
        }

        linha.getChildren().addAll(
                txtNome,
                txtQuantidade,
                dpValidade,
                btnRemover
        );

        listaItens.getChildren().add(linha);

        linhasItens.add(
                new LinhaItem(
                        linha,
                        txtNome,
                        txtQuantidade,
                        dpValidade
                )
        );
    }

    private void configurarDatePickerValidade(
            DatePicker datePicker) {

        datePicker.setEditable(true);
        datePicker.setPromptText("dd/MM/aaaa");

        datePicker.setConverter(new StringConverter<LocalDate>() {

            @Override
            public String toString(LocalDate data) {
                return data == null
                        ? ""
                        : FORMATO_DATA.format(data);
            }

            @Override
            public LocalDate fromString(String texto) {

                if (texto == null || texto.isBlank()) {
                    return null;
                }

                return LocalDate.parse(
                        texto.trim(),
                        FORMATO_DATA
                );
            }
        });

        datePicker.getEditor().setOnAction(event -> {
            confirmarData(datePicker, true);
        });

        datePicker.focusedProperty().addListener(
                (observable, tinhaFoco, temFoco) -> {

                    if (!temFoco) {
                        confirmarData(datePicker, true);
                    }
                }
        );
    }

    private boolean confirmarData(
            DatePicker datePicker,
            boolean mostrarErro) {

        String texto = datePicker.getEditor().getText();

        if (texto == null || texto.isBlank()) {
            datePicker.setValue(null);
            return true;
        }

        try {
            LocalDate data = LocalDate.parse(
                    texto.trim(),
                    FORMATO_DATA
            );

            datePicker.setValue(data);

            datePicker.getEditor().setText(
                    FORMATO_DATA.format(data)
            );

            return true;

        } catch (DateTimeParseException e) {

            if (mostrarErro) {
                exibirAlerta(
                        Alert.AlertType.ERROR,
                        "Data inválida",
                        "Confira a validade digitada. "
                                + "Use o formato dd/MM/aaaa, "
                                + "por exemplo, 25/10/2026."
                );
            }

            LocalDate dataAnterior = datePicker.getValue();

            datePicker.getEditor().setText(
                    dataAnterior == null
                            ? ""
                            : FORMATO_DATA.format(dataAnterior)
            );

            return false;
        }
    }

    @FXML
    private void salvar() {

        try {
            if (txtNomeDoador.getText() == null
                    || txtNomeDoador.getText().isBlank()) {

                exibirAlerta(
                        Alert.AlertType.ERROR,
                        "Erro de validação",
                        "O nome do doador é obrigatório."
                );
                return;
            }

            String nomeDoador = txtNomeDoador.getText().trim();

            if (nomeDoador.length() > 150) {
                exibirAlerta(
                        Alert.AlertType.ERROR,
                        "Nome inválido",
                        "O nome do doador deve ter no máximo 150 caracteres."
                );
                return;
            }

            String tipo = cbTipoDoacao.getValue();

            if (tipo == null
                    || (!"FINANCEIRA".equalsIgnoreCase(tipo)
                    && !"MATERIAL".equalsIgnoreCase(tipo))) {

                exibirAlerta(
                        Alert.AlertType.ERROR,
                        "Erro de validação",
                        "Selecione um tipo de doação válido."
                );
                return;
            }

            if (dpDataDoacao.getValue() == null) {
                exibirAlerta(
                        Alert.AlertType.ERROR,
                        "Erro de validação",
                        "Informe a data da doação."
                );
                return;
            }

            if (dpDataDoacao.getValue().isAfter(LocalDate.now())) {
                exibirAlerta(
                        Alert.AlertType.ERROR,
                        "Data inválida",
                        "Não é permitido registrar doações com datas futuras."
                );
                return;
            }

            Doacao doacao = doacaoEmEdicao != null
                    ? doacaoEmEdicao
                    : new Doacao();

            doacao.setNomeDoador(nomeDoador);
            doacao.setTipoDoacao(tipo.toUpperCase());
            doacao.setDataDoacao(dpDataDoacao.getValue());

            String descricao = txtDescricao.getText();

            doacao.setDescricao(
                    descricao == null || descricao.isBlank()
                            ? null
                            : descricao.trim()
            );

            List<ItemDoacao> itens = new ArrayList<>();

            if ("FINANCEIRA".equalsIgnoreCase(tipo)) {

                String textoValor = txtValor.getText();

                if (textoValor == null || textoValor.isBlank()) {
                    exibirAlerta(
                            Alert.AlertType.ERROR,
                            "Erro de validação",
                            "Informe o valor da doação financeira."
                    );
                    return;
                }

                String valorNormalizado = textoValor.trim()
                        .replace("R$", "")
                        .replace(" ", "");

                if (valorNormalizado.contains(",")
                        && valorNormalizado.contains(".")) {

                    valorNormalizado = valorNormalizado
                            .replace(".", "")
                            .replace(",", ".");

                } else {
                    valorNormalizado = valorNormalizado
                            .replace(",", ".");
                }

                double valor = Double.parseDouble(valorNormalizado);

                if (!Double.isFinite(valor) || valor <= 0) {
                    exibirAlerta(
                            Alert.AlertType.ERROR,
                            "Valor inválido",
                            "O valor da doação deve ser maior que zero."
                    );
                    return;
                }

                doacao.setValor(valor);

            } else {

                doacao.setValor(null);

                if (linhasItens.isEmpty()) {

                    boolean registroAntigoSemItens =
                            doacaoEmEdicao != null
                            && doacaoLegadaSemItens;

                    if (!registroAntigoSemItens) {
                        exibirAlerta(
                                Alert.AlertType.ERROR,
                                "Itens obrigatórios",
                                "Adicione pelo menos um item à doação material."
                        );
                        return;
                    }

                } else {

                    for (int i = 0; i < linhasItens.size(); i++) {

                        LinhaItem linha = linhasItens.get(i);

                        String nomeItem = linha.txtNome.getText();
                        String textoQuantidade =
                                linha.txtQuantidade.getText();

                        if (nomeItem == null || nomeItem.isBlank()) {
                            exibirAlerta(
                                    Alert.AlertType.ERROR,
                                    "Item inválido",
                                    "Informe o nome do item " + (i + 1) + "."
                            );
                            return;
                        }

                        nomeItem = nomeItem.trim();

                        if (nomeItem.length() > 150) {
                            exibirAlerta(
                                    Alert.AlertType.ERROR,
                                    "Item inválido",
                                    "O nome do item " + (i + 1)
                                            + " deve ter no máximo 150 caracteres."
                            );
                            return;
                        }

                        if (textoQuantidade == null
                                || textoQuantidade.isBlank()) {

                            exibirAlerta(
                                    Alert.AlertType.ERROR,
                                    "Quantidade obrigatória",
                                    "Informe a quantidade do item " + (i + 1) + "."
                            );
                            return;
                        }

                        BigDecimal quantidade = new BigDecimal(
                                textoQuantidade.trim().replace(",", ".")
                        );

                        if (quantidade.compareTo(BigDecimal.ZERO) <= 0) {
                            exibirAlerta(
                                    Alert.AlertType.ERROR,
                                    "Quantidade inválida",
                                    "A quantidade do item " + (i + 1)
                                            + " deve ser maior que zero."
                            );
                            return;
                        }

                        if (quantidade.precision() > 10
                                || quantidade.scale() > 2) {

                            exibirAlerta(
                                    Alert.AlertType.ERROR,
                                    "Quantidade inválida",
                                    "A quantidade do item " + (i + 1)
                                            + " aceita até 10 dígitos "
                                            + "e 2 casas decimais."
                            );
                            return;
                        }

                        if (!confirmarData(linha.dpValidade, true)) {
                            return;
                        }

                        ItemDoacao item = new ItemDoacao();

                        item.setNomeItem(nomeItem);
                        item.setQuantidade(quantidade);

                        item.setValidade(
                                linha.dpValidade.getValue()
                        );

                        itens.add(item);
                    }
                }
            }

            boolean material =
                    "MATERIAL".equalsIgnoreCase(tipo);

            if (material
                    && linhasItens.isEmpty()
                    && doacaoEmEdicao != null
                    && doacaoLegadaSemItens) {

                doacaoDAO.salvar(doacao);

            } else {

                doacaoDAO.salvar(doacao, itens);
            }

            exibirAlerta(
                    Alert.AlertType.INFORMATION,
                    "Sucesso",
                    "Doação salva com sucesso!"
            );

            limpar();

        } catch (NumberFormatException e) {

            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Valor ou quantidade inválida",
                    "Confira os números informados. "
                            + "Use ponto ou vírgula para separar "
                            + "as casas decimais."
            );

        } catch (Exception e) {

            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível salvar a doação: "
                            + e.getMessage()
            );
        }
    }

    @FXML
    private void limpar() {

        doacaoEmEdicao = null;
        doacaoLegadaSemItens = false;

        txtNomeDoador.clear();

        cbTipoDoacao.setValue(null);
        cbTipoDoacao.getSelectionModel().clearSelection();

        dpDataDoacao.setValue(LocalDate.now());

        txtValor.clear();
        txtDescricao.clear();

        linhasItens.clear();

        if (listaItens != null) {
            listaItens.getChildren().clear();
        }

        handleTipoChange();
    }

    @FXML
    private void verHistorico() {
        NavigationManager.getInstance()
                .navegarConteudo("historico-doacoes");
    }

    public void carregarDoacaoParaEdicao(Doacao doacao) {

        if (doacao == null) {
            return;
        }

        doacaoEmEdicao = doacao;

        txtNomeDoador.setText(doacao.getNomeDoador());

        cbTipoDoacao.setValue(doacao.getTipoDoacao());

        dpDataDoacao.setValue(doacao.getDataDoacao());

        txtDescricao.setText(doacao.getDescricao());

        if (doacao.getValor() != null) {
            txtValor.setText(
                    String.valueOf(doacao.getValor())
            );
        } else {
            txtValor.clear();
        }

        linhasItens.clear();
        listaItens.getChildren().clear();

        if ("MATERIAL".equalsIgnoreCase(doacao.getTipoDoacao())) {

            List<ItemDoacao> itens =
                    doacaoDAO.listarItensPorDoacao(doacao.getId());

            doacaoLegadaSemItens = itens.isEmpty();

            for (ItemDoacao item : itens) {

                criarLinhaItem(
                        item.getNomeItem(),
                        item.getQuantidade()
                                .stripTrailingZeros()
                                .toPlainString(),
                        item.getValidade()
                );
            }

        } else {
            doacaoLegadaSemItens = false;
        }

        handleTipoChange();
    }

    private void exibirAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensagem) {

        Alert alert = new Alert(tipo);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        alert.showAndWait();
    }
}
