package com.abrigo.controller;

import java.util.ArrayList;
import java.util.List;

import com.abrigo.dao.LarTemporarioDAO;
import com.abrigo.model.LarTemp;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

public class CadastroLarTempController {

    @FXML private TextField txtNome;
    @FXML private TextField txtEndereco;
    @FXML private TextField txtTelefone;
    @FXML private Spinner<Integer> spCapacidade;
    @FXML private Spinner<Integer> spDisponibilidadeVagas;
    @FXML private ComboBox<String> cbAceitaDoencas;
    @FXML private ComboBox<String> cbAceitaDeficiencia;
    @FXML private Button btnSalvar;

    // Novo: label de aviso ao lado do campo de Vagas Disponíveis.
    // Precisa existir no FXML com esse fx:id (ver observação depois do código).
    @FXML private Label lblAvisoVagas;

    private final LarTemporarioDAO larDAO = new LarTemporarioDAO();
    private Long idEmEdicao = null;

    private static LarTemp larParaEdicao = null;

    public static void setLarParaEdicao(LarTemp lar) {
        larParaEdicao = lar;
    }

    private static final String ESTILO_INVALIDO =
            " -fx-border-color: #E53935 !important; -fx-border-width: 2px; -fx-border-radius: 10;";

    @FXML
    public void initialize() {
        if (cbAceitaDoencas != null && cbAceitaDoencas.getItems().isEmpty()) {
            cbAceitaDoencas.setItems(FXCollections.observableArrayList("Sim", "Não"));
        }

        if (cbAceitaDeficiencia != null && cbAceitaDeficiencia.getItems().isEmpty()) {
            cbAceitaDeficiencia.setItems(FXCollections.observableArrayList("Sim", "Não"));
        }

        if (spCapacidade != null) {
            spCapacidade.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        }

        if (spDisponibilidadeVagas != null) {
            spDisponibilidadeVagas.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        }

        if (lblAvisoVagas != null) {
            lblAvisoVagas.setVisible(false);
            lblAvisoVagas.setManaged(false);
        }

        configurarValidacaoEmTempoReal();

        if (larParaEdicao != null) {
            preencherParaEdicao(larParaEdicao);
            larParaEdicao = null;
        }
    }

    // Igual ao configurarLimpezaValidacao() do CadastroAnimalController:
    // some com a borda vermelha assim que o usuário mexe no campo,
    // e além disso já verifica vagas x capacidade em tempo real.
    private void configurarValidacaoEmTempoReal() {
        txtNome.textProperty().addListener((o, ov, nv) -> marcarInvalido(txtNome, false));
        txtEndereco.textProperty().addListener((o, ov, nv) -> marcarInvalido(txtEndereco, false));
        txtTelefone.textProperty().addListener((o, ov, nv) -> marcarInvalido(txtTelefone, false));
        cbAceitaDoencas.valueProperty().addListener((o, ov, nv) -> marcarInvalido(cbAceitaDoencas, false));
        cbAceitaDeficiencia.valueProperty().addListener((o, ov, nv) -> marcarInvalido(cbAceitaDeficiencia, false));

        spCapacidade.valueProperty().addListener((o, ov, nv) -> validarVagasEmTempoReal());
        spDisponibilidadeVagas.valueProperty().addListener((o, ov, nv) -> validarVagasEmTempoReal());
    }

    // Roda a cada alteração de capacidade ou vagas: se vagas > capacidade,
    // deixa o Spinner de vagas com borda vermelha e mostra o aviso ao lado.
    private void validarVagasEmTempoReal() {
        Integer capacidade = spCapacidade.getValue();
        Integer vagas = spDisponibilidadeVagas.getValue();

        boolean vagasExcedeCapacidade = capacidade != null && vagas != null && vagas > capacidade;

        marcarInvalido(spDisponibilidadeVagas, vagasExcedeCapacidade);

        if (lblAvisoVagas == null) {
            return;
        }
        if (vagasExcedeCapacidade) {
            lblAvisoVagas.setText("Vagas não pode ser maior que a capacidade.");
            lblAvisoVagas.setVisible(true);
            lblAvisoVagas.setManaged(true);
        } else {
            lblAvisoVagas.setText("");
            lblAvisoVagas.setVisible(false);
            lblAvisoVagas.setManaged(false);
        }
    }

