package com.abrigo.controller;

import com.abrigo.dao.LarTemporarioDAO;
import com.abrigo.model.LarTemp;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
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

    private final LarTemporarioDAO larDAO = new LarTemporarioDAO();
    private Long idEmEdicao = null;

    // Variável estática para receber os dados de outros controllers
    private static LarTemp larParaEdicao = null;

    public static void setLarParaEdicao(LarTemp lar) {
        larParaEdicao = lar;
    }

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

        // Se houver um lar selecionado, preenche os campos automaticamente
        if (larParaEdicao != null) {
            preencherParaEdicao(larParaEdicao);
            larParaEdicao = null; // Limpa a variável estática após consumir
        }
    }

    public void preencherParaEdicao(LarTemp lar) {
        if (lar == null) return;

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
        if (!validarCampos()) return;

        String nome = txtNome.getText().trim();
        String endereco = txtEndereco.getText().trim();
        String telefone = txtTelefone.getText().trim();
        Integer capacidade = spCapacidade.getValue();
        Integer vagas = spDisponibilidadeVagas.getValue();
        Boolean aceitaDoencas = "Sim".equalsIgnoreCase(cbAceitaDoencas.getValue());
        Boolean aceitaDeficiencia = "Sim".equalsIgnoreCase(cbAceitaDeficiencia.getValue());

        LarTemp lar = new LarTemp(nome, endereco, telefone, capacidade, vagas, aceitaDoencas, aceitaDeficiencia);

        boolean sucesso;
        if (idEmEdicao != null) {
            lar.setId(idEmEdicao);
            sucesso = larDAO.atualizar(lar);
        } else {
            sucesso = larDAO.salvar(lar);
        }

        if (sucesso) {
            mostrarAlerta("Sucesso", idEmEdicao != null ? "Lar atualizado com sucesso!" : "Lar cadastrado com sucesso!", Alert.AlertType.INFORMATION);
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
    }

    private boolean validarCampos() {
        if (txtNome == null || txtNome.getText().isBlank()) {
            mostrarAlerta("Aviso", "O campo Nome do responsável é obrigatório.", Alert.AlertType.WARNING);
            return false;
        }
        return true;
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