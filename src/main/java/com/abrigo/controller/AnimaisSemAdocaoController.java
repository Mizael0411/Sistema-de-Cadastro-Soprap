package com.abrigo.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.abrigo.dto.AnimalNaoAdotadoDTO;
import com.abrigo.repository.AdocaoRepository;
import com.abrigo.repository.AnimalRepository;
import com.abrigo.util.SelecionarLarTempDialog;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;

public class AnimaisSemAdocaoController implements Initializable {

    @FXML private TextField txtBusca;
    @FXML private TableView<AnimalNaoAdotadoDTO> tabelaAnimais;
    @FXML private TableColumn<AnimalNaoAdotadoDTO, Long> colunaIdAnimal;
    @FXML private TableColumn<AnimalNaoAdotadoDTO, String> colunaNome;
    @FXML private TableColumn<AnimalNaoAdotadoDTO, String> colunaStatus;

    private final AnimalRepository animalRepository = new AnimalRepository();
    private final AdocaoRepository adocaoRepository = new AdocaoRepository();

    private final ObservableList<AnimalNaoAdotadoDTO> dados = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colunaIdAnimal.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().idAnimal()));
        colunaNome.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nome()));
        colunaStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().statusAdocao()));

        FilteredList<AnimalNaoAdotadoDTO> filtrados = new FilteredList<>(dados, a -> true);
        txtBusca.textProperty().addListener((obs, oldV, newV) -> {
            String termo = newV == null ? "" : newV.trim().toLowerCase();
            filtrados.setPredicate(animal ->
                    termo.isEmpty()
                            || animal.nome().toLowerCase().contains(termo)
                            || String.valueOf(animal.idAnimal()).contains(termo));
        });

        SortedList<AnimalNaoAdotadoDTO> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tabelaAnimais.comparatorProperty());
        tabelaAnimais.setItems(ordenados);

        carregarDados();
    }

    private void carregarDados() {
        dados.setAll(animalRepository.findAnimaisSemAdocao());
    }

    @FXML
    private void adotarDefinitivo() {
        AnimalNaoAdotadoDTO selecionado = tabelaAnimais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(AlertType.WARNING, "Selecione um animal na tabela.").showAndWait();
            return;
        }
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Adoção Definitiva");
        dialog.setHeaderText("Adotar " + selecionado.nome());
        dialog.setContentText("Nome do tutor:");
        dialog.showAndWait().ifPresent(nomeTutor -> {
            if (nomeTutor.isBlank()) {
                return;
            }
            try {
                adocaoRepository.registrarAdocaoDefinitiva(selecionado.idAnimal(), nomeTutor);
            } catch (IllegalStateException e) {
                new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
            }
            carregarDados(); // recarrega de qualquer forma: se deu erro, tira a linha desatualizada da tela
        });
    }

    @FXML
    private void adotarLarTemporario() {
        AnimalNaoAdotadoDTO selecionado = tabelaAnimais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(AlertType.WARNING, "Selecione um animal na tabela.").showAndWait();
            return;
        }
        SelecionarLarTempDialog.abrir().ifPresent(larEscolhido -> {
            try {
                adocaoRepository.registrarAdocaoLarTemporario(
                        selecionado.idAnimal(), larEscolhido.getId(), larEscolhido.getNome());
                carregarDados();
            } catch (IllegalStateException e) {
                new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
            }
        });
    }

    @FXML
    private void voltar() {
        Stage stage = (Stage) tabelaAnimais.getScene().getWindow();
        stage.close(); // ajuste para a navegação que você já usa entre telas
    }
}