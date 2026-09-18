package com.abrigo.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.abrigo.dto.AdocaoDefinitivaDTO;
import com.abrigo.repository.AdocaoRepository;

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
import javafx.stage.Stage;

public class AdocoesDefinitivasController implements Initializable {

    @FXML private TextField txtBusca;
    @FXML private TableView<AdocaoDefinitivaDTO> tabelaAdocoes;
    @FXML private TableColumn<AdocaoDefinitivaDTO, Long> colunaIdAnimal;
    @FXML private TableColumn<AdocaoDefinitivaDTO, String> colunaNome;
    @FXML private TableColumn<AdocaoDefinitivaDTO, String> colunaStatus;
    @FXML private TableColumn<AdocaoDefinitivaDTO, String> colunaTutor;
    @FXML private TableColumn<AdocaoDefinitivaDTO, Long> colunaIdAdocao;

    private final AdocaoRepository adocaoRepository = new AdocaoRepository();
    private final ObservableList<AdocaoDefinitivaDTO> dados = FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colunaIdAnimal.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().idAnimal()));
        colunaNome.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nomeAnimal()));
        colunaStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().statusAdocao()));
        colunaTutor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nomeTutor()));
        colunaIdAdocao.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().idAdocao()));

        FilteredList<AdocaoDefinitivaDTO> filtrados = new FilteredList<>(dados, a -> true);
        txtBusca.textProperty().addListener((obs, oldV, newV) -> {
            String termo = newV == null ? "" : newV.trim().toLowerCase();
            filtrados.setPredicate(a ->
                    termo.isEmpty()
                            || a.nomeAnimal().toLowerCase().contains(termo)
                            || a.nomeTutor().toLowerCase().contains(termo)
                            || String.valueOf(a.idAnimal()).contains(termo));
        });

        SortedList<AdocaoDefinitivaDTO> ordenados = new SortedList<>(filtrados);
        ordenados.comparatorProperty().bind(tabelaAdocoes.comparatorProperty());
        tabelaAdocoes.setItems(ordenados);

        carregarDados();
    }

    private void carregarDados() {
        dados.setAll(adocaoRepository.findAdocoesDefinitivas());
    }

    @FXML
    private void cancelarAdocao() {
        AdocaoDefinitivaDTO selecionado = tabelaAdocoes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            new Alert(AlertType.WARNING, "Selecione uma adoção na tabela.").showAndWait();
            return;
        }
        Alert confirmacao = new Alert(AlertType.CONFIRMATION,
                "Cancelar a adoção de " + selecionado.nomeAnimal() + "? O animal volta para a lista de sem adoção.");
        confirmacao.showAndWait().filter(botao -> botao.getButtonData().isDefaultButton()).ifPresent(botao -> {
            adocaoRepository.cancelarAdocao(selecionado.idAdocao());
            carregarDados();
        });
    }

    @FXML
    private void voltar() {
        NavigationManager.getInstance().navegarConteudo("gestao-adoçao");
    }
}
