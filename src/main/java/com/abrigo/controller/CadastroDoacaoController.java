package com.abrigo.controller;

import java.time.LocalDate;

import com.abrigo.dao.DoacaoDAO;
import com.abrigo.model.Doacao;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class CadastroDoacaoController {

    @FXML private TextField txtNomeDoador;
    @FXML private ComboBox<String> cbTipoDoacao;
    @FXML private DatePicker dpDataDoacao;
    @FXML private TextField txtValor;
    @FXML private TextArea txtDescricao;
    @FXML private Label lblDescricao;

    @FXML private VBox boxValor;
    @FXML private VBox boxDescricao;

    private final DoacaoDAO doacaoDAO = new DoacaoDAO();
    private Doacao doacaoEmEdicao;

    @FXML
    public void initialize() {
        cbTipoDoacao.setItems(FXCollections.observableArrayList("FINANCEIRA", "MATERIAL"));
        
        // CORREÇÃO: Define o Prompt Text padrão e garante que inicia nulo
        cbTipoDoacao.setPromptText("Selecione...");
        
        dpDataDoacao.setValue(LocalDate.now());

        // Bloqueio de seleção de datas futuras
        dpDataDoacao.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isAfter(LocalDate.now())) {
                    setDisable(true);
                    setStyle("-fx-background-color: #FFEBEE;");
                }
            }
        });

        handleTipoChange();

        // Carrega registro se veio do botão Editar do Histórico
        Doacao doacaoPend = NavigationManager.getInstance().getDoacaoParaEdicao();
        if (doacaoPend != null) {
            carregarDoacaoParaEdicao(doacaoPend);
        }
    }

    @FXML
    private void handleTipoChange() {
        String tipo = cbTipoDoacao.getValue();
        boolean isFinanceira = "FINANCEIRA".equalsIgnoreCase(tipo);

        if (boxValor != null) {
            boxValor.setVisible(isFinanceira);
            boxValor.setManaged(isFinanceira);
        }

        if (lblDescricao != null) {
            if (isFinanceira) {
                lblDescricao.setText("OBSERVAÇÃO / DETALHES (OPCIONAL)");
            } else {
                lblDescricao.setText("DESCRIÇÃO DO ITEM OU MATERIAL *");
            }
        }
    }

    @FXML
    private void salvar() {
        try {
            if (txtNomeDoador.getText() == null || txtNomeDoador.getText().isBlank()) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Validação", "O nome do doador é obrigatório.");
                return;
            }

            if (cbTipoDoacao.getValue() == null) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Selecione o tipo de doação.");
                return;
            }

            if (dpDataDoacao.getValue() == null) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Informe a data da doação.");
                return;
            }

            if (dpDataDoacao.getValue().isAfter(LocalDate.now())) {
                exibirAlerta(Alert.AlertType.ERROR, "Data Inválida", "Não é permitido registrar doações com datas futuras.");
                return;
            }

            Doacao doacao = (doacaoEmEdicao != null) ? doacaoEmEdicao : new Doacao();
            doacao.setNomeDoador(txtNomeDoador.getText().trim());
            doacao.setTipoDoacao(cbTipoDoacao.getValue());
            doacao.setDataDoacao(dpDataDoacao.getValue());
            doacao.setDescricao(txtDescricao.getText());

            if ("FINANCEIRA".equalsIgnoreCase(cbTipoDoacao.getValue())) {
                if (txtValor.getText() != null && !txtValor.getText().isBlank()) {
                    doacao.setValor(Double.parseDouble(txtValor.getText().replace(",", ".")));
                } else {
                    exibirAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Informe o valor para a doação financeira.");
                    return;
                }
            } else {
                if (txtDescricao.getText() == null || txtDescricao.getText().isBlank()) {
                    exibirAlerta(Alert.AlertType.ERROR, "Erro de Validação", "Por favor, descreva o que foi doado.");
                    return;
                }
                doacao.setValor(null);
            }

            doacaoDAO.salvar(doacao);
            exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Doação salva com sucesso!");
            limpar();

        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Formatação", "O valor numérico informado é inválido.");
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro", "Erro ao salvar doação: " + e.getMessage());
        }
    }

    @FXML
    private void limpar() {
        this.doacaoEmEdicao = null;
        txtNomeDoador.clear();
        cbTipoDoacao.setValue(null);
        cbTipoDoacao.getSelectionModel().clearSelection();
        dpDataDoacao.setValue(LocalDate.now());
        txtValor.clear();
        txtDescricao.clear();
        handleTipoChange();
    }

    @FXML
    private void verHistorico() {
        NavigationManager.getInstance().navegarConteudo("historico-doacoes");
    }

    public void carregarDoacaoParaEdicao(Doacao doacao) {
        if (doacao != null) {
            this.doacaoEmEdicao = doacao;
            txtNomeDoador.setText(doacao.getNomeDoador());
            cbTipoDoacao.setValue(doacao.getTipoDoacao());
            dpDataDoacao.setValue(doacao.getDataDoacao());
            txtDescricao.setText(doacao.getDescricao());

            if (doacao.getValor() != null) {
                txtValor.setText(String.valueOf(doacao.getValor()));
            } else {
                txtValor.clear();
            }

            handleTipoChange();
        }
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}