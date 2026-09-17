package com.abrigo.controller;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;

import com.abrigo.dao.ProdutoDAO;
import com.abrigo.model.Produto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoricoCadastroProdutoController {

    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, Long> colId;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, Double> colPrecoCompra;
    @FXML private TableColumn<Produto, LocalDate> colDataCompra;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colPrecoCompra.setCellValueFactory(new PropertyValueFactory<>("precoCompra"));
        colDataCompra.setCellValueFactory(new PropertyValueFactory<>("dataCompra"));

        // Formatação em Reais (R$)
        colPrecoCompra.setCellFactory(tc -> new TableCell<>() {
            private final NumberFormat nf = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : nf.format(value));
            }
        });

        // Formatação de Data (dd/MM/yyyy)
        colDataCompra.setCellFactory(tc -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : formatter.format(value));
            }
        });

        carregarDados();
    }

    private void carregarDados() {
        try {
            ObservableList<Produto> listaProdutos = FXCollections.observableArrayList(produtoDAO.listarTodos());
            tabelaProdutos.setItems(listaProdutos);
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

        // Armazena no manager para garantia total (Fallback)
        NavigationManager.getInstance().setProdutoParaEdicao(selecionado);

        // Abre a tela e recupera a instância do controller
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