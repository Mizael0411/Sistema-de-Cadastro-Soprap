package com.abrigo.controller;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.abrigo.model.Auditoria;
import com.abrigo.model.NavigationManager;
import com.abrigo.service.AuditoriaService;
import com.abrigo.service.SessaoUsuario;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class AuditoriaController {

    @FXML
    private TableView<Auditoria> tabelaAuditoria;

    @FXML
    private TableColumn<Auditoria, Long> colId;

    @FXML
    private TableColumn<Auditoria, String> colUsuario;

    @FXML
    private TableColumn<Auditoria, String> colOperacao;

    @FXML
    private TableColumn<Auditoria, String> colModulo;

    @FXML
    private TableColumn<Auditoria, Long> colRegistro;

    @FXML
    private TableColumn<Auditoria, String> colDescricao;

    @FXML
    private TableColumn<Auditoria, String> colDataHora;

    @FXML
    private ComboBox<String> cbUsuario;

    @FXML
    private ComboBox<String> cbOperacao;

    @FXML
    private ComboBox<String> cbModulo;

    @FXML
    private Label lblQuantidade;

    @FXML
    private Button btnAtualizar;

    private final AuditoriaService auditoriaService =
            new AuditoriaService();

    private final ObservableList<Auditoria> registros =
            FXCollections.observableArrayList();

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final String TODOS_USUARIOS = "Todos os usuários";
    private static final String TODAS_OPERACOES = "Todas as operações";
    private static final String TODOS_MODULOS = "Todos os módulos";

    @FXML
    private void initialize() {
        if (!SessaoUsuario.isAdministradora()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Acesso negado",
                    "Somente administradoras podem acessar a auditoria."
            );

            if (tabelaAuditoria != null) {
                tabelaAuditoria.setDisable(true);
            }

            if (btnAtualizar != null) {
                btnAtualizar.setDisable(true);
            }

            return;
        }

        configurarTabela();
        configurarFiltros();
        carregarAuditoria();
    }

    private void configurarTabela() {
        colId.setCellValueFactory(
                new PropertyValueFactory<>("id")
        );

        colUsuario.setCellValueFactory(
                new PropertyValueFactory<>("nomeUsuario")
        );

        colOperacao.setCellValueFactory(
                new PropertyValueFactory<>("operacao")
        );

        colModulo.setCellValueFactory(
                new PropertyValueFactory<>("modulo")
        );

        colRegistro.setCellValueFactory(
                new PropertyValueFactory<>("registroId")
        );

        colDescricao.setCellValueFactory(
                new PropertyValueFactory<>("descricao")
        );

        colDataHora.setCellValueFactory(cell -> {
            Auditoria auditoria = cell.getValue();

            if (auditoria.getDataHora() == null) {
                return new SimpleStringProperty("");
            }

            return new SimpleStringProperty(
                    auditoria.getDataHora().format(FORMATO_DATA)
            );
        });

        tabelaAuditoria.setItems(registros);

        tabelaAuditoria.setColumnResizePolicy(
                TableView.CONSTRAINED_RESIZE_POLICY
        );
    }

    private void configurarFiltros() {
        cbUsuario.setOnAction(event -> aplicarFiltros());
        cbOperacao.setOnAction(event -> aplicarFiltros());
        cbModulo.setOnAction(event -> aplicarFiltros());
    }

    @FXML
    private void carregarAuditoria() {
        if (!SessaoUsuario.isAdministradora()) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Acesso negado",
                    "Você não tem permissão para consultar a auditoria."
            );
            return;
        }

        try {
            String usuarioSelecionado = cbUsuario.getValue();
            String operacaoSelecionada = cbOperacao.getValue();
            String moduloSelecionado = cbModulo.getValue();

            List<Auditoria> lista = auditoriaService.listarTodas();

            registros.setAll(lista);

            preencherFiltros(
                    usuarioSelecionado,
                    operacaoSelecionada,
                    moduloSelecionado
            );

            aplicarFiltros();

        } catch (Exception e) {
            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Erro ao carregar auditoria",
                    "Não foi possível carregar os registros de auditoria."
            );

            e.printStackTrace();
        }
    }

    private void preencherFiltros(
            String usuarioSelecionado,
            String operacaoSelecionada,
            String moduloSelecionado) {

        List<String> usuarios = registros.stream()
                .map(Auditoria::getNomeUsuario)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<String> operacoes = registros.stream()
                .map(Auditoria::getOperacao)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        List<String> modulos = registros.stream()
                .map(Auditoria::getModulo)
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());

        cbUsuario.getItems().setAll(TODOS_USUARIOS);
        cbUsuario.getItems().addAll(usuarios);

        cbOperacao.getItems().setAll(TODAS_OPERACOES);
        cbOperacao.getItems().addAll(operacoes);

        cbModulo.getItems().setAll(TODOS_MODULOS);
        cbModulo.getItems().addAll(modulos);

        restaurarSelecao(
                cbUsuario,
                usuarioSelecionado,
                TODOS_USUARIOS
        );

        restaurarSelecao(
                cbOperacao,
                operacaoSelecionada,
                TODAS_OPERACOES
        );

        restaurarSelecao(
                cbModulo,
                moduloSelecionado,
                TODOS_MODULOS
        );
    }

    private void restaurarSelecao(
            ComboBox<String> combo,
            String valorAnterior,
            String valorPadrao) {

        if (valorAnterior != null
                && combo.getItems().contains(valorAnterior)) {

            combo.getSelectionModel().select(valorAnterior);

        } else {
            combo.getSelectionModel().select(valorPadrao);
        }
    }

    @FXML
    private void aplicarFiltros() {
        String usuario = cbUsuario.getValue();
        String operacao = cbOperacao.getValue();
        String modulo = cbModulo.getValue();

        if (TODOS_USUARIOS.equals(usuario)) {
            usuario = null;
        }

        if (TODAS_OPERACOES.equals(operacao)) {
            operacao = null;
        }

        if (TODOS_MODULOS.equals(modulo)) {
            modulo = null;
        }

        final String filtroUsuario = usuario;
        final String filtroOperacao = operacao;
        final String filtroModulo = modulo;

        List<Auditoria> filtrados = registros.stream()
                .filter(a -> filtroUsuario == null
                        || filtroUsuario.equals(a.getNomeUsuario()))
                .filter(a -> filtroOperacao == null
                        || filtroOperacao.equals(a.getOperacao()))
                .filter(a -> filtroModulo == null
                        || filtroModulo.equals(a.getModulo()))
                .collect(Collectors.toList());

        tabelaAuditoria.setItems(
                FXCollections.observableArrayList(filtrados)
        );

        lblQuantidade.setText(
                filtrados.size() + " registro(s)"
        );
    }

    @FXML
    private void limparFiltros() {
        cbUsuario.getSelectionModel().select(TODOS_USUARIOS);
        cbOperacao.getSelectionModel().select(TODAS_OPERACOES);
        cbModulo.getSelectionModel().select(TODOS_MODULOS);

        aplicarFiltros();
    }

    @FXML
    private void voltar() {
        NavigationManager.getInstance().voltarMenu();
    }

    private void mostrarAlerta(
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
