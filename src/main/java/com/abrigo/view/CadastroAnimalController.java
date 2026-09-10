package com.abrigo.view;

import java.time.LocalDate;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.model.Animal;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class CadastroAnimalController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtIdade;

    @FXML
    private ComboBox<String> cbSexo;

    @FXML
    private DatePicker dpDataNascimento;

    @FXML
    private ComboBox<String> cbGravida;

    @FXML
    private ComboBox<String> cbVacina;

    @FXML
    private DatePicker dpUltimaConsulta;

    @FXML
    private void salvar() {
        String nome = txtNome.getText().trim();
        String idadeTexto = txtIdade.getText().trim();
        String sexo = cbSexo.getValue();
        LocalDate dataNascimento = dpDataNascimento.getValue();
        String statusGravidez = cbGravida.getValue();
        String statusVacina = cbVacina.getValue();
        LocalDate ultimaConsulta = dpUltimaConsulta.getValue();

        if (nome.isEmpty() || idadeTexto.isEmpty() || sexo == null
                || dataNascimento == null || statusGravidez == null
                || statusVacina == null || ultimaConsulta == null) {

            mostrarAlerta("Erro", "Preencha todos os campos.");
            return;
        }

        try {
            int idade = Integer.parseInt(idadeTexto);

            if (idade < 0) {
               mostrarAlerta("Erro", "A idade não pode ser negativa.");
               return;
            }

            Animal animal = new Animal();
            animal.setNome(nome);
            animal.setIdade(idade);
            animal.setDataNascimento(java.sql.Date.valueOf(dataNascimento));
            animal.setSexo(sexo);
            animal.setStatusVacinacao(statusVacina);
            animal.setStatusGravidez(statusGravidez);
            animal.setData_ultima_vacinacao(java.sql.Date.valueOf(ultimaConsulta));

            AnimalDAO animalDAO = new AnimalDAO();

            if (animalDAO.salvar(animal)) {
               mostrarAlerta("Sucesso", "Animal cadastrado com sucesso!");
               limpar();
            } else {
               mostrarAlerta("Erro", "Não foi possível cadastrar o animal.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "A idade deve ser um número inteiro.");
        }
    }
    @FXML
    private void atualizarStatusGravidez() {
        String sexo = cbSexo.getValue();

        if ("Macho".equals(sexo)) {
            cbGravida.setValue("Não se aplica");
            cbGravida.setDisable(true);

        } else if ("Fêmea".equals(sexo)) {
            cbGravida.setDisable(false);

            cbGravida.getItems().setAll(
                    "Não grávida",
                    "Grávida",
                    "Não se aplica"
            );

            cbGravida.setValue(null);
        }
    }
    @FXML
    private void limpar() {
        txtNome.clear();
        txtIdade.clear();
        cbSexo.setValue(null);
        dpDataNascimento.setValue(null);
        cbGravida.setDisable(false);
        cbGravida.getItems().setAll(
                "Não grávida",
                "Grávida",
                "Não se aplica"
        );
        cbGravida.setValue(null);

        cbVacina.setValue(null);
        dpUltimaConsulta.setValue(null);
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}