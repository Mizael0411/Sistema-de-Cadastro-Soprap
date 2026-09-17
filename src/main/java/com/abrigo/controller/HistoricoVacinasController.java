package com.abrigo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.dao.VacinaDAO;
import com.abrigo.model.Animal;
import com.abrigo.model.Vacina;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoricoVacinasController {

    @FXML private ComboBox<Animal> cbAnimal;
    @FXML private TableView<Vacina> tabelaHistorico;
    @FXML private TableColumn<Vacina, String> colunaVacina;
    @FXML private TableColumn<Vacina, String> colunaDose;
    @FXML private TableColumn<Vacina, LocalDate> colunaData;

    private final VacinaDAO vacinaDAO = new VacinaDAO();
    private final AnimalDAO animalDAO = new AnimalDAO();

    @FXML
    public void initialize() {
        // Mapeamento das colunas conforme IDs do FXML
        colunaVacina.setCellValueFactory(new PropertyValueFactory<>("tipoVacina"));
        colunaDose.setCellValueFactory(new PropertyValueFactory<>("dose"));
        colunaData.setCellValueFactory(new PropertyValueFactory<>("dataVacinacao"));

        // Formatação da Data (dd/MM/yyyy)
        colunaData.setCellFactory(tc -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : formatter.format(value));
            }
        });

        carregarAnimais();

        // Evento de seleção no ComboBox para filtrar a tabela por animal
        cbAnimal.getSelectionModel().selectedItemProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                carregarVacinasDoAnimal(selecionado.getId());
            } else {
                tabelaHistorico.getItems().clear();
            }
        });
    }

    private void carregarAnimais() {
        try {
            cbAnimal.setItems(FXCollections.observableArrayList(animalDAO.listarTodos()));
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Conexão", "Falha ao carregar lista de animais: " + e.getMessage());
        }
    }

    private void carregarVacinasDoAnimal(Object animalId) {
        try {
            ObservableList<Vacina> lista = FXCollections.observableArrayList(vacinaDAO.buscarPorAnimalId(animalId));
            tabelaHistorico.setItems(lista);
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Conexão", "Não foi possível carregar o histórico: " + e.getMessage());
        }
    }

    @FXML
    public void abrirNovoRegistro() {
        NavigationManager.getInstance().navegarConteudo("registrar-vacinas");
    }

    @FXML
    public void editarVacina() {
        Vacina selecionada = tabelaHistorico.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Pendente", "Selecione uma vacina na tabela para editar.");
            return;
        }

        NavigationManager.getInstance().setVacinaParaEdicao(selecionada);
        NavigationManager.getInstance().navegarConteudo("registrar-vacinas");
    }

    @FXML
    public void excluirVacina() {
        Vacina selecionada = tabelaHistorico.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Pendente", "Selecione uma vacina na tabela para excluir.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja realmente excluir a vacina selecionada?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            if (vacinaDAO.excluir(selecionada.getId())) {
                carregarVacinasDoAnimal(cbAnimal.getValue().getId());
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Registro de vacina excluído com sucesso.");
            } else {
                exibirAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao excluir o registro de vacina.");
            }
        }
    }

    @FXML
    public void voltar() {
        NavigationManager.getInstance().navegarConteudo("registrar-vacinas");
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}