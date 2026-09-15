package com.abrigo.view;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

/**
 * Controller responsável pelo cadastro de lar temporário.
 * Camada: Controller
 * Arquivo Relacionado: cadastroLarTemp.fxml
 */
public class CadastroLarTempController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEndereco;
    @FXML private TextField txtTelefone;
    @FXML private Spinner<Integer> spCapacidade;
    @FXML private Spinner<Integer> spDisponibilidadeVagas;
    @FXML private ComboBox<String> cbAceitaDoencas;
    @FXML private Button btnSalvar;
    @FXML private Button btnLimpar;

    @FXML
    public void initialize() {
        if (cbAceitaDoencas != null) {
            cbAceitaDoencas.setItems(FXCollections.observableArrayList("Sim", "Não"));
        }

        if (spCapacidade != null) {
            spCapacidade.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 50, 0));
        }

        if (spDisponibilidadeVagas != null) {
            spDisponibilidadeVagas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        }
    }

    public String getNome() {
        return txtNome.getText();
    }

    public String getEndereco() {
        return txtEndereco.getText();
    }

    public String getTelefone() {
        return txtTelefone.getText();
    }

    public Integer getCapacidade() {
        return spCapacidade != null ? spCapacidade.getValue() : 0;
    }

    public Integer getDisponibilidadeVagas() {
        return spDisponibilidadeVagas != null ? spDisponibilidadeVagas.getValue() : 0;
    }

    public String getAceitaDoencas() {
        return cbAceitaDoencas != null ? cbAceitaDoencas.getValue() : null;
    }

    @FXML
    private void salvarLarTemporario() {
        salvar();
    }

    @FXML
    private void salvar() {
        if (txtNome.getText() == null || txtNome.getText().isBlank()
                || txtEndereco.getText() == null || txtEndereco.getText().isBlank()
                || txtTelefone.getText() == null || txtTelefone.getText().isBlank()
                || cbAceitaDoencas.getValue() == null) {
            mostrarAlerta("Erro", "Preencha todos os campos antes de salvar.");
            return;
        }

        mostrarAlerta("Sucesso", "Lar temporário cadastrado com sucesso!");
        limparCampos();
    }

    @FXML
    private void limpar() {
        limparCampos();
    }

    @FXML
    private void limparCampos() {
        if (txtNome != null) txtNome.clear();
        if (txtEndereco != null) txtEndereco.clear();
        if (txtTelefone != null) txtTelefone.clear();
        if (spCapacidade != null && spCapacidade.getValueFactory() != null) {
            spCapacidade.getValueFactory().setValue(0);
        }
        if (spDisponibilidadeVagas != null && spDisponibilidadeVagas.getValueFactory() != null) {
            spDisponibilidadeVagas.getValueFactory().setValue(0);
        }
        if (cbAceitaDoencas != null) cbAceitaDoencas.setValue(null);
    }

    @FXML
    private void verHistorico() {
        NavigationManager.getInstance().navegarConteudo("lares-cadastrados");
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }
}