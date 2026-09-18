package com.abrigo.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.abrigo.dto.AnimalNaoAdotadoDTO;
import com.abrigo.repository.AdocaoRepository;
import com.abrigo.repository.AnimalRepository;

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
            adocaoRepository.registrarAdocaoDefinitiva(selecionado.idAnimal(), nomeTutor);
            carregarDados();
        });
    }

    @FXML
    private void adotarLarTemporario() {
        AnimalNaoAdotadoDTO selecionado = tabelaAnimais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(AlertType.WARNING, "Selecione um animal na tabela.").showAndWait();
            return;
        }
        TextInputDialog dialogTutor = new TextInputDialog();
        dialogTutor.setTitle("Adoção em Lar Temporário");
        dialogTutor.setHeaderText("Adotar " + selecionado.nome());
        dialogTutor.setContentText("Nome do tutor:");
        dialogTutor.showAndWait().ifPresent(nomeTutor -> {
            if (nomeTutor.isBlank()) {
                return;
            }
            // TODO: trocar por uma tela/combo real de seleção de LarTemp existente.
            TextInputDialog dialogLar = new TextInputDialog();
            dialogLar.setTitle("Lar Temporário");
            dialogLar.setContentText("ID do lar temporário:");
            dialogLar.showAndWait().ifPresent(idLarTexto -> {
                try {
                    Long idLarTemp = Long.valueOf(idLarTexto.trim());
                    adocaoRepository.registrarAdocaoLarTemporario(selecionado.idAnimal(), idLarTemp, nomeTutor);
                    carregarDados();
                } catch (NumberFormatException e) {
                    new Alert(AlertType.ERROR, "ID de lar temporário inválido.").showAndWait();
                }
            });
        });
    }

    @FXML
    private void voltar() {
        NavigationManager.getInstance().navegarConteudo("gestao-adoçao");
    }
}
