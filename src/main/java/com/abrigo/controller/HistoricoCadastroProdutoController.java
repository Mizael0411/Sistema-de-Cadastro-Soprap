package com.abrigo.controller;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import com.abrigo.dao.ProdutoDAO;
import com.abrigo.model.Produto;

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
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoricoCadastroProdutoController {

    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, Long> colId;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, Double> colPrecoCompra;
    @FXML private TableColumn<Produto, LocalDate> colDataCompra;
    @FXML private TextField txtBusca;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private final ObservableList<Produto> todosProdutos = FXCollections.observableArrayList();
    private FilteredList<Produto> produtosFiltrados;

    @FXML
    public void initialize() {
        configurarColunas();
        configurarBusca();
        carregarDados();
    }

    private void configurarColunas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPrecoCompra.setCellValueFactory(new PropertyValueFactory<>("precoCompra"));
        colDataCompra.setCellValueFactory(new PropertyValueFactory<>("dataCompra"));

        colPrecoCompra.setCellFactory(tc -> new TableCell<>() {
            private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : nf.format(value));
            }
        });

        colDataCompra.setCellFactory(tc -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : formatter.format(value));
            }
        });
    }

    private void configurarBusca() {
        produtosFiltrados = new FilteredList<>(todosProdutos, p -> true);
        tabelaProdutos.setItems(produtosFiltrados);

        if (txtBusca != null) {
            txtBusca.textProperty().addListener((obs, oldVal, newVal) -> aplicarFiltro(newVal));
        }
    }

    private void aplicarFiltro(String termo) {
        if (termo == null || termo.isBlank()) {
            produtosFiltrados.setPredicate(p -> true);
            return;
        }

        String termoMinusc = termo.trim().toLowerCase();
        produtosFiltrados.setPredicate(p -> {
            if (p.getId() != null && String.valueOf(p.getId()).contains(termoMinusc)) {
                return true;
            }
            if (p.getNome() != null && p.getNome().toLowerCase().contains(termoMinusc)) {
                return true;
            }
            return false;
        });
    }

    private void carregarDados() {
        try {
            List<Produto> lista = produtoDAO.listarTodos();
            todosProdutos.setAll(lista);
            aplicarFiltro(txtBusca != null ? txtBusca.getText() : null);
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Conexão", "Não foi possível carregar o histórico: " + e.getMessage());
        }
    }

    @FXML
    public void btnNovoProdutoOnAction() {
        NavigationManager.getInstance().navegarConteudo("cadastro-produto");
    }

    @FXML
    public void btnEditarOnAction() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Pendente", "Selecione um produto na tabela para editar.");
            return;
        }

        NavigationManager.getInstance().setProdutoParaEdicao(selecionado);

        CadastroProdutoController controller = (CadastroProdutoController) NavigationManager.getInstance()
                .navegarConteudoComController("cadastro-produto");

        if (controller != null) {
            controller.carregarProdutoParaEdicao(selecionado);
        }
    }

    @FXML
    public void btnExcluirOnAction() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            exibirAlerta(Alert.AlertType.WARNING, "Seleção Pendente", "Selecione um produto na tabela para excluir.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar Exclusão");
        alert.setHeaderText(null);
        alert.setContentText("Deseja realmente excluir o produto: " + selecionado.getNome() + "?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                produtoDAO.deletar(selecionado.getId());
                carregarDados();
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto excluído com sucesso.");
            } catch (Exception e) {
                exibirAlerta(Alert.AlertType.ERROR, "Erro", "Falha ao excluir o registro: " + e.getMessage());
            }
        }
    }

    @FXML
    public void btnVoltarOnAction() {
        NavigationManager.getInstance().navegarConteudo("cadastro-produto");
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}