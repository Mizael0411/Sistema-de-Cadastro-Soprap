package com.abrigo.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.abrigo.dto.AnimalNaoAdotadoDTO;
import com.abrigo.model.NavigationManager;
import com.abrigo.repository.AdocaoRepository;
import com.abrigo.repository.AnimalRepository;
import com.abrigo.util.FormatadorTelefone;
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

public class AnimaisSemAdocaoController implements Initializable {

    @FXML
    private TextField txtBusca;

    @FXML
    private TableView<AnimalNaoAdotadoDTO> tabelaAnimais;

    @FXML
    private TableColumn<AnimalNaoAdotadoDTO, Long> colunaIdAnimal;

    @FXML
    private TableColumn<AnimalNaoAdotadoDTO, String> colunaNome;

    @FXML
    private TableColumn<AnimalNaoAdotadoDTO, String> colunaStatus;

    private final AnimalRepository animalRepository = new AnimalRepository();
    private final AdocaoRepository adocaoRepository = new AdocaoRepository();

    private final ObservableList<AnimalNaoAdotadoDTO> dados =
            FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colunaIdAnimal.setCellValueFactory(
                d -> new SimpleObjectProperty<>(d.getValue().idAnimal()));

        colunaNome.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().nome()));

        colunaStatus.setCellValueFactory(
                d -> new SimpleStringProperty(d.getValue().statusAdocao()));

        FilteredList<AnimalNaoAdotadoDTO> filtrados =
                new FilteredList<>(dados, a -> true);

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
        AnimalNaoAdotadoDTO selecionado =
                tabelaAnimais.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            new Alert(AlertType.WARNING,
                    "Selecione um animal na tabela.").showAndWait();
            return;
        }

        if (adocaoRepository.possuiLarTemporarioAtivo(selecionado.idAnimal())) {
            new Alert(AlertType.WARNING,
                    selecionado.nome() + " está em um lar temporário e não "
                            + "pode ser adotado definitivamente. "
                            + "Cancele o lar temporário antes de continuar.")
                    .showAndWait();
            return;
        }

        if (adocaoRepository.possuiAdocaoDefinitivaAtiva(selecionado.idAnimal())) {
            new Alert(AlertType.WARNING,
                    selecionado.nome() + " já foi adotado definitivamente.")
                    .showAndWait();
            return;
        }

        TextInputDialog dialogNome = new TextInputDialog();
        dialogNome.setTitle("Adoção Definitiva");
        dialogNome.setHeaderText("Adotar " + selecionado.nome());
        dialogNome.setContentText("Nome do tutor:");

        dialogNome.showAndWait().ifPresent(nomeTutor -> {
            if (nomeTutor.isBlank()) {
                new Alert(AlertType.WARNING,
                        "Informe o nome do tutor.").showAndWait();
                return;
            }

            TextInputDialog dialogTelefone = new TextInputDialog();
            dialogTelefone.setTitle("Adoção Definitiva");
            dialogTelefone.setHeaderText("Contato do tutor");
            dialogTelefone.setContentText(
                    "Telefone do tutor: (XX) XXXX-XXXX ou (XX) 9XXXX-XXXX");

            dialogTelefone.getEditor().setTextFormatter(
                    FormatadorTelefone.criarTextFormatter());

            dialogTelefone.showAndWait().ifPresent(telefoneTutor -> {
                if (telefoneTutor.isBlank()) {
                    new Alert(AlertType.WARNING,
                            "Informe o telefone do tutor.").showAndWait();
                    return;
                }

                if (!FormatadorTelefone.telefoneValido(telefoneTutor)) {
                    new Alert(AlertType.WARNING,
                            "Informe um telefone válido com DDD: "
                                    + "10 dígitos para telefone fixo ou "
                                    + "11 dígitos para celular.")
                            .showAndWait();
                    return;
                }

                try {
                    adocaoRepository.registrarAdocaoDefinitiva(
                            selecionado.idAnimal(),
                            nomeTutor.trim(),
                            FormatadorTelefone.somenteDigitos(telefoneTutor));

                    new Alert(AlertType.INFORMATION,
                            "Adoção registrada com sucesso!").showAndWait();

                } catch (IllegalStateException e) {
                    new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
                } catch (RuntimeException e) {
                    new Alert(AlertType.ERROR,
                            "Não foi possível registrar a adoção.").showAndWait();
                    e.printStackTrace();
                }

                carregarDados();
            });
        });
    }

    @FXML
    private void adotarLarTemporario() {
        AnimalNaoAdotadoDTO selecionado =
                tabelaAnimais.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            new Alert(AlertType.WARNING,
                    "Selecione um animal na tabela.").showAndWait();
            return;
        }

        if (adocaoRepository.possuiAdocaoAtiva(selecionado.idAnimal())) {
            new Alert(AlertType.WARNING,
                    selecionado.nome() + " já possui um registro de adoção "
                            + "ativo (lar temporário ou definitiva).")
                    .showAndWait();
            return;
        }

        SelecionarLarTempDialog.abrir().ifPresent(larEscolhido -> {
            try {
                adocaoRepository.registrarAdocaoLarTemporario(
                        selecionado.idAnimal(),
                        larEscolhido.getId(),
                        larEscolhido.getNome());

                carregarDados();

            } catch (IllegalStateException e) {
                new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
            } catch (RuntimeException e) {
                new Alert(AlertType.ERROR,
                        "Não foi possível registrar a adoção.").showAndWait();
                e.printStackTrace();
            }
        });
    }

    @FXML
    private void voltar() {
        NavigationManager.getInstance().navegarConteudo("gestao-adocao");
    }
}