    // Igual ao marcarInvalido(...) do CadastroAnimalController.
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
        marcarInvalido(txtEndereco, false);
        marcarInvalido(txtTelefone, false);
        marcarInvalido(spCapacidade, false);
        marcarInvalido(spDisponibilidadeVagas, false);
        marcarInvalido(cbAceitaDoencas, false);
        marcarInvalido(cbAceitaDeficiencia, false);
        if (lblAvisoVagas != null) {
            lblAvisoVagas.setText("");
            lblAvisoVagas.setVisible(false);
            lblAvisoVagas.setManaged(false);
        }
    }

    public void preencherParaEdicao(LarTemp lar) {
        if (lar == null) return;
        limparMarcacoes();

        this.idEmEdicao = lar.getId();

        if (txtNome != null) txtNome.setText(lar.getNome());
        if (txtEndereco != null) txtEndereco.setText(lar.getEndereco());
        if (txtTelefone != null) txtTelefone.setText(lar.getTelefone());

        if (spCapacidade != null && spCapacidade.getValueFactory() != null) {
            spCapacidade.getValueFactory().setValue(lar.getCapacidadeMaxima() != null ? lar.getCapacidadeMaxima() : 0);
        }

        if (spDisponibilidadeVagas != null && spDisponibilidadeVagas.getValueFactory() != null) {
            spDisponibilidadeVagas.getValueFactory().setValue(lar.getVagasDisponiveis() != null ? lar.getVagasDisponiveis() : 0);
        }

        if (cbAceitaDoencas != null) {
            Boolean aceita = lar.getAceitaDoencasTransmissiveis();
            cbAceitaDoencas.setValue(Boolean.TRUE.equals(aceita) ? "Sim" : "Não");
        }

        if (cbAceitaDeficiencia != null) {
            Boolean aceitaDef = lar.getAceitaDeficiencia();
            cbAceitaDeficiencia.setValue(Boolean.TRUE.equals(aceitaDef) ? "Sim" : "Não");
        }

        if (btnSalvar != null) {
            btnSalvar.setText("Atualizar");
        }
    }

    @FXML
    private void salvar() {
        limparMarcacoes();

        String nome = txtNome.getText() != null ? txtNome.getText().trim() : "";
        String endereco = txtEndereco.getText() != null ? txtEndereco.getText().trim() : "";
        String telefone = txtTelefone.getText() != null ? txtTelefone.getText().trim() : "";
        Integer capacidade = spCapacidade.getValue();
        Integer vagas = spDisponibilidadeVagas.getValue();
        String aceitaDoencasTexto = cbAceitaDoencas.getValue();
        String aceitaDeficienciaTexto = cbAceitaDeficiencia.getValue();

        List<String> camposFaltantes = new ArrayList<>();
        List<Control> controlesInvalidos = new ArrayList<>();

        // ---------- CAMPOS OBRIGATÓRIOS ----------
        if (nome.isEmpty()) {
            camposFaltantes.add("Nome do Responsável");
            controlesInvalidos.add(txtNome);
        }
        if (endereco.isEmpty()) {
            camposFaltantes.add("Endereço");
            controlesInvalidos.add(txtEndereco);
        }
        if (telefone.isEmpty()) {
            camposFaltantes.add("Telefone");
            controlesInvalidos.add(txtTelefone);
        }
        if (capacidade == null || capacidade <= 0) {
            camposFaltantes.add("Capacidade Máxima (deve ser maior que zero)");
            controlesInvalidos.add(spCapacidade);
        }
        if (aceitaDoencasTexto == null) {
            camposFaltantes.add("Aceita Doenças Transmissíveis");
            controlesInvalidos.add(cbAceitaDoencas);
        }
        if (aceitaDeficienciaTexto == null) {
            camposFaltantes.add("Aceita Deficiência");
            controlesInvalidos.add(cbAceitaDeficiencia);
        }

        // ---------- VALIDAÇÕES DE VALOR ----------
        if (vagas == null) {
            camposFaltantes.add("Vagas Disponíveis");
            controlesInvalidos.add(spDisponibilidadeVagas);
        } else if (capacidade != null && vagas > capacidade) {
            camposFaltantes.add("Vagas Disponíveis (não pode ser maior que a capacidade)");
            controlesInvalidos.add(spDisponibilidadeVagas);
        }

        // ---------- SE HOUVER ERROS, MARCA E MOSTRA ----------
        if (!camposFaltantes.isEmpty()) {
            for (Control c : controlesInvalidos) {
                marcarInvalido(c, true);
            }

            StringBuilder msg = new StringBuilder();
            msg.append(camposFaltantes.size() == 1
                    ? "Corrija o seguinte campo destacado em vermelho:\n\n"
                    : "Corrija os seguintes campos destacados em vermelho:\n\n");
            for (String campo : camposFaltantes) {
                msg.append("  •  ").append(campo).append("\n");
            }

            mostrarAlerta("Campos Inválidos", msg.toString(), Alert.AlertType.WARNING);
            return;
        }

        // ---------- PERSISTÊNCIA ----------
        Boolean aceitaDoencas = "Sim".equalsIgnoreCase(aceitaDoencasTexto);
        Boolean aceitaDeficiencia = "Sim".equalsIgnoreCase(aceitaDeficienciaTexto);

        LarTemp lar = new LarTemp(nome, endereco, telefone, capacidade, vagas, aceitaDoencas, aceitaDeficiencia);

        boolean sucesso;
        if (idEmEdicao != null) {
            lar.setId(idEmEdicao);
            sucesso = larDAO.atualizar(lar);
        } else {
            sucesso = larDAO.salvar(lar);
        }

        if (sucesso) {
            mostrarAlerta("Sucesso",
                    idEmEdicao != null ? "Lar atualizado com sucesso!" : "Lar cadastrado com sucesso!",
                    Alert.AlertType.INFORMATION);
            limparCampos();
            NavigationManager.getInstance().navegarConteudo("historico-lar-temporario");
        } else {
            mostrarAlerta("Erro", "Erro ao salvar no banco de dados.", Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void limparCampos() {
        this.idEmEdicao = null;
        if (btnSalvar != null) btnSalvar.setText("Salvar");
        if (txtNome != null) txtNome.clear();
        if (txtEndereco != null) txtEndereco.clear();
        if (txtTelefone != null) txtTelefone.clear();
        if (spCapacidade != null && spCapacidade.getValueFactory() != null) spCapacidade.getValueFactory().setValue(0);
        if (spDisponibilidadeVagas != null && spDisponibilidadeVagas.getValueFactory() != null) spDisponibilidadeVagas.getValueFactory().setValue(0);
        if (cbAceitaDoencas != null) cbAceitaDoencas.setValue(null);
        if (cbAceitaDeficiencia != null) cbAceitaDeficiencia.setValue(null);
        limparMarcacoes();
    }

    private void mostrarAlerta(String titulo, String mensagem, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    @FXML
    public void verHistorico() {
        NavigationManager.getInstance().navegarConteudo("historico-lar-temporario");
    }
}