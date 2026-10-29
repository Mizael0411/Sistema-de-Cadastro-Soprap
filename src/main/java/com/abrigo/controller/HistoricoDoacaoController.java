package com.abrigo.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import com.abrigo.dao.DoacaoDAO;
import com.abrigo.model.Doacao;
import com.abrigo.model.ItemDoacao;
import com.abrigo.model.NavigationManager;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

public class HistoricoDoacaoController {

    @FXML
    private TableView<Doacao> tabelaDoacoes;

    @FXML
    private TableColumn<Doacao, Integer> colId;

    @FXML
    private TableColumn<Doacao, String> colDoador;

    @FXML
    private TableColumn<Doacao, String> colTipo;

    @FXML
    private TableColumn<Doacao, Object> colData;

    @FXML
    private TableColumn<Doacao, String> colDetalhes;

    @FXML
    private TextField txtBusca;

    @FXML
    private Label lblIdDetalhe;

    @FXML
    private Label lblDoadorDetalhe;

    @FXML
    private Label lblTipoDetalhe;

    @FXML
    private Label lblDataDetalhe;

    @FXML
    private Label lblValorDetalhe;

    @FXML
    private Label lblDescricaoDetalhe;

    @FXML
    private Label lblItensDetalhe;

    @FXML
    private VBox boxValorDetalhe;

    private final DoacaoDAO doacaoDAO = new DoacaoDAO();

    private final NavigationManager navigationManager =
            NavigationManager.getInstance();

    private final ObservableList<Doacao> listaDoacoes =
            FXCollections.observableArrayList();

