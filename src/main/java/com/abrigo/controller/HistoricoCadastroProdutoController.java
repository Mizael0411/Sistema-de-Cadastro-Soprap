package com.abrigo.controller;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

import com.abrigo.dao.ProdutoDAO;
import com.abrigo.model.NavigationManager;
import com.abrigo.model.Produto;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class HistoricoCadastroProdutoController {

    @FXML
    private TableView<Produto> tabelaProdutos;

    @FXML
    private TableColumn<Produto, Long> colId;

    @FXML
    private TableColumn<Produto, String> colNome;

    @FXML
    private TableColumn<Produto, Double> colPrecoCompra;

    @FXML
    private TableColumn<Produto, LocalDate> colDataCompra;

    @FXML
    private TableColumn<Produto, LocalDate> colDataValidade;

    @FXML
    private TextField txtBusca;

    @FXML
    private Label lblIdDetalhe;

    @FXML
    private Label lblNomeDetalhe;

    @FXML
    private Label lblPrecoDetalhe;

    @FXML
    private Label lblDataCompraDetalhe;

    @FXML
    private Label lblDataValidadeDetalhe;

    @FXML
    private Label lblSituacaoValidade;

    private final ProdutoDAO produtoDAO = new ProdutoDAO();

    private final NavigationManager navigationManager =
            NavigationManager.getInstance();

    private final ObservableList<Produto> todosProdutos =
            FXCollections.observableArrayList();

    private FilteredList<Produto> produtosFiltrados;

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final NumberFormat FORMATO_MOEDA =
            NumberFormat.getCurrencyInstance(
                    Locale.forLanguageTag("pt-BR")
            );

    @FXML
    public void initialize() {

        configurarTabela();
        configurarColunaPreco();
        configurarColunaDataCompra();
        configurarColunaValidade();
        configurarBusca();
        configurarSelecao();

        carregarDados();
    }

    private void configurarTabela() {

        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colNome.setCellValueFactory(
                new PropertyValueFactory<>("nome")
        );

        colPrecoCompra.setCellValueFactory(
                new PropertyValueFactory<>("precoCompra")
        );

        colDataCompra.setCellValueFactory(
                new PropertyValueFactory<>("dataCompra")
        );

        colDataValidade.setCellValueFactory(
                new PropertyValueFactory<>("dataValidade")
        );

        tabelaProdutos.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }

    private void configurarColunaPreco() {

        colPrecoCompra.setCellFactory(coluna ->
                new TableCell<Produto, Double>() {

            @Override
            protected void updateItem(Double valor, boolean vazio) {

                super.updateItem(valor, vazio);

                setText(null);
                setStyle("");

                if (vazio) {
                    return;
                }

                setText(
                        valor == null
                                ? "Não informado"
                                : FORMATO_MOEDA.format(valor)
                );
            }
        });
    }

    private void configurarColunaDataCompra() {

        colDataCompra.setCellFactory(coluna ->
                new TableCell<Produto, LocalDate>() {

            @Override
            protected void updateItem(
                    LocalDate data,
                    boolean vazio) {

                super.updateItem(data, vazio);

                setText(null);
                setStyle("");

                if (vazio) {
                    return;
                }

                setText(
                        data == null
                                ? "Não informada"
                                : data.format(FORMATO_DATA)
                );
            }
        });
    }

    private void configurarColunaValidade() {

        colDataValidade.setCellFactory(coluna ->
                new TableCell<Produto, LocalDate>() {

            @Override
            protected void updateItem(
                    LocalDate validade,
                    boolean vazio) {

                super.updateItem(validade, vazio);

                setText(null);
                setStyle("");

                if (vazio) {
                    return;
                }

                if (validade == null) {

                    setText("Não informada");
                    setStyle("-fx-text-fill: #8A7B82;");

                    return;
                }

                setText(validade.format(FORMATO_DATA));

                LocalDate hoje = LocalDate.now();

                if (validade.isBefore(hoje)) {

                    setStyle(
                            "-fx-text-fill: #C62828;"
                            + "-fx-font-weight: bold;"
                    );

                } else if (validade.isEqual(hoje)) {

                    setStyle(
                            "-fx-text-fill: #C62828;"
                            + "-fx-font-weight: bold;"
                    );

                } else {

                    long dias = ChronoUnit.DAYS.between(
                            hoje,
                            validade
                    );

                    if (dias <= 30) {

                        setStyle(
                                "-fx-text-fill: #B9770E;"
                                + "-fx-font-weight: bold;"
                        );

                    } else {

                        setStyle("-fx-text-fill: #15803D;");
                    }
                }
            }
        });
    }

    private void configurarSelecao() {

        tabelaProdutos.getSelectionModel()
                .selectedItemProperty()
                .addListener((observable, anterior, selecionado) ->
                        mostrarDetalhes(selecionado)
                );
    }

    private void mostrarDetalhes(Produto produto) {

        if (produto == null) {

            lblIdDetalhe.setText("Nenhum produto selecionado");
            lblNomeDetalhe.setText("Selecione um produto na tabela");
            lblPrecoDetalhe.setText("—");
            lblDataCompraDetalhe.setText("—");
            lblDataValidadeDetalhe.setText("—");
            lblSituacaoValidade.setText(
                    "Selecione um produto para consultar a validade."
            );

            lblSituacaoValidade.setStyle(
                    "-fx-text-fill: #6F5F67;"
            );

            return;
        }

        lblIdDetalhe.setText(
                "Produto ID: " + produto.getId()
        );

        lblNomeDetalhe.setText(
                textoOuTraco(produto.getNome())
        );

        Double preco = produto.getPrecoCompra();

        lblPrecoDetalhe.setText(
                preco == null
                        ? "Não informado"
                        : FORMATO_MOEDA.format(preco)
        );

        LocalDate dataCompra = produto.getDataCompra();

        lblDataCompraDetalhe.setText(
                dataCompra == null
                        ? "Não informada"
                        : dataCompra.format(FORMATO_DATA)
        );

        LocalDate validade = produto.getDataValidade();

        lblDataValidadeDetalhe.setText(
                validade == null
                        ? "Não informada"
                        : validade.format(FORMATO_DATA)
        );

        atualizarSituacaoValidade(validade);
    }

    private void atualizarSituacaoValidade(LocalDate validade) {

        LocalDate hoje = LocalDate.now();

        if (validade == null) {

            lblSituacaoValidade.setText(
                    "A validade deste produto não foi informada."
            );

            lblSituacaoValidade.setStyle(
                    "-fx-text-fill: #6F5F67;"
            );

            return;
        }

        if (validade.isBefore(hoje)) {

            long diasVencidos = ChronoUnit.DAYS.between(
                    validade,
                    hoje
            );

            lblSituacaoValidade.setText(
                    "Produto vencido há "
                    + diasVencidos
                    + (diasVencidos == 1 ? " dia." : " dias.")
            );

            lblSituacaoValidade.setStyle(
                    "-fx-text-fill: #C62828;"
                    + "-fx-font-weight: bold;"
            );

        } else if (validade.isEqual(hoje)) {

            lblSituacaoValidade.setText(
                    "Atenção: este produto vence hoje!"
            );

            lblSituacaoValidade.setStyle(
                    "-fx-text-fill: #C62828;"
                    + "-fx-font-weight: bold;"
            );

        } else {

            long diasRestantes = ChronoUnit.DAYS.between(
                    hoje,
                    validade
            );

            if (diasRestantes <= 30) {

                lblSituacaoValidade.setText(
                        "Atenção: vence em "
                        + diasRestantes
                        + (diasRestantes == 1
                                ? " dia."
                                : " dias.")
                );

                lblSituacaoValidade.setStyle(
                        "-fx-text-fill: #B9770E;"
                        + "-fx-font-weight: bold;"
                );

            } else {

                lblSituacaoValidade.setText(
                        "Produto dentro da validade. "
                        + diasRestantes
                        + " dias restantes."
                );

                lblSituacaoValidade.setStyle(
                        "-fx-text-fill: #15803D;"
                        + "-fx-font-weight: bold;"
                );
            }
        }
    }

    private void configurarBusca() {

        produtosFiltrados = new FilteredList<>(
                todosProdutos,
                produto -> true
        );

        tabelaProdutos.setItems(produtosFiltrados);

        txtBusca.textProperty().addListener(
                (observable, anterior, atual) ->
                        aplicarFiltro(atual)
        );
    }

    private void aplicarFiltro(String termo) {

        if (termo == null || termo.isBlank()) {

            produtosFiltrados.setPredicate(
                    produto -> true
            );

            return;
        }

        String busca = termo.trim().toLowerCase(Locale.ROOT);

        produtosFiltrados.setPredicate(produto -> {

            if (produto.getId() != null
                    && String.valueOf(produto.getId())
                            .contains(busca)) {

                return true;
            }

            String nome = produto.getNome();

            return nome != null
                    && nome.toLowerCase(Locale.ROOT)
                            .contains(busca);
        });

        if (produtosFiltrados.isEmpty()) {
            mostrarDetalhes(null);
        } else {
            tabelaProdutos.getSelectionModel().selectFirst();
        }
    }

    private void carregarDados() {

        try {

            List<Produto> produtos = produtoDAO.listarTodos();

            todosProdutos.setAll(produtos);

            aplicarFiltro(
                    txtBusca == null
                            ? ""
                            : txtBusca.getText()
            );

            if (produtosFiltrados.isEmpty()) {
                mostrarDetalhes(null);
            } else if (tabelaProdutos.getSelectionModel()
                    .getSelectedItem() == null) {

                tabelaProdutos.getSelectionModel().selectFirst();
            }

        } catch (Exception e) {

            exibirAlerta(
                    Alert.AlertType.ERROR,
                    "Erro ao carregar produtos",
                    "Não foi possível carregar o histórico de produtos.\n\n"
                    + mensagemErro(e)
            );
        }
    }

    @FXML
    public void btnNovoProdutoOnAction() {

        navigationManager.navegarConteudo("cadastro-produto");
    }

    @FXML
    public void btnEditarOnAction() {

        Produto selecionado = tabelaProdutos
                .getSelectionModel()
                .getSelectedItem();

        if (selecionado == null) {

            exibirAlerta(
                    Alert.AlertType.WARNING,
                    "Seleção pendente",
                    "Selecione um produto na tabela para editar."
            );

            return;
        }

        navigationManager.setProdutoParaEdicao(selecionado);

        Object controller = navigationManager
                .navegarConteudoComController("cadastro-produto");

        if (controller instanceof CadastroProdutoController) {

            ((CadastroProdutoController) controller)
                    .carregarProdutoParaEdicao(selecionado);
        }
    }

    @FXML
    public void btnExcluirOnAction() {

        Produto selecionado = tabelaProdutos
                .getSelectionModel()
                .getSelectedItem();

        if (selecionado == null) {

            exibirAlerta(
                    Alert.AlertType.WARNING,
                    "Seleção pendente",
                    "Selecione um produto na tabela para excluir."
            );

            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION
        );

        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Excluir produto");

        confirmacao.setContentText(
                "Deseja realmente excluir o produto \""
                + textoOuTraco(selecionado.getNome())
                + "\"?"
        );

        confirmacao.showAndWait().ifPresent(resposta -> {

            if (resposta == ButtonType.OK) {

                try {

                    produtoDAO.deletar(selecionado.getId());

                    carregarDados();

                    exibirAlerta(
                            Alert.AlertType.INFORMATION,
                            "Sucesso",
                            "Produto excluído com sucesso."
                    );

                } catch (Exception e) {

                    exibirAlerta(
                            Alert.AlertType.ERROR,
                            "Erro ao excluir",
                            "Não foi possível excluir o produto.\n\n"
                            + mensagemErro(e)
                    );
                }
            }
        });
    }

    @FXML
    public void btnVoltarOnAction() {

        navigationManager.navegarConteudo("cadastro-produto");
    }

    private String textoOuTraco(String texto) {

        return texto == null || texto.isBlank()
                ? "Não informado"
                : texto.trim();
    }

    private String mensagemErro(Exception e) {

        return e.getMessage() == null
                || e.getMessage().isBlank()
                        ? "Verifique o console para mais detalhes."
                        : e.getMessage();
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