package com.abrigo.controller;

import com.abrigo.dao.LarTemporarioDAO;
import com.abrigo.model.LarTemp;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Optional;

public class HistoricoLarTempController {

    @FXML private TableView<LarTemp> tblLares;
    @FXML private TableColumn<LarTemp, Long> colId;
    @FXML private TableColumn<LarTemp, String> colNome;
    @FXML private TableColumn<LarTemp, String> colEndereco;
    @FXML private TableColumn<LarTemp, String> colTelefone;
    @FXML private TableColumn<LarTemp, Integer> colCapacidade;
    @FXML private TableColumn<LarTemp, Integer> colVagas;
    @FXML private TableColumn<LarTemp, String> colAceitaDoencas;
    @FXML private TableColumn<LarTemp, String> colAceitaDeficiencia;

    @FXML private TextField txtFiltroNome;

    private final LarTemporarioDAO larDAO = new LarTemporarioDAO();
    private final ObservableList<LarTemp> listaLares = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        configurarTabela();
        carregarDados();
    }

    private void configurarTabela() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colEndereco.setCellValueFactory(new PropertyValueFactory<>("endereco"));
        colTelefone.setCellValueFactory(new PropertyValueFactory<>("telefone"));
        colCapacidade.setCellValueFactory(new PropertyValueFactory<>("capacidadeMaxima"));
        colVagas.setCellValueFactory(new PropertyValueFactory<>("vagasDisponiveis"));
        
        colAceitaDoencas.setCellValueFactory(cellData -> {
            Boolean aceita = cellData.getValue().getAceitaDoencasTransmissiveis();
            return new SimpleStringProperty(aceita != null && aceita ? "Sim" : "Não");
        });

        colAceitaDeficiencia.setCellValueFactory(cellData -> {
            Boolean aceita = cellData.getValue().getAceitaDeficiencia();
            return new SimpleStringProperty(aceita != null && aceita ? "Sim" : "Não");
        });
    }

    private void carregarDados() {
        listaLares.clear();
        listaLares.addAll(larDAO.listarTodos());
        tblLares.setItems(listaLares);
    }

    @FXML
    public void filtrar() {
        String termo = txtFiltroNome.getText() != null ? txtFiltroNome.getText().toLowerCase().trim() : "";
        
        if (termo.isEmpty()) {
            tblLares.setItems(listaLares);
        } else {
            ObservableList<LarTemp> filtrados = FXCollections.observableArrayList();
            for (LarTemp lar : listaLares) {
                if (lar.getNome() != null && lar.getNome().toLowerCase().contains(termo)) {
                    filtrados.add(lar);
                }
            }
            tblLares.setItems(filtrados);
        }
    }

    @FXML
    public void editarLar() {
        LarTemp selecionado = tblLares.getSelectionModel().getSelectedItem();
        
        if (selecionado == null) {
            mostrarAlerta("Aviso", "Selecione um lar temporário na tabela para editar.", Alert.AlertType.WARNING);
            return;
        }

        CadastroLarTempController.setLarParaEdicao(selecionado);
        NavigationManager.getInstance().navegarConteudo("cadastro-lar-temp");
    }

    @FXML
    public void novoLar() {
        CadastroLarTempController.setLarParaEdicao(null);
        NavigationManager.getInstance().navegarConteudo("cadastro-lar-temp");
    }

    @FXML
    public void excluirLar() {
        LarTemp selecionado = tblLares.getSelectionModel().getSelectedItem();
        
        if (selecionado == null) {
            mostrarAlerta("Aviso", "Selecione um lar temporário na tabela para excluir.", Alert.AlertType.WARNING);
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacao.setTitle("Confirmar Exclusão");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText("Tem certeza que deseja excluir o lar de " + selecionado.getNome() + "?");

        Optional<ButtonType> resultado = confirmacao.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (larDAO.excluir(selecionado.getId())) {
                mostrarAlerta("Sucesso", "Registro excluído com sucesso!", Alert.AlertType.INFORMATION);
                carregarDados();
            } else {
                mostrarAlerta("Erro", "Erro ao excluir o registro do banco de dados.", Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    public void voltar() {
        NavigationManager.getInstance().navegarConteudo("cadastro-lar-temp");
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}