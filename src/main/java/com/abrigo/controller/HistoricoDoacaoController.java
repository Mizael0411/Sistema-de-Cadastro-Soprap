package com.abrigo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import com.abrigo.dao.DoacaoDAO;
import com.abrigo.model.Doacao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

public class HistoricoDoacaoController {

    @FXML private TableView<Doacao> tabelaDoacoes;
    @FXML private TableColumn<Doacao, Long> colId;
    @FXML private TableColumn<Doacao, String> colDoador;
    @FXML private TableColumn<Doacao, String> colTipo;
    @FXML private TableColumn<Doacao, LocalDate> colData;
    @FXML private TableColumn<Doacao, String> colDetalhes;
    @FXML private TextField txtBusca;

    private final DoacaoDAO doacaoDAO = new DoacaoDAO();
    private final ObservableList<Doacao> todasDoacoes = FXCollections.observableArrayList();
    private FilteredList<Doacao> doacoesFiltradas;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDoador.setCellValueFactory(new PropertyValueFactory<>("nomeDoador"));
        colTipo.setCellValueFactory(new PropertyValueFactory<>("tipoDoacao"));
        colData.setCellValueFactory(new PropertyValueFactory<>("dataDoacao"));
        colDetalhes.setCellValueFactory(new PropertyValueFactory<>("descricao"));

        // Redimensiona as colunas para ocupar toda a largura da tabela proporcionalmente
        tabelaDoacoes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        // Formatação da Data na Tabela (dd/MM/yyyy)
        colData.setCellFactory(tc -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : formatter.format(value));
            }
        });

        // Configuração do Tooltip Rápido com quebra de linha para textos cortados
        colDetalhes.setCellFactory(column -> new TableCell<Doacao, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setTooltip(null);
                } else {
                    setText(item);
                    Tooltip tooltip = new Tooltip(item);
                    tooltip.setWrapText(true);
                    tooltip.setMaxWidth(400);
                    tooltip.setShowDelay(Duration.millis(100)); // Tempo de exibição quase instantâneo
                    setTooltip(tooltip);
                }
            }
        });

        configurarBusca();
        carregarDados();
    }

    private void configurarBusca() {
        doacoesFiltradas = new FilteredList<>(todasDoacoes, d -> true);
        tabelaDoacoes.setItems(doacoesFiltradas);

        if (txtBusca != null) {
            txtBusca.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro(newVal));
        }
    }

    private void aplicarFiltro(String termo) {
        if (termo == null || termo.isBlank()) {
            doacoesFiltradas.setPredicate(d -> true);
            return;
        }

        String termoLimpo = termo.trim();
        String termoMinusc = termoLimpo.toLowerCase();

        doacoesFiltradas.setPredicate(d -> {
            if (d.getId() != null && String.valueOf(d.getId()).contains(termoLimpo)) {
                return true;
            }
            if (d.getNomeDoador() != null && d.getNomeDoador().toLowerCase().contains(termoMinusc)) {
                return true;
            }
            if (d.getTipoDoacao() != null && d.getTipoDoacao().toLowerCase().contains(termoMinusc)) {
                return true;
            }
            return false;
        });
    }

    private void carregarDados() {
        try {
            List<Doacao> lista = doacaoDAO.listarTodos();
            todasDoacoes.setAll(lista);
            aplicarFiltro(txtBusca != null ? txtBusca.getText() : null);
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Conexão",
                    "Não foi possível carregar o histórico de doações: " + e.getMessage());
        }
    }

    @FXML
    public void btnNovaDoacaoOnAction() {
        NavigationManager.getInstance().navegarConteudo("cadastro-doacoes");
    }

    @FXML
    public void btnEditarOnAction() {
        Doacao selecionada = tabelaDoacoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Pendente",
                    "Selecione uma doação na tabela para editar.");
            return;
        }

        NavigationManager.getInstance().setDoacaoParaEdicao(selecionada);

        CadastroDoacaoController controller = (CadastroDoacaoController) NavigationManager.getInstance()
                .navegarConteudoComController("cadastro-doacoes");

        if (controller != null) {
            controller.carregarDoacaoParaEdicao(selecionada);
        }
    }

    @FXML
    public void btnExcluirOnAction() {
        Doacao selecionada = tabelaDoacoes.getSelectionModel().getSelectedItem();
        if (selecionada == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Pendente",
                    "Selecione uma doação na tabela para excluir.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja realmente excluir a doação de: " + selecionada.getNomeDoador() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                doacaoDAO.deletar(selecionada.getId());
                carregarDados();
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Doação excluída com sucesso.");
            } catch (Exception e) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro",
                        "Falha ao excluir o registro: " + e.getMessage());
            }
        }
    }

    @FXML
    public void btnVoltarOnAction() {
        NavigationManager.getInstance().navegarConteudo("cadastro-doacoes");
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}