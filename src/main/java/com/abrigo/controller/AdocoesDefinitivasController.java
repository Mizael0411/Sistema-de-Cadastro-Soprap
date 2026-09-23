package com.abrigo.controller;

import java.util.List;

import com.abrigo.dto.AdocaoDefinitivaDTO;
import com.abrigo.repository.AdocaoRepository;
import com.abrigo.util.DestaqueTabelaUtil;

import javafx.animation.Animation;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class AdocoesDefinitivasController {

    @FXML private TextField txtBusca;
    @FXML private TableView<AdocaoDefinitivaDTO> tabelaAdocoes;
    @FXML private TableColumn<AdocaoDefinitivaDTO, Long> colunaIdAnimal;
    @FXML private TableColumn<AdocaoDefinitivaDTO, String> colunaNome;
    @FXML private TableColumn<AdocaoDefinitivaDTO, String> colunaStatus;
    @FXML private TableColumn<AdocaoDefinitivaDTO, String> colunaTutor;
    @FXML private TableColumn<AdocaoDefinitivaDTO, Long> colunaIdAdocao;

    private final AdocaoRepository adocaoRepository = new AdocaoRepository();
    private final ObservableList<AdocaoDefinitivaDTO> dados = FXCollections.observableArrayList();

    // Id do animal recém-transferido, se houver, pra destacar a linha em verde.
    private Long idAnimalParaDestacar;

    @FXML
    public void initialize() {
        configurarColunas();
        configurarBusca();
        DestaqueTabelaUtil.configurarRowFactory(tabelaAdocoes, () -> idAnimalParaDestacar);
        carregarDadosAssincrono();
    }


    public void destacarAnimalRecemAdicionado(Long idAnimal) {
        this.idAnimalParaDestacar = idAnimal;

        DestaqueTabelaUtil.selecionarEExibir(tabelaAdocoes, dados, idAnimalParaDestacar);
        tabelaAdocoes.refresh();
    }

    private void configurarColunas() {
        colunaIdAnimal.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().idAnimal()));
        colunaNome.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nomeAnimal()));
        colunaStatus.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().statusAdocao()));
        colunaTutor.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().nomeTutor()));
        colunaIdAdocao.setCellValueFactory(d -> new SimpleObjectProperty<>(d.getValue().idAdocao()));
    }

    private void configurarBusca() {
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
    }


    private void carregarDadosAssincrono() {
        tabelaAdocoes.setPlaceholder(criarPlaceholderCarregando());

        Task<List<AdocaoDefinitivaDTO>> task = new Task<>() {
            @Override
            protected List<AdocaoDefinitivaDTO> call() {
                return adocaoRepository.findAdocoesDefinitivas();
            }
        };

        task.setOnSucceeded(e -> {
            dados.setAll(task.getValue());
            tabelaAdocoes.setPlaceholder(criarPlaceholderVazio());

            if (idAnimalParaDestacar != null) {
                DestaqueTabelaUtil.selecionarEExibir(tabelaAdocoes, dados, idAnimalParaDestacar);
                PauseTransition apagarDestaque = new PauseTransition(Duration.seconds(4));
                apagarDestaque.setOnFinished(ev -> {
                    idAnimalParaDestacar = null;
                    tabelaAdocoes.refresh();
                });
                apagarDestaque.play();
            }
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
            tabelaAdocoes.setPlaceholder(criarPlaceholderVazio());
            mostrarAlerta(AlertType.ERROR, "Erro",
                    "Não foi possível carregar as adoções definitivas.\n" +
                            (ex != null ? ex.getMessage() : ""));
        });

        Thread t = new Thread(task, "carregar-adocoes-definitivas");
        t.setDaemon(true);
        t.start();
    }


    private Node criarPlaceholderCarregando() {
        Label pata = new Label("🐾");
        pata.getStyleClass().add("loading-paw");

        ScaleTransition pulso = new ScaleTransition(Duration.millis(650), pata);
        pulso.setFromX(0.85);
        pulso.setFromY(0.85);
        pulso.setToX(1.25);
        pulso.setToY(1.25);
        pulso.setCycleCount(Animation.INDEFINITE);
        pulso.setAutoReverse(true);
        pulso.play();

        Label texto = new Label("Farejando adoções...");
        texto.getStyleClass().add("loading-text");

        VBox box = new VBox(10, pata, texto);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("loading-placeholder");
        return box;
    }

    private Node criarPlaceholderVazio() {
        Label pata = new Label("🐾");
        pata.getStyleClass().add("empty-paw");

        Label texto = new Label("Nenhuma adoção definitiva encontrada.");
        texto.getStyleClass().add("loading-text");

        VBox box = new VBox(10, pata, texto);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("loading-placeholder");
        return box;
    }

    @FXML
    private void cancelarAdocao() {
        AdocaoDefinitivaDTO selecionado = tabelaAdocoes.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(AlertType.WARNING, "Seleção Necessária", "Selecione uma adoção na tabela.");
            return;
        }
        Alert confirmacao = new Alert(AlertType.CONFIRMATION,
                "Cancelar a adoção de " + selecionado.nomeAnimal() + "? O animal volta para a lista de sem adoção.");
        confirmacao.setHeaderText(null);
        confirmacao.showAndWait().filter(botao -> botao.getButtonData().isDefaultButton()).ifPresent(botao -> {
            adocaoRepository.cancelarAdocao(selecionado.idAdocao());
            carregarDadosAssincrono();
        });
    }

    @FXML
    private void voltar() {
        NavigationManager.getInstance().navegarConteudo("gestao-adocao");
    }

    private void mostrarAlerta(AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}