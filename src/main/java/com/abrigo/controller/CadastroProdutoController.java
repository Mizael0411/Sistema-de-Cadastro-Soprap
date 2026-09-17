package com.abrigo.controller;

import java.time.LocalDate;

import com.abrigo.dao.ProdutoDAO;
import com.abrigo.model.Produto;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;

public class CadastroProdutoController {

    @FXML private TextField txtNome;
    @FXML private TextField txtPrecoCompra;
    @FXML private DatePicker dpDataCompra;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();
    private Produto produtoEdicao = null;

    @FXML
    public void initialize() {
        Produto produto = NavigationManager.getInstance().getProdutoParaEdicao();
        if(produto != null) {
            carregarProdutoParaEdicao(produto);
        }

        if (dpDataCompra != null) {
            dpDataCompra.setDayCellFactory(picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);
                    if (date != null && date.isAfter(LocalDate.now())) {
                        setDisable(true);
                        setStyle("-fx-background-color: #ffc4c4;");
                    }
                }
            });
        }
    }

    public void carregarProdutoParaEdicao(Produto produto) {
        if (produto != null) {
            this.produtoEdicao = produto;
            txtNome.setText(produto.getNome());
            txtPrecoCompra.setText(String.valueOf(produto.getPrecoCompra()));
            dpDataCompra.setValue(produto.getDataCompra());
        }
    }

    @FXML
    private void btnSalvarOnAction() {
        try {
            if (txtNome.getText().trim().isEmpty() || 
                txtPrecoCompra.getText().trim().isEmpty() || 
                dpDataCompra.getValue() == null) {
                exibirAlerta(Alert.AlertType.WARNING, "Campos Obrigatórios", "Por favor, preencha todos os campos.");
                return;
            }

            if (dpDataCompra.getValue().isAfter(LocalDate.now())) {
                exibirAlerta(Alert.AlertType.WARNING, "Data Inválida", "A data de compra não pode ser uma data futura.");
                return;
            }

            double preco = Double.parseDouble(txtPrecoCompra.getText().replace(",", "."));
            if (preco <= 0) {
                exibirAlerta(Alert.AlertType.WARNING, "Preço Inválido", "O preço de compra deve ser maior que zero.");
                return;
            }

            if (this.produtoEdicao == null) {
                Produto novo = new Produto(
                    txtNome.getText().trim(),
                    preco,
                    dpDataCompra.getValue()
                );
                produtoDAO.salvar(novo);
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto cadastrado com sucesso!");
            } else {
                this.produtoEdicao.setNome(txtNome.getText().trim());
                this.produtoEdicao.setPrecoCompra(preco);
                this.produtoEdicao.setDataCompra(dpDataCompra.getValue());

                produtoDAO.atualizar(this.produtoEdicao);
                exibirAlerta(Alert.AlertType.INFORMATION, "Sucesso", "Produto atualizado com sucesso!");
            }

            btnLimparOnAction();

        } catch (NumberFormatException e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro de Formatação", "Insira um valor numérico válido para o preço.");
        } catch (Exception e) {
            exibirAlerta(Alert.AlertType.ERROR, "Erro no Banco", "Falha ao salvar a operação: " + e.getMessage());
        }
    }

    @FXML
    private void btnLimparOnAction() {
        txtNome.clear();
        txtPrecoCompra.clear();
        dpDataCompra.setValue(null);
        this.produtoEdicao = null;
    }

    @FXML
    public void verHistorico() {
        NavigationManager.getInstance().navegarConteudo("historico-produto");
    }

    private void exibirAlerta(Alert.AlertType tipo, String titulo, String msg) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}