    private final DateTimeFormatter formatoData =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        configurarTabela();
        configurarSelecao();
        carregarDoacoes();
    }

    private void configurarTabela() {
        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colDoador.setCellValueFactory(
                new PropertyValueFactory<>("nomeDoador")
        );

        colTipo.setCellValueFactory(
                new PropertyValueFactory<>("tipoDoacao")
        );

        colData.setCellValueFactory(
                new PropertyValueFactory<>("dataDoacao")
        );

        colDetalhes.setCellValueFactory(
                new PropertyValueFactory<>("descricao")
        );
    }

    private void configurarSelecao() {
        tabelaDoacoes.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, selecionada) ->
                        mostrarDetalhes(selecionada)
                );
    }

    private void carregarDoacoes() {
        try {
            listaDoacoes.setAll(doacaoDAO.listarTodos());

            tabelaDoacoes.setItems(listaDoacoes);

            if (!listaDoacoes.isEmpty()) {
                tabelaDoacoes.getSelectionModel().selectFirst();
            } else {
                mostrarDetalhes(null);
            }

        } catch (Exception e) {
            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível carregar as doações.",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void filtrar() {
        String busca = txtBusca.getText() == null
                ? ""
                : txtBusca.getText()
                        .trim()
                        .toLowerCase(Locale.ROOT);

        Doacao selecionadaAnterior =
                tabelaDoacoes.getSelectionModel().getSelectedItem();

        ObservableList<Doacao> filtradas =
                FXCollections.observableArrayList();

        for (Doacao doacao : listaDoacoes) {
            String id = String.valueOf(doacao.getId());

            String doador = texto(doacao.getNomeDoador());

            String tipo = texto(doacao.getTipoDoacao());

            String descricao = texto(doacao.getDescricao());

            if (id.contains(busca)
                    || doador.toLowerCase(Locale.ROOT).contains(busca)
                    || tipo.toLowerCase(Locale.ROOT).contains(busca)
                    || descricao.toLowerCase(Locale.ROOT).contains(busca)) {

                filtradas.add(doacao);
            }
        }

        tabelaDoacoes.setItems(filtradas);

        if (selecionadaAnterior != null
                && filtradas.contains(selecionadaAnterior)) {

            tabelaDoacoes.getSelectionModel()
                    .select(selecionadaAnterior);

        } else if (!filtradas.isEmpty()) {

            tabelaDoacoes.getSelectionModel().selectFirst();

        } else {

            mostrarDetalhes(null);
        }
    }

    private void mostrarDetalhes(Doacao doacao) {

        if (doacao == null) {
            lblIdDetalhe.setText("Nenhuma doação selecionada");
            lblDoadorDetalhe.setText("Selecione uma doação na tabela");
            lblTipoDetalhe.setText("—");
            lblDataDetalhe.setText("—");
            lblValorDetalhe.setText("—");
            lblDescricaoDetalhe.setText("—");
            lblItensDetalhe.setText("—");

            boxValorDetalhe.setVisible(true);
            boxValorDetalhe.setManaged(true);

            return;
        }

        lblIdDetalhe.setText(
                "Doação ID: " + doacao.getId()
        );

        lblDoadorDetalhe.setText(
                textoOuTraco(doacao.getNomeDoador())
        );

        lblTipoDetalhe.setText(
                textoOuTraco(doacao.getTipoDoacao())
        );

        lblDataDetalhe.setText(
                formatarData(doacao.getDataDoacao())
        );

        lblDescricaoDetalhe.setText(
                textoOuTraco(doacao.getDescricao())
        );

        configurarValor(doacao);

        carregarItens(doacao);
    }

    private void configurarValor(Doacao doacao) {

        String tipo = texto(doacao.getTipoDoacao())
                .trim()
                .toLowerCase(Locale.ROOT);

        boolean doacaoMaterial = tipo.contains("material");

        boxValorDetalhe.setVisible(!doacaoMaterial);
        boxValorDetalhe.setManaged(!doacaoMaterial);

        if (!doacaoMaterial) {
            Object valor = doacao.getValor();

            lblValorDetalhe.setText(
                    valor == null ? "—" : "R$ " + valor
            );
        }
    }

    private void carregarItens(Doacao doacao) {

        try {
            List<ItemDoacao> itens =
                    doacaoDAO.listarItensPorDoacao(doacao.getId());

            if (itens == null || itens.isEmpty()) {
                lblItensDetalhe.setText(
                        "Nenhum produto cadastrado nesta doação."
                );
                return;
            }

            String textoItens = itens.stream()
                    .map(item -> {

                        String nome =
                                textoOuTraco(item.getNomeItem());

                        String quantidade =
                                String.valueOf(item.getQuantidade());

                        String validade =
                                descreverValidade(item.getValidade());

                        return nome
                                + "\nQuantidade: " + quantidade
                                + "\n" + validade;
                    })
                    .collect(Collectors.joining("\n\n"));

            lblItensDetalhe.setText(textoItens);

        } catch (Exception e) {
            lblItensDetalhe.setText(
                    "Não foi possível carregar os produtos desta doação."
            );
        }
    }

    private String descreverValidade(Object validade) {

        LocalDate dataValidade = converterParaLocalDate(validade);

        if (dataValidade == null) {
            return "Validade não informada.";
        }

        LocalDate hoje = LocalDate.now();

        long dias = ChronoUnit.DAYS.between(
                hoje,
                dataValidade
        );

        String data = dataValidade.format(formatoData);

        if (dias < 0) {
            long diasVencidos = Math.abs(dias);

            return "Vencido em " + data
                    + " (há " + diasVencidos
                    + (diasVencidos == 1 ? " dia)." : " dias).");
        }

        if (dias == 0) {
            return "Vence hoje (" + data + ").";
        }

        return "Dentro da validade: " + dias
                + (dias == 1 ? " dia restante" : " dias restantes")
                + " (vence em " + data + ").";
    }

    private LocalDate converterParaLocalDate(Object validade) {

        if (validade == null) {
            return null;
        }

        if (validade instanceof LocalDate) {
            return (LocalDate) validade;
        }

        if (validade instanceof java.sql.Date) {
            return ((java.sql.Date) validade).toLocalDate();
        }

        if (validade instanceof Date) {
            return new java.sql.Date(
                    ((Date) validade).getTime()
            ).toLocalDate();
        }

        String textoValidade = validade.toString().trim();

        try {
            return LocalDate.parse(textoValidade);

        } catch (DateTimeParseException e) {

            try {
                return LocalDate.parse(
                        textoValidade,
                        formatoData
                );

            } catch (DateTimeParseException ex) {
                return null;
            }
        }
    }

    private String formatarData(Object data) {

        if (data == null) {
            return "—";
        }

        if (data instanceof LocalDate) {
            return ((LocalDate) data).format(formatoData);
        }

        if (data instanceof java.sql.Date) {
            return ((java.sql.Date) data)
                    .toLocalDate()
                    .format(formatoData);
        }

        if (data instanceof Date) {
            SimpleDateFormat formato =
                    new SimpleDateFormat("dd/MM/yyyy");

            return formato.format((Date) data);
        }

        String textoData = data.toString().trim();

        try {
            return LocalDate.parse(textoData)
                    .format(formatoData);

        } catch (DateTimeParseException e) {
            return textoData;
        }
    }

    private String texto(Object valor) {
        return valor == null ? "" : valor.toString();
    }

    private String textoOuTraco(Object valor) {
        String resultado = texto(valor).trim();

        return resultado.isEmpty() ? "—" : resultado;
    }

    @FXML
    private void btnNovaDoacaoOnAction() {

        try {
            navigationManager.navegarConteudoComController(
                    "cadastro-doacoes"
            );

        } catch (Exception e) {
            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível abrir o cadastro de doações.",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void btnEditarOnAction() {

        Doacao selecionada =
                tabelaDoacoes.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            exibirAlerta(
                    Alert.AlertType.WARNING,
                    "Atenção",
                    "Selecione uma doação para editar.",
                    null
            );
            return;
        }

        try {
            navigationManager.setDoacaoParaEdicao(selecionada);

            navigationManager.navegarConteudoComController(
                    "cadastro-doacoes"
            );

        } catch (Exception e) {
            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível abrir a edição da doação.",
                    e.getMessage()
            );
        }
    }

    @FXML
    private void btnExcluirOnAction() {

        Doacao selecionada =
                tabelaDoacoes.getSelectionModel().getSelectedItem();

        if (selecionada == null) {
            exibirAlerta(
                    Alert.AlertType.WARNING,
                    "Atenção",
                    "Selecione uma doação para excluir.",
                    null
            );
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Excluir doação");

        confirmacao.setContentText(
                "Deseja realmente excluir a doação de "
                        + textoOuTraco(selecionada.getNomeDoador())
                        + "?"
        );

        confirmacao.showAndWait().ifPresent(resposta -> {

            if (resposta == ButtonType.OK) {

                try {
                    doacaoDAO.deletar(selecionada.getId());

                    carregarDoacoes();

                } catch (Exception e) {
                    exibirAlerta(
                            Alert.AlertType.ERROR,
                            "Erro",
                            "Não foi possível excluir a doação.",
                            e.getMessage()
                    );
                }
            }
        });
    }

    @FXML
    private void btnVoltarOnAction() {

        try {
            navigationManager.voltarMenu();

        } catch (Exception e) {
            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível voltar ao menu.",
                    e.getMessage()
            );
        }
    }

    private void exibirAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensagem,
            String detalhes) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(mensagem);

        if (detalhes != null && !detalhes.isBlank()) {
            alerta.setContentText(detalhes);
        }

        alerta.showAndWait();
    }
}
