package com.abrigo.controller;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.dao.VacinaDAO;
import com.abrigo.model.Animal;
import com.abrigo.model.Vacina;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

public class RegistrarVacinasController {

    @FXML private ComboBox<Animal> cbAnimal;
    @FXML private ComboBox<String> cbTipoVacina; // Alterado de TextField para ComboBox
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

        cbTipoVacina.setItems(FXCollections.observableArrayList(
            "Raiva", "Virose", "Raiva e Virose"
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
        
        cbTipoVacina.setValue(vacina.getTipoVacina());
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
        
        String tipoVacina = cbTipoVacina.getValue();
        if (tipoVacina == null || (!tipoVacina.equalsIgnoreCase("Raiva") &&
                !tipoVacina.equalsIgnoreCase("Virose") &&
                !tipoVacina.equalsIgnoreCase("Raiva e Virose"))) {
            mostrarAlerta("Validação", "Selecione um tipo válido de vacina (Raiva, Virose ou Raiva e Virose).");
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
        Animal animalSelecionado = cbAnimal.getValue();

        vacina.setAnimal(animalSelecionado);
        vacina.setTipoVacina(tipoVacina);
        vacina.setDose(cbDose.getValue());
        vacina.setDataVacinacao(dpDataVacinacao.getValue());

        boolean sucesso;
        if (vacina.getId() == null) {
            sucesso = vacinaDAO.salvar(vacina);
        } else {
            sucesso = vacinaDAO.atualizar(vacina);
        }

        if (sucesso) {

            animalSelecionado.setStatusVacinacao("Vacinado (" + tipoVacina + ")");
            animalDAO.atualizar(animalSelecionado);

            mostrarAlerta("Sucesso", "Registro de vacina salvo e status do animal atualizado!");
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