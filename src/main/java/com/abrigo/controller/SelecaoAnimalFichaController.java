package com.abrigo.controller;

import java.util.List;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.model.Animal;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class SelecaoAnimalFichaController {

    @FXML private TableView<Animal> tabelaAnimais;
    @FXML private TableColumn<Animal, Long> colId;
    @FXML private TableColumn<Animal, String> colNome;
    @FXML private TextField txtBusca;

    private final AnimalDAO animalDAO = new AnimalDAO();
    private final ObservableList<Animal> listaAnimais = FXCollections.observableArrayList();
    private FilteredList<Animal> animaisFiltrados;

    @FXML
    public void initialize() {
        configurarColunas();
        configurarBusca();
        configurarDuploClique();
        carregarAnimaisAssincrono();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colNome.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNome()));
    }

    private void configurarBusca() {
        animaisFiltrados = new FilteredList<>(listaAnimais, p -> true);
        tabelaAnimais.setItems(animaisFiltrados);

        if (txtBusca != null) {
            txtBusca.textProperty().addListener((obs, oldV, newV) -> {
                String termo = newV == null ? "" : newV.trim().toLowerCase();
                animaisFiltrados.setPredicate(a -> {
                    if (termo.isBlank()) return true;
                    if (a.getId() != null && String.valueOf(a.getId()).contains(termo)) return true;
                    if (a.getNome() != null && a.getNome().toLowerCase().contains(termo)) return true;
                    return false;
                });
            });
        }
    }

    private void configurarDuploClique() {
        tabelaAnimais.setRowFactory(tv -> {
            TableRow<Animal> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    abrirFicha(row.getItem());
                }
            });
            return row;
        });
    }

    @FXML
    private void btnVerFichaOnAction() {
        Animal selecionado = tabelaAnimais.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Seleção Necessária");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, selecione um animal na tabela.");
            alert.showAndWait();
            return;
        }
        abrirFicha(selecionado);
    }

    private void abrirFicha(Animal animal) {
        NavigationManager.getInstance().setAnimalSelecionado(animal);
        NavigationManager.getInstance().navegarConteudo("ficha-tecnica-animais");
    }

    private void carregarAnimaisAssincrono() {
        Task<List<Animal>> task = new Task<>() {
            @Override
            protected List<Animal> call() {
                return animalDAO.listarTodos();
            }
        };

        task.setOnSucceeded(e -> listaAnimais.setAll(task.getValue()));
        
        Thread t = new Thread(task, "carregar-selecao-animais");
        t.setDaemon(true);
        t.start();
    }

    @FXML
    private void btnVoltarOnAction() {
        NavigationManager.getInstance().voltarMenu();
    }
}