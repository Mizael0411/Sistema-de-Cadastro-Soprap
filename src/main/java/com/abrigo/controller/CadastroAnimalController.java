package com.abrigo.controller;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.database.JPAUtil;
import com.abrigo.model.Animal;

import jakarta.persistence.EntityManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class CadastroAnimalController {

    @FXML private TextField txtNome;
    @FXML private TextField txtIdade;
    @FXML private DatePicker dpDataNascimento;
    @FXML private ComboBox<String> cbSexo;
    @FXML private ComboBox<String> cbGravida;
    @FXML private ComboBox<String> cbEspecie;
    @FXML private ComboBox<String> cbPorte;

    private Animal animalEmEdicao = null;

    private static final String ESTILO_INVALIDO =
            " -fx-border-color: #E53935 !important; -fx-border-width: 2px; -fx-border-radius: 10;";

    @FXML
    public void initialize() {
        cbSexo.setItems(FXCollections.observableArrayList("Macho", "Fêmea"));
        cbGravida.setItems(FXCollections.observableArrayList("Grávida", "Não grávida", "Não se aplica"));
        cbEspecie.setItems(FXCollections.observableArrayList("Cachorro", "Gato"));
        cbPorte.setItems(FXCollections.observableArrayList("Mini", "Pequeno", "Médio", "Grande", "Gigante"));

        cbSexo.valueProperty().addListener((obs, oldVal, newVal) -> {
            atualizarStatusGravidez();
            marcarInvalido(cbSexo, false);
        });
        dpDataNascimento.valueProperty().addListener((obs, oldDate, newDate) -> {
            calcularIdade(newDate);
            marcarInvalido(dpDataNascimento, false);
        });

        dpDataNascimento.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #ffc4c4;");
                }
            }
        });

        dpDataNascimento.getEditor().textProperty().addListener((obs, oldText, newText) -> {
            try {
                if (newText != null && !newText.isBlank()) {
                    LocalDate dataDigitada = dpDataNascimento.getConverter().fromString(newText);
                    calcularIdade(dataDigitada);
                }
            } catch (Exception e) {}
        });

        configurarLimpezaValidacao();
    }

    private void configurarLimpezaValidacao() {
        txtNome.textProperty().addListener((o, ov, nv) -> marcarInvalido(txtNome, false));
        txtIdade.textProperty().addListener((o, ov, nv) -> marcarInvalido(txtIdade, false));
        cbEspecie.valueProperty().addListener((o, ov, nv) -> marcarInvalido(cbEspecie, false));
        cbPorte.valueProperty().addListener((o, ov, nv) -> marcarInvalido(cbPorte, false));
        cbGravida.valueProperty().addListener((o, ov, nv) -> marcarInvalido(cbGravida, false));
    }

    private void marcarInvalido(Control control, boolean invalido) {
        if (control == null) return;

        final String CHAVE_ESTILO_ORIGINAL = "estiloOriginal";
        if (control.getProperties().get(CHAVE_ESTILO_ORIGINAL) == null) {
            control.getProperties().put(CHAVE_ESTILO_ORIGINAL, control.getStyle());
        }
        String original = (String) control.getProperties().get(CHAVE_ESTILO_ORIGINAL);

        if (invalido) {
            control.setStyle(original + ESTILO_INVALIDO);
        } else {
            control.setStyle(original);
        }
    }

    private void limparMarcacoes() {
        marcarInvalido(txtNome, false);
        marcarInvalido(txtIdade, false);
        marcarInvalido(cbEspecie, false);
        marcarInvalido(cbPorte, false);
        marcarInvalido(cbSexo, false);
        marcarInvalido(cbGravida, false);
        marcarInvalido(dpDataNascimento, false);
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
        limparMarcacoes();

        txtNome.setText(animal.getNome());
        txtIdade.setText(String.valueOf(animal.getIdade()));
        dpDataNascimento.setValue(animal.getDataNascimento());
        cbSexo.setValue(animal.getSexo());
        cbGravida.setValue(animal.getStatusGravidez());
        cbEspecie.setValue(animal.getEspecie());
        cbPorte.setValue(animal.getPorte());
    }

    @FXML
    private void salvar() {
        limparMarcacoes();

        String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
        String idadeTexto = txtIdade.getText() != null ? txtIdade.getText().trim() : "";
        String sexo = cbSexo.getValue();
        String statusGravidaTexto = cbGravida.getValue();
        String especie = cbEspecie.getValue();
        String porte = cbPorte.getValue();

        if ("Macho".equalsIgnoreCase(sexo) && statusGravidaTexto == null) {
            statusGravidaTexto = "Não se aplica";
            cbGravida.setValue("Não se aplica");
        }

        List<String> camposFaltantes = new ArrayList<>();
        List<Control> controlesInvalidos = new ArrayList<>();

        if (nome.isEmpty()) {
            camposFaltantes.add("Nome do Animal");
            controlesInvalidos.add(txtNome);
        }
        if (especie == null) {
            camposFaltantes.add("Espécie");
            controlesInvalidos.add(cbEspecie);
        }
        if (porte == null) {
            camposFaltantes.add("Porte do Animal");
            controlesInvalidos.add(cbPorte);
        }
        if (sexo == null) {
            camposFaltantes.add("Sexo");
            controlesInvalidos.add(cbSexo);
        }
        if (statusGravidaTexto == null || statusGravidaTexto.isBlank()) {
            camposFaltantes.add("Status Gestacional");
            controlesInvalidos.add(cbGravida);
        } else if ("Fêmea".equalsIgnoreCase(sexo) && "Não se aplica".equalsIgnoreCase(statusGravidaTexto)) {
            camposFaltantes.add("Status Gestacional (selecione Grávida ou Não grávida)");
            controlesInvalidos.add(cbGravida);
        }
        if (idadeTexto.isEmpty()) {
            camposFaltantes.add("Idade Estimada");
            controlesInvalidos.add(txtIdade);
        }

        if (!idadeTexto.isEmpty()) {
            try {
                int idade = Integer.parseInt(idadeTexto);
                if (idade < 0) {
                    camposFaltantes.add("Idade (não pode ser negativa)");
                    controlesInvalidos.add(txtIdade);
                }
            } catch (NumberFormatException e) {
                camposFaltantes.add("Idade (deve ser um número inteiro)");
                controlesInvalidos.add(txtIdade);
            }
        }

        LocalDate dataNasc = dpDataNascimento.getValue();
        if (dataNasc != null && dataNasc.isAfter(LocalDate.now())) {
            camposFaltantes.add("Data de Nascimento (não pode ser futura)");
            controlesInvalidos.add(dpDataNascimento);
        }

        if (!camposFaltantes.isEmpty()) {
            for (Control c : controlesInvalidos) {
                marcarInvalido(c, true);
            }

            StringBuilder msg = new StringBuilder();
            if (camposFaltantes.size() == 1) {
                msg.append("Corrija o seguinte campo destacado em vermelho:\n\n");
            } else {
                msg.append("Corrija os seguintes campos destacados em vermelho:\n\n");
            }
            for (String campo : camposFaltantes) {
                msg.append("  •  ").append(campo).append("\n");
            }

            mostrarAlerta("Campos Inválidos", msg.toString());
            return;
        }

        try {
            int idade = Integer.parseInt(idadeTexto);

            Animal animal = (this.animalEmEdicao != null) ? this.animalEmEdicao : new Animal();

            animal.setNome(nome);
            animal.setIdade(idade);
            animal.setDataNascimento(dataNasc);
            animal.setSexo(sexo);
            animal.setStatusGravidez(statusGravidaTexto);
            animal.setEspecie(especie);
            animal.setPorte(porte);

            AnimalDAO animalDAO = new AnimalDAO();
            boolean sucesso = (this.animalEmEdicao != null)
                    ? animalDAO.atualizar(animal)
                    : animalDAO.salvar(animal);

            if (sucesso) {
                String msg = (animalEmEdicao != null)
                        ? "Animal atualizado com sucesso!"
                        : "Animal cadastrado com sucesso!";
                mostrarAlerta("Sucesso", msg);

                boolean eraEdicao = (animalEmEdicao != null);
                limpar();

                if (eraEdicao) {
                    NavigationManager.getInstance().navegarConteudo("historico-animais-cadastrados");
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
        cbEspecie.setValue(null);
        cbPorte.setValue(null);
        limparMarcacoes();
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