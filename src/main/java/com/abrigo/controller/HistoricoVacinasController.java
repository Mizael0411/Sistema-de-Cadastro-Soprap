package com.abrigo.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.dao.VacinaDAO;
import com.abrigo.model.Animal;
import com.abrigo.model.Vacina;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistoricoVacinasController {

    @FXML private ComboBox<Animal> cbAnimal;
    @FXML private TableView<Vacina> tabelaHistorico;
    @FXML private TableColumn<Vacina, String> colunaVacina;
    @FXML private TableColumn<Vacina, String> colunaDose;
    @FXML private TableColumn<Vacina, String> colunaData;

    private final AnimalDAO animalDAO = new AnimalDAO();
    private final VacinaDAO vacinaDAO = new VacinaDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML 
    void initialize() {
        if (colunaVacina != null) {
            colunaVacina.setCellValueFactory(cellData -> 
                new SimpleStringProperty(cellData.getValue().getTipoVacina())
            );
        }

        if (colunaDose != null) {
            colunaDose.setCellValueFactory(cellData -> 
                new SimpleStringProperty(
                    cellData.getValue().getDose() != null && !cellData.getValue().getDose().isEmpty() 
                        ? cellData.getValue().getDose() 
                        : "Dose Única"
                )
            );
        }

        if (colunaData != null) {
            colunaData.setCellValueFactory(cellData -> 
                new SimpleStringProperty(
                    cellData.getValue().getDataVacinacao() != null 
                        ? cellData.getValue().getDataVacinacao().format(formatter) 
                        : ""
                )
            );
        }

        if (cbAnimal != null) {
            carregarAnimais();
            cbAnimal.valueProperty().addListener((obs, antigo, selecionado) -> {
                if (selecionado != null) {
                    carregarHistoricoDoAnimal(selecionado);
                } else if (tabelaHistorico != null) {
                    tabelaHistorico.getItems().clear();
                }
            });
        }
    }

    private void carregarAnimais() {
        List<Animal> animais = animalDAO.listarTodos();
        cbAnimal.setItems(FXCollections.observableArrayList(animais));
    }

    private void carregarHistoricoDoAnimal(Animal animal) {
        List<Vacina> vacinas = vacinaDAO.buscarPorAnimalId(animal.getId());

        if (vacinas.isEmpty() && animal.getDataUltimaVacinacao() != null) {
            Vacina vacinaOriginal = new Vacina();
            vacinaOriginal.setAnimal(animal);
            vacinaOriginal.setTipoVacina(
                animal.getStatusVacinacao() != null ? animal.getStatusVacinacao() : "Não informada"
            );
            vacinaOriginal.setDataVacinacao(animal.getDataUltimaVacinacao());
            vacinaOriginal.setDose("Cadastro Inicial");
            vacinas.add(vacinaOriginal);
        }

        if (tabelaHistorico != null) {
            tabelaHistorico.setItems(FXCollections.observableArrayList(vacinas));
        }
    }

    @FXML
    private void abrirNovoRegistro() {
        novaVacina();
    }

    @FXML
    private void novaVacina() {
        if (cbAnimal != null && cbAnimal.getValue() != null) {
            NavigationManager.getInstance().setAnimalParaVacinar(cbAnimal.getValue());
        }
        NavigationManager.getInstance().navegarConteudo("registrar-vacinas");
    }

    @FXML
    private void editarVacina() {
        if (tabelaHistorico == null) return;
        Vacina selecionada = tabelaHistorico.getSelectionModel().getSelectedItem();
        
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção necessária", "Selecione um registro na tabela para editar.");
            return;
        }

        if (selecionada.getId() == null) {
            mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Inicial", "Edite este registro na tela de 'Animais Cadastrados'.");
            return;
        }

        NavigationManager.getInstance().setVacinaParaEdicao(selecionada);
        NavigationManager.getInstance().navegarConteudo("registrar-vacinas");
    }

    @FXML
    private void excluirVacina() {
        if (tabelaHistorico == null) return;
        Vacina selecionada = tabelaHistorico.getSelectionModel().getSelectedItem();
        
        if (selecionada == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Seleção necessária", "Selecione um registro para excluir.");
            return;
        }

        if (selecionada.getId() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Registro Inicial", "Não é possível excluir o registro inicial por aqui.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Deseja realmente excluir este registro?");
        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            if (vacinaDAO.excluir(selecionada.getId())) {
                carregarHistoricoDoAnimal(cbAnimal.getValue());
            }
        }
    }

    @FXML 
    void voltar() {
        NavigationManager.getInstance().navegarConteudo("registrar-vacinas");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}