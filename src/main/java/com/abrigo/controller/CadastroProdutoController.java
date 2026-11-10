package com.abrigo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

import com.abrigo.dao.ProdutoDAO;
import com.abrigo.model.NavigationManager;
import com.abrigo.model.Produto;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;

public class CadastroProdutoController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtPrecoCompra;

    @FXML
    private DatePicker dpDataCompra;

    @FXML
    private DatePicker dpDataValidade;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private Produto produtoEdicao = null;

    private String nomeOriginal;
    private Double precoOriginal;
    private LocalDate dataCompraOriginal;
    private LocalDate dataValidadeOriginal;

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {

        configurarDatePicker(dpDataCompra);
        configurarDatePicker(dpDataValidade);

        if (dpDataCompra != null) {
            dpDataCompra.setDayCellFactory(picker ->
                    new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);

                    boolean dataFutura = date != null
                            && date.isAfter(LocalDate.now());

                    setDisable(empty || dataFutura);

                    setStyle(dataFutura
                            ? "-fx-background-color: #ffc4c4;;"
                            : "");
                }
            });
        }

        Produto produto =
                NavigationManager.getInstance()
                        .getProdutoParaEdicao();

        if (produto != null) {
            carregarProdutoParaEdicao(produto);
        }
    }

    private void configurarDatePicker(DatePicker datePicker) {

        if (datePicker == null) {
            return;
        }

        datePicker.setConverter(new StringConverter<LocalDate>() {

            @Override
            public String toString(LocalDate data) {
                return data == null
                        ? ""
                        : data.format(FORMATO_DATA);
            }

            @Override
            public LocalDate fromString(String texto) {

                if (texto == null || texto.isBlank()) {
                    return null;
                }

                return LocalDate.parse(
                        texto.trim(),
                        FORMATO_DATA
                );
            }
        });

        datePicker.setPromptText("dd/MM/aaaa");
    }

    private LocalDate lerData(DatePicker datePicker) {

        if (datePicker == null) {
            return null;
        }

        String texto = datePicker.getEditor().getText();

        if (texto == null || texto.isBlank()) {
            return null;
        }

        return LocalDate.parse(
                texto.trim(),
                FORMATO_DATA
        );
    }

    public void carregarProdutoParaEdicao(Produto produto) {

        if (produto == null) {
            return;
        }

        this.produtoEdicao = produto;

        this.nomeOriginal = produto.getNome();
        this.precoOriginal = produto.getPrecoCompra();
        this.dataCompraOriginal = produto.getDataCompra();
        this.dataValidadeOriginal = produto.getDataValidade();

        txtNome.setText(produto.getNome());

        txtPrecoCompra.setText(
                produto.getPrecoCompra() == null
                        ? ""
                        : String.valueOf(produto.getPrecoCompra())
        );

        dpDataCompra.setValue(produto.getDataCompra());
        dpDataValidade.setValue(produto.getDataValidade());
    }

    @FXML
    private void btnSalvarOnAction() {

        try {
            String nome = txtNome.getText() == null
                    ? ""
                    : txtNome.getText().trim();

            String textoPreco = txtPrecoCompra.getText() == null
                    ? ""
                    : txtPrecoCompra.getText().trim();

            LocalDate dataCompra = lerData(dpDataCompra);
            LocalDate dataValidade = lerData(dpDataValidade);

            if (nome.isEmpty()
                    || textoPreco.isEmpty()
                    || dataCompra == null) {

                exibirAlerta(
                        Alert.AlertType.WARNING,
                        "Campos obrigatórios",
                        "Preencha o nome, o preço de compra "
                        + "e a data da compra."
                );

                return;
            }

            if (dataCompra.isAfter(LocalDate.now())) {

                exibirAlerta(
                        Alert.AlertType.WARNING,
                        "Data inválida",
                        "A data de compra não pode ser futura."
                );

                return;
            }

            double preco = Double.parseDouble(
                    textoPreco.replace(",", ".")
            );

            if (!Double.isFinite(preco) || preco <= 0) {

                exibirAlerta(
                        Alert.AlertType.WARNING,
                        "Preço inválido",
                        "O preço de compra deve ser maior que zero."
                );

                return;
            }

            if (produtoEdicao == null) {

                Produto novo = new Produto(
                        nome,
                        preco,
                        dataCompra,
                        dataValidade
                );

                produtoDAO.salvar(novo);

                exibirAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sucesso",
                        "Produto cadastrado com sucesso!"
                );

            } else {

                boolean houveAlteracao =
                        !Objects.equals(nomeOriginal, nome)
                        || !Objects.equals(precoOriginal, preco)
                        || !Objects.equals(dataCompraOriginal, dataCompra)
                        || !Objects.equals(dataValidadeOriginal, dataValidade);

                if (!houveAlteracao) {
                    exibirAlerta(
                            Alert.AlertType.INFORMATION,
                            "Sem alterações",
                            "Nenhum dado do produto foi alterado."
                    );

                    btnLimparOnAction();
                    return;
                }

                produtoEdicao.setNome(nome);
                produtoEdicao.setPrecoCompra(preco);
                produtoEdicao.setDataCompra(dataCompra);
                produtoEdicao.setDataValidade(dataValidade);

                produtoDAO.atualizar(produtoEdicao);

                exibirAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sucesso",
                        "Produto atualizado com sucesso!"
                );
            }

            btnLimparOnAction();

        } catch (DateTimeParseException e) {

            exibirAlerta(
                    Alert.AlertType.WARNING,
                    "Data inválida",
                    "Confira as datas digitadas. Utilize o formato "
                    + "dd/MM/aaaa, por exemplo: 25/12/2026."
            );

        } catch (NumberFormatException e) {

            exibirAlerta(
                    Alert.AlertType.WARNING,
                    "Preço inválido",
                    "Digite um preço numérico válido, por exemplo: "
                    + "15,90."
            );

        } catch (Exception e) {

            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro ao salvar",
                    "Não foi possível salvar o produto.\n\n"
                    + e.getMessage()
            );
        }
    }

    @FXML
    private void btnLimparOnAction() {

        txtNome.clear();
        txtPrecoCompra.clear();

        dpDataCompra.setValue(null);
        dpDataValidade.setValue(null);

        produtoEdicao = null;

        nomeOriginal = null;
        precoOriginal = null;
        dataCompraOriginal = null;
        dataValidadeOriginal = null;

        dpDataCompra.getEditor().clear();
        dpDataValidade.getEditor().clear();

        NavigationManager.getInstance()
                .setProdutoParaEdicao(null);
    }

    @FXML
    public void verHistorico() {

        NavigationManager.getInstance()
                .navegarConteudo("historico-produto");
    }

    private void exibirAlerta(
            Alert.AlertType tipo,
            String titulo,
            String mensagem) {

        Alert alerta = new Alert(tipo);

        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);

        alerta.showAndWait();
    }
}
