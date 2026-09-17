package com.abrigo.controller;

import java.time.LocalDate;
import com.abrigo.dao.AnimalDAO;
import com.abrigo.dao.VacinaDAO;
import com.abrigo.model.Animal;
import com.abrigo.model.Vacina;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class RegistrarVacinasController {

    @FXML private ComboBox<Animal> cbAnimal;
    @FXML private TextField txtTipoVacina;
    @FXML private ComboBox<String> cbDose;
    @FXML private DatePicker dpDataVacinacao;

    private final VacinaDAO vacinaDAO = new VacinaDAO();
    private final AnimalDAO animalDAO = new AnimalDAO();
    private Vacina vacinaEmEdicao;

    @FXML
    public void initialize() {
        cbDose.setItems(FXCollections.observableArrayList(
            "1ª Dose", "2ª Dose", "3ª Dose", "Reforço Anual", "Dose Única"
        ));

        cbAnimal.setItems(FXCollections.observableArrayList(animalDAO.listarTodos()));

        Animal animalParaVacinar = NavigationManager.getInstance().getAnimalParaVacinar();
        if (animalParaVacinar != null) {
            cbAnimal.setValue(animalParaVacinar);
        }

        Vacina edicao = NavigationManager.getInstance().getVacinaParaEdicao();
        if (edicao != null) {
            preencherCamposEdicao(edicao);
        }
    }

    private void preencherCamposEdicao(Vacina vacina) {
        this.vacinaEmEdicao = vacina;
        if (vacina.getAnimal() != null) {
            cbAnimal.setValue(vacina.getAnimal());
        }
        txtTipoVacina.setText(vacina.getTipoVacina());
        dpDataVacinacao.setValue(vacina.getDataVacinacao());

        if (vacina.getDose() != null && !vacina.getDose().isEmpty()) {
            if (!cbDose.getItems().contains(vacina.getDose())) {
                cbDose.getItems().add(vacina.getDose());
            }
            cbDose.setValue(vacina.getDose());
        }
    }

    @FXML
    private void salvar() {
        if (cbAnimal.getValue() == null) {
            mostrarAlerta("Validação", "Selecione um animal.");
            return;
        }
        if (txtTipoVacina.getText() == null || txtTipoVacina.getText().trim().isEmpty()) {
            mostrarAlerta("Validação", "Informe o tipo da vacina.");
            return;
        }
        if (cbDose.getValue() == null || cbDose.getValue().trim().isEmpty()) {
            mostrarAlerta("Validação", "Selecione a dose da vacina.");
            return;
        }
        if (dpDataVacinacao.getValue() == null) {
            mostrarAlerta("Validação", "Selecione a data de vacinação.");
            return;
        }

        Vacina vacina = (vacinaEmEdicao != null) ? vacinaEmEdicao : new Vacina();
        vacina.setAnimal(cbAnimal.getValue());
        vacina.setTipoVacina(txtTipoVacina.getText().trim());
        vacina.setDose(cbDose.getValue());
        vacina.setDataVacinacao(dpDataVacinacao.getValue());

        boolean sucesso;
        if (vacina.getId() == null) {
            sucesso = vacinaDAO.salvar(vacina);
        } else {
            sucesso = vacinaDAO.atualizar(vacina);
        }

        if (sucesso) {
            mostrarAlerta("Sucesso", "Registro de vacina salvo com sucesso!");
            abrirHistorico();
        } else {
            mostrarAlerta("Erro", "Não foi possível salvar o registro da vacina.");
        }
    }

    @FXML
    private void abrirHistorico() {
        NavigationManager.getInstance().navegarConteudo("historico-vacinas");
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}