package com.abrigo.view;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import java.time.LocalDate;

/**
 * Controller responsável pela tela de cadastro de animais.
 * Camada: Controller
 * Arquivo Relacionado: cadastroAnimal.fxml
 * @author Mizael
 */
public class CadastroAnimalController {

    @FXML private TextField txtNome;
    @FXML private TextField txtIdade;
    @FXML private ComboBox<String> cbSexo;
    @FXML private DatePicker dpDataNascimento;
    @FXML private ComboBox<String> cbGravida;
    @FXML private ComboBox<String> cbVacina;
    @FXML private DatePicker dpUltimaConsulta;
    @FXML private Button btnSalvar;
    @FXML private Button btnLimpar;

    // --- Métodos para capturar os valores dos campos ---

    public String getNome() {
        return txtNome.getText();
    }

    public String getIdade() {
        return txtIdade.getText();
    }

    public String getSexo() {
        return cbSexo.getValue();
    }

    public LocalDate getDataNascimento() {
        return dpDataNascimento.getValue();
    }
    

    public String getStatusGravidez() {
        return cbGravida.getValue();
    }

    public String getStatusVacinacao() {
        return cbVacina.getValue();
    }

    public LocalDate getDataUltimaConsulta() {
        return dpUltimaConsulta.getValue();
    }
}