package com.abrigo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.dao.VacinaDAO;
import com.abrigo.model.Animal;
import com.abrigo.model.Vacina;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class HistoricoVacinasController {

    @FXML private ComboBox<Animal> cbAnimal;
    @FXML private TableView<Vacina> tabelaHistorico;
    @FXML private TableColumn<Vacina, Long> colunaId;
    @FXML private TableColumn<Vacina, String> colunaNomeAnimal;
    @FXML private TableColumn<Vacina, String> colunaVacina;
    @FXML private TableColumn<Vacina, String> colunaDose;
    @FXML private TableColumn<Vacina, LocalDate> colunaData;
    @FXML private TextField txtBusca;

    private final VacinaDAO vacinaDAO = new VacinaDAO();

    private final ObservableList<Vacina> todasVacinas = FXCollections.observableArrayList();
    private FilteredList<Vacina> vacinasFiltradas;

    @FXML
    public void initialize() {
        configurarColunas();
        configurarBusca();
        carregarTabelaAssincrono();
    }

    private void configurarColunas() {
        colunaId.setCellValueFactory(c ->
            new SimpleObjectProperty<>(c.getValue().getId()));

        colunaNomeAnimal.setCellValueFactory(c -> {
            if (c.getValue() != null && c.getValue().getAnimal() != null) {
                return new SimpleStringProperty(c.getValue().getAnimal().getNome());
            }
            return new SimpleStringProperty("");
        });

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
    }

    private void configurarBusca() {
        vacinasFiltradas = new FilteredList<>(todasVacinas, p -> true);
        tabelaHistorico.setItems(vacinasFiltradas);

        if (txtBusca != null) {
            txtBusca.textProperty().addListener((obs, oldV, newV) -> aplicarFiltro(newV));
        }
    }

    private void carregarAnimais() {
        try {
            cbAnimal.setItems(FXCollections.observableArrayList(animalDAO.listarTodos()));
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Conexão", "Falha ao carregar lista de animais: " + e.getMessage());
        }
        String t = termo.trim().toLowerCase();
        vacinasFiltradas.setPredicate(v -> {
            if (v.getId() != null && String.valueOf(v.getId()).contains(t)) return true;

            if (v.getAnimal() != null && v.getAnimal().getNome() != null && v.getAnimal().getNome().toLowerCase().contains(t)) return true;

            if (v.getTipoVacina() != null && v.getTipoVacina().toLowerCase().contains(t)) return true;

            if (v.getDose() != null && v.getDose().toLowerCase().contains(t)) return true;

            return false;
        });
    }

    private void carregarTabelaAssincrono() {
        Task<List<Vacina>> task = new Task<>() {
            @Override
            protected List<Vacina> call() {
                return vacinaDAO.listarTodos();
            }
        };

        task.setOnSucceeded(e -> {
            todasVacinas.setAll(task.getValue());
            aplicarFiltro(txtBusca != null ? txtBusca.getText() : null);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            if (ex != null) ex.printStackTrace();
            exibirAlerta(Alert.AlertType.ERROR, "Erro",
                    "Não foi possível carregar a lista de vacinas.\n" +
                            (ex != null ? ex.getMessage() : ""));
        });

        Thread t = new Thread(task, "carregar-vacinas");
        t.setDaemon(true);
        t.start();
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
                carregarTabelaAssincrono();
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