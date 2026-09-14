package com.abrigo.view;

import java.time.LocalDate;

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
    @FXML private ComboBox<String> cbSexo;
    @FXML private DatePicker dpDataNascimento;
    @FXML private ComboBox<String> cbGravida;
    @FXML private ComboBox<String> cbVacina;
    @FXML private DatePicker dpUltimaConsulta;

    // Guarda o animal que está sendo editado (null se for novo cadastro)
    private Animal animalEmEdicao = null;

    @FXML
    public void initialize() {
        cbSexo.setItems(FXCollections.observableArrayList("Macho", "Fêmea"));
        cbVacina.setItems(FXCollections.observableArrayList("Vacinado", "Não Vacinado", "Incompleto"));
        cbGravida.setItems(FXCollections.observableArrayList("Não grávida", "Grávida", "Não se aplica"));

        dpDataNascimento.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                int idadeCalculada = java.time.Period.between(newVal, LocalDate.now()).getYears();
                txtIdade.setText(String.valueOf(idadeCalculada));
            }
        });

        cbSexo.valueProperty().addListener((obs, oldVal, newVal) -> atualizarStatusGravidez());
    }

    // Método para carregar os dados do animal na tela ao clicar em Editar
    public void carregarDadosParaEdicao(Animal animal) {
        this.animalEmEdicao = animal;

        txtNome.setText(animal.getNome());
        txtIdade.setText(String.valueOf(animal.getIdade()));
        cbSexo.setValue(animal.getSexo());
        cbVacina.setValue(animal.getStatusVacinacao());
        cbGravida.setValue(animal.getStatusGravidez());

        // Atribuição direta de LocalDate para o DatePicker
        dpDataNascimento.setValue(animal.getDataNascimento());
        dpUltimaConsulta.setValue(animal.getDataUltimaVacinacao());
    }

    @FXML
    private void salvar() {
        String nome = txtNome.getText().trim();
        String idadeTexto = txtIdade.getText().trim();
        String sexo = cbSexo.getValue();
        String statusGravidez = cbGravida.getValue();
        String statusVacina = cbVacina.getValue();

        if (nome.isEmpty() || idadeTexto.isEmpty() || sexo == null || statusGravidez == null || statusVacina == null) {
            mostrarAlerta("Erro", "Preencha todos os campos obrigatórios.");
            return;
        }

        try {
            int idade = Integer.parseInt(idadeTexto);

            // Se for novo, instancia um novo Animal; se for edição, altera o existente
            Animal animal = (this.animalEmEdicao != null) ? this.animalEmEdicao : new Animal();
            
            animal.setNome(nome);
            animal.setIdade(idade);
            animal.setSexo(sexo);
            animal.setStatusVacinacao(statusVacina);
            animal.setStatusGravidez(statusGravidez);

            // Atribuição direta dos valores LocalDate do DatePicker no objeto Animal
            animal.setDataNascimento(dpDataNascimento.getValue());
            animal.setDataUltimaVacinacao(dpUltimaConsulta.getValue());

            AnimalDAO animalDAO = new AnimalDAO();

            if (animalDAO.salvar(animal)) {
                String msg = (animalEmEdicao != null) ? "Animal atualizado com sucesso!" : "Animal cadastrado com sucesso!";
                mostrarAlerta("Sucesso", msg);
                limpar();
                
                // Retorna para a tabela de animais cadastrados após salvar a edição
                if (animalEmEdicao != null) {
                    NavigationManager.getInstance().navegarConteudo("animais-cadastrados");
                }
            } else {
                mostrarAlerta("Erro", "Não foi possível salvar os dados do animal.");
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
            if ("Não se aplica".equals(cbGravida.getValue())) {
                cbGravida.setValue(null);
            }
        }
    }

    @FXML
    private void limpar() {
        this.animalEmEdicao = null;
        txtNome.clear();
        txtIdade.clear();
        cbSexo.setValue(null);
        dpDataNascimento.setValue(null);
        cbGravida.setDisable(false);
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

    @FXML
    private void verHistorico() {
        NavigationManager.getInstance().navegarConteudo("historico-animais-cadastrados");
    }
}