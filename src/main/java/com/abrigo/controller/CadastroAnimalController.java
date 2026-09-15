package com.abrigo.controller;

import java.time.LocalDate;
import java.time.Period;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.model.Animal;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class CadastroAnimalController {

    @FXML private TextField txtNome;
    @FXML private TextField txtIdade;
    @FXML private DatePicker dpDataNascimento;
    @FXML private ComboBox<String> cbSexo;
    @FXML private ComboBox<String> cbGravida;
    @FXML private ComboBox<String> cbVacina;
    @FXML private DatePicker dpUltimaConsulta;

    private Animal animalEmEdicao = null;

    @FXML
    public void initialize() {
        cbSexo.setItems(FXCollections.observableArrayList("Macho", "Fêmea"));
        cbVacina.setItems(FXCollections.observableArrayList("Vacinado", "Não Vacinado", "Incompleto"));
        cbGravida.setItems(FXCollections.observableArrayList("Grávida", "Não grávida", "Não se aplica"));

        cbSexo.valueProperty().addListener((obs, oldVal, newVal) -> atualizarStatusGravidez());
        
        dpDataNascimento.valueProperty().addListener((obs, oldDate, newDate) -> calcularIdade(newDate));
    }

    private void calcularIdade(LocalDate dataNascimento) {
        if (dataNascimento != null) {
            LocalDate hoje = LocalDate.now();
            if (!dataNascimento.isAfter(hoje)) {
                int anos = Period.between(dataNascimento, hoje).getYears();
                txtIdade.setText(String.valueOf(anos));
            } else {
                txtIdade.clear();
            }
        }
    }

    public void carregarDadosParaEdicao(Animal animal) {
        this.animalEmEdicao = animal;

        txtNome.setText(animal.getNome());
        txtIdade.setText(String.valueOf(animal.getIdade()));
        dpDataNascimento.setValue(animal.getDataNascimento());
        cbSexo.setValue(animal.getSexo());
        cbVacina.setValue(animal.getStatusVacinacao());
        cbGravida.setValue(animal.getStatusGravidez());
        dpUltimaConsulta.setValue(animal.getDataUltimaVacinacao());
    }

    @FXML
    private void salvar() {
        String nome = txtNome.getText().trim();
        String idadeTexto = txtIdade.getText().trim();
        String sexo = cbSexo.getValue();
        String statusGravidaTexto = cbGravida.getValue();
        String statusVacina = cbVacina.getValue();

        if (nome.isEmpty() || idadeTexto.isEmpty() || sexo == null || statusVacina == null || statusGravidaTexto == null) {
            mostrarAlerta("Erro", "Preencha todos os campos obrigatórios.");
            return;
        }

        if ("Fêmea".equalsIgnoreCase(sexo) && "Não se aplica".equalsIgnoreCase(statusGravidaTexto)) {
            mostrarAlerta("Erro", "Selecione se a fêmea está grávida ou não grávida.");
            return;
        }

        try {
            int idade = Integer.parseInt(idadeTexto);
            if (idade < 0) {
                mostrarAlerta("Erro", "A idade não pode ser negativa.");
                return;
            }

            Animal animal = (this.animalEmEdicao != null) ? this.animalEmEdicao : new Animal();
            
            animal.setNome(nome);
            animal.setIdade(idade);
            animal.setDataNascimento(dpDataNascimento.getValue());
            animal.setSexo(sexo);
            animal.setStatusVacinacao(statusVacina);
            animal.setStatusGravidez(statusGravidaTexto);
            animal.setDataUltimaVacinacao(dpUltimaConsulta.getValue());

            AnimalDAO animalDAO = new AnimalDAO();
            boolean sucesso;

            if (this.animalEmEdicao != null) {
                sucesso = animalDAO.atualizar(animal);
            } else {
                sucesso = animalDAO.salvar(animal);
            }

            if (sucesso) {
                String msg = (animalEmEdicao != null) ? "Animal atualizado com sucesso!" : "Animal cadastrado com sucesso!";
                mostrarAlerta("Sucesso", msg);
                
                boolean eraEdicao = (animalEmEdicao != null);
                limpar();

                if (eraEdicao) {
                    NavigationManager.getInstance().navegarConteudo("animais-cadastrados");
                }
            } else {
                mostrarAlerta("Erro", "Não foi possível salvar os dados do animal no banco de dados.");
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Erro", "A idade deve ser um número inteiro válido.");
        }
    }

    @FXML
    private void atualizarStatusGravidez() {
        String sexo = cbSexo.getValue();
        if ("Macho".equalsIgnoreCase(sexo)) {
            cbGravida.setValue("Não se aplica");
            cbGravida.setDisable(true);
        } else {
            cbGravida.setDisable(false);
            if ("Não se aplica".equalsIgnoreCase(cbGravida.getValue())) {
                cbGravida.setValue(null);
            }
        }
    }

    @FXML
    private void limpar() {
        this.animalEmEdicao = null;
        txtNome.clear();
        txtIdade.clear();
        dpDataNascimento.setValue(null);
        cbSexo.setValue(null);
        cbGravida.setValue(null);
        cbGravida.setDisable(false);
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

    @FXML
    private void verHistorico() {
        NavigationManager.getInstance().navegarConteudo("historico-animais-cadastrados");
    }
}