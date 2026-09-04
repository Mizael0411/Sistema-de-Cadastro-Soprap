package com.abrigo.view;

import com.abrigo.model.Animal;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;

public class RegistrarVacinasController {
    
    @FXML
    private ComboBox<Animal> cbAnimal;

    @FXML
    private ComboBox<String> cbTipoVacina;

    @FXML
    private DatePicker dpDataAplicacao;

    @FXML
    private Button saveButton;

    @FXML
    private Button abrirHistoricoVacinas;

    @FXML
    public void initialize() {

    }

    @FXML 
    private void salvarVacina() {
        if(cbAnimal.getValue() == null) {
            mostrarAlerta(
                Alert.AlertType.WARNING,
                "Campo obrigatório",
                "Selecione um animal."
            );
            cbAnimal.requestFocus();
            return;
        }

        if(cbTipoVacina.getValue() == null) {
            mostrarAlerta(
                Alert.AlertType.WARNING,
                "Campo obrigatório",
                "Selecione o tipo de vacina."
            );
            cbTipoVacina.requestFocus();
            return;
        }

        if(dpDataAplicacao.getValue() == null) {
            mostrarAlerta(
                Alert.AlertType.WARNING,
                "Campo obrigatório",
                "Informe a data de aplicação."
            );
            dpDataAplicacao.requestFocus();
            return;
        }

        if(dpDataAplicacao.getValue().isAfter(java.time.LocalDate.now())) {
            mostrarAlerta(
                Alert.AlertType.WARNING,
                "Data inválida",
                "A data de aplicação não pode ser futura."
            );
            dpDataAplicacao.requestFocus();
            return;
        }

        mostrarAlerta(
            Alert.AlertType.INFORMATION,
            "Sucesso",
            "Dados da vacinação preenchidos corretamente."
        );
    }

    @FXML
    private void abrirHistorico() {
        NavigationManager.getInstance().navegarConteudo("historico-vacinas");
    }

    private void mostrarAlerta(
        Alert.AlertType tipo,
        String titulo,
        String mensagem) {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensagem);
            alert.showAndWait();
        }
}
