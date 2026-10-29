package com.abrigo.controller;

import java.util.Locale;
import java.util.Optional;

import com.abrigo.dao.LarTemporarioDAO;
import com.abrigo.model.LarTemp;
import com.abrigo.model.NavigationManager;
import com.abrigo.util.FormatadorTelefone;

import javafx.beans.property.SimpleStringProperty;
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

public class HistoricoLarTempController {

    @FXML private TableView<LarTemp> tblLares;

    @FXML private TableColumn<LarTemp, Long> colId;
    @FXML private TableColumn<LarTemp, String> colNome;
    @FXML private TableColumn<LarTemp, String> colTelefone;
    @FXML private TableColumn<LarTemp, Integer> colVagas;

    @FXML private TextField txtFiltroNome;

    @FXML private Label lblIdDetalhe;
    @FXML private Label lblNomeDetalhe;
    @FXML private Label lblEnderecoDetalhe;
    @FXML private Label lblTelefoneDetalhe;
    @FXML private Label lblCapacidadeDetalhe;
    @FXML private Label lblVagasDetalhe;
    @FXML private Label lblDoencasDetalhe;
    @FXML private Label lblDeficienciaDetalhe;

    private final LarTemporarioDAO larDAO = new LarTemporarioDAO();

    private final ObservableList<LarTemp> listaLares =
            FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabela();

        tblLares.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, selecionado) ->
                        mostrarDetalhes(selecionado));

        carregarDados();
    }

    private void configurarTabela() {
        colId.setCellValueFactory(
                new PropertyValueFactory<>("id"));

        colNome.setCellValueFactory(
                new PropertyValueFactory<>("nome"));

        colTelefone.setCellValueFactory(dados ->
                new SimpleStringProperty(
                        formatarTelefone(dados.getValue().getTelefone())));

        colVagas.setCellValueFactory(
                new PropertyValueFactory<>("vagasDisponiveis"));
    }

    private void carregarDados() {
        listaLares.setAll(larDAO.listarTodos());
        filtrar();
    }

    @FXML
    public void filtrar() {
        String termo = txtFiltroNome.getText() != null
                ? txtFiltroNome.getText().trim().toLowerCase(Locale.ROOT)
                : "";

        ObservableList<LarTemp> filtrados =
                FXCollections.observableArrayList();

        for (LarTemp lar : listaLares) {
            String nome = lar.getNome() != null
                    ? lar.getNome().toLowerCase(Locale.ROOT)
                    : "";

            if (nome.contains(termo)) {
                filtrados.add(lar);
            }
        }

        tblLares.getSelectionModel().clearSelection();
        tblLares.setItems(filtrados);
        mostrarDetalhes(null);
    }

    private void mostrarDetalhes(LarTemp lar) {
        if (lar == null) {
            lblIdDetalhe.setText("Nenhum lar selecionado");
            lblNomeDetalhe.setText("Selecione um lar na tabela");
            lblEnderecoDetalhe.setText("—");
            lblTelefoneDetalhe.setText("—");
            lblCapacidadeDetalhe.setText("—");
            lblVagasDetalhe.setText("—");
            lblDoencasDetalhe.setText(
                    "Doenças transmissíveis: —");
            lblDeficienciaDetalhe.setText(
                    "Animais com deficiência: —");
            return;
        }

        lblIdDetalhe.setText("Cadastro nº " + lar.getId());
        lblNomeDetalhe.setText(
                textoSeguro(lar.getNome(), "Nome não informado"));

        lblEnderecoDetalhe.setText(
                textoSeguro(lar.getEndereco(), "Endereço não informado"));

        lblTelefoneDetalhe.setText(
                formatarTelefone(lar.getTelefone()));

        lblCapacidadeDetalhe.setText(
                String.valueOf(lar.getCapacidadeMaxima()));

        lblVagasDetalhe.setText(
                String.valueOf(lar.getVagasDisponiveis()));

        lblDoencasDetalhe.setText(
                "Doenças transmissíveis: "
                + (Boolean.TRUE.equals(
                        lar.getAceitaDoencasTransmissiveis())
                        ? "Sim"
                        : "Não"));

        lblDeficienciaDetalhe.setText(
                "Animais com deficiência: "
                + (Boolean.TRUE.equals(
                        lar.getAceitaDeficiencia())
                        ? "Sim"
                        : "Não"));
    }

    private String formatarTelefone(String telefone) {
        if (telefone == null || telefone.isBlank()) {
            return "Telefone não informado";
        }

        return FormatadorTelefone.formatar(telefone);
    }

    private String textoSeguro(String valor, String padrao) {
        return valor == null || valor.isBlank()
                ? padrao
                : valor;
    }

    @FXML
    public void editarLar() {
        LarTemp selecionado = tblLares.getSelectionModel()
                .getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(
                    "Aviso",
                    "Selecione um lar na tabela para editar.",
                    Alert.AlertType.WARNING);
            return;
        }

        CadastroLarTempController.setLarParaEdicao(selecionado);

        NavigationManager.getInstance()
                .navegarConteudo("cadastro-lar-temp");
    }

    @FXML
    public void novoLar() {
        CadastroLarTempController.setLarParaEdicao(null);

        NavigationManager.getInstance()
                .navegarConteudo("cadastro-lar-temp");
    }

    @FXML
    public void excluirLar() {
        LarTemp selecionado = tblLares.getSelectionModel()
                .getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(
                    "Aviso",
                    "Selecione um lar na tabela para excluir.",
                    Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacao =
                new Alert(Alert.AlertType.CONFIRMATION);

        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText(
                "Tem certeza que deseja excluir o lar de "
                + selecionado.getNome() + "?");

        Optional<ButtonType> resultado = confirmacao.showAndWait();

        if (resultado.isPresent()
                && resultado.get() == ButtonType.OK) {

            if (larDAO.excluir(selecionado.getId())) {
                mostrarAlerta(
                        "Sucesso",
                        "Registro excluído com sucesso!",
                        Alert.AlertType.INFORMATION);

                carregarDados();
            } else {
                mostrarAlerta(
                        "Erro",
                        "Erro ao excluir o registro do banco de dados.",
                        Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void voltar() {
        NavigationManager.getInstance()
                .navegarConteudo("cadastro-lar-temp");
    }

    private void mostrarAlerta(
            String titulo,
            String mensagem,
            Alert.AlertType tipo) {

        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}
