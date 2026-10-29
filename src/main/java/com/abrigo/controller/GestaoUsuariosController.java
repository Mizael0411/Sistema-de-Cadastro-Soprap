package com.abrigo.controller;

import java.io.InputStream;
import java.util.Optional;

import com.abrigo.model.Usuario;
import com.abrigo.service.SessaoUsuario;
import com.abrigo.service.UsuarioService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

public class GestaoUsuariosController {

    @FXML
    private TextField txtNome;

    @FXML
    private TextField txtNomeUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private TextField txtSenhaVisivel;

    @FXML
    private ToggleButton btnToggleSenha;

    @FXML
    private ImageView imgIconeSenha;

    @FXML
    private TableView<Usuario> tabelaUsuarios;

    @FXML
    private TableColumn<Usuario, String> colNome;

    @FXML
    private TableColumn<Usuario, String> colNomeUsuario;

    @FXML
    private TableColumn<Usuario, String> colPerfil;

    @FXML
    private TableColumn<Usuario, Boolean> colAtivo;

    private final UsuarioService usuarioService = new UsuarioService();

    private Image iconeOlho;
    private Image iconeOlhoOculto;

    @FXML
    private void initialize() {

        txtSenha.textProperty()
                .bindBidirectional(txtSenhaVisivel.textProperty());

        txtSenha.setVisible(true);
        txtSenha.setManaged(true);

        txtSenhaVisivel.setVisible(false);
        txtSenhaVisivel.setManaged(false);

        carregarIcones();

        if (!SessaoUsuario.isAdministradora()) {
            mostrarAcessoNegado();
            return;
        }

        colNome.setCellValueFactory(
                new PropertyValueFactory<>("nome"));

        colNomeUsuario.setCellValueFactory(
                new PropertyValueFactory<>("nomeUsuario"));

        colPerfil.setCellValueFactory(
                new PropertyValueFactory<>("perfil"));

        colAtivo.setCellValueFactory(
                new PropertyValueFactory<>("ativo"));

        colAtivo.setCellFactory(
                new Callback<TableColumn<Usuario, Boolean>,
                             TableCell<Usuario, Boolean>>() {

                    @Override
                    public TableCell<Usuario, Boolean> call(
                            TableColumn<Usuario, Boolean> coluna) {

                        return new TableCell<Usuario, Boolean>() {

                            @Override
                            protected void updateItem(
                                    Boolean ativo, boolean vazio) {

                                super.updateItem(ativo, vazio);

                                if (vazio || ativo == null) {
                                    setText(null);
                                } else {
                                    setText(ativo ? "Ativo" : "Inativo");
                                }
                            }
                        };
                    }
                });

        atualizarLista();
    }

    private void carregarIcones() {

        iconeOlho = carregarImagem(
                "/com/abrigo/images/cachorro-olho-aberto.png");

        iconeOlhoOculto = carregarImagem(
                "/com/abrigo/images/cachorro-olho-fechado.png");

        imgIconeSenha.setImage(iconeOlhoOculto);
    }

    private Image carregarImagem(String caminho) {

        InputStream stream = getClass().getResourceAsStream(caminho);

        if (stream == null) {
            throw new IllegalStateException(
                    "Não foi possível encontrar a imagem: " + caminho
            );
        }

        try (stream) {
            return new Image(stream);
        } catch (Exception e) {
            throw new IllegalStateException(
                    "Erro ao carregar a imagem: " + caminho, e
            );
        }
    }

    @FXML
    private void alternarVisibilidadeSenha() {

        boolean mostrar = btnToggleSenha.isSelected();

        if (mostrar) {

            txtSenha.setVisible(false);
            txtSenha.setManaged(false);

            txtSenhaVisivel.setManaged(true);
            txtSenhaVisivel.setVisible(true);

            imgIconeSenha.setImage(iconeOlho);

            Platform.runLater(() -> {
                txtSenhaVisivel.requestFocus();
                txtSenhaVisivel.positionCaret(
                        txtSenhaVisivel.getText().length());
            });

        } else {

            txtSenhaVisivel.setVisible(false);
            txtSenhaVisivel.setManaged(false);

            txtSenha.setManaged(true);
            txtSenha.setVisible(true);

            imgIconeSenha.setImage(iconeOlhoOculto);

            Platform.runLater(() -> {
                txtSenha.requestFocus();
                txtSenha.positionCaret(
                        txtSenha.getText().length());
            });
        }
    }

    @FXML
    private void atualizarLista() {

        if (!SessaoUsuario.isAdministradora()) {
            mostrarAcessoNegado();
            return;
        }

        try {
            tabelaUsuarios.setItems(
                    FXCollections.observableArrayList(
                            usuarioService.listarTodos()
                                    .stream()
                                    .filter(usuario ->
                                            Boolean.TRUE.equals(
                                                    usuario.getAtivo()))
                                    .toList()
                    )
            );

        } catch (RuntimeException e) {
            e.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível carregar os usuários."
            );
        }
    }

    @FXML
    private void cadastrarFuncionario() {

        if (!SessaoUsuario.isAdministradora()) {
            mostrarAcessoNegado();
            return;
        }

        try {
            usuarioService.cadastrarFuncionario(
                    txtNome.getText(),
                    txtNomeUsuario.getText(),
                    txtSenha.getText()
            );

            txtNome.clear();
            txtNomeUsuario.clear();
            txtSenha.clear();

            btnToggleSenha.setSelected(false);

            txtSenhaVisivel.setVisible(false);
            txtSenhaVisivel.setManaged(false);

            txtSenha.setVisible(true);
            txtSenha.setManaged(true);

            imgIconeSenha.setImage(iconeOlho);

            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Sucesso",
                    "Funcionário cadastrado com sucesso."
            );

            atualizarLista();

        } catch (IllegalArgumentException e) {

            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Verifique os dados",
                    e.getMessage()
            );

        } catch (RuntimeException e) {

            e.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível cadastrar o funcionário."
            );
        }
    }

    @FXML
    private void desativarSelecionado() {

        if (!SessaoUsuario.isAdministradora()) {
            mostrarAcessoNegado();
            return;
        }

        Usuario selecionado =
                tabelaUsuarios.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Nenhum usuário selecionado",
                    "Selecione um usuário na tabela."
            );
            return;
        }

        if (!Boolean.TRUE.equals(selecionado.getAtivo())) {
            mostrarAlerta(
                    Alert.AlertType.INFORMATION,
                    "Conta já desativada",
                    "Esse usuário já está desativado."
            );
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION);

        confirmacao.setTitle("Confirmar desativação");
        confirmacao.setHeaderText(null);
        confirmacao.setContentText(
                "Deseja desativar a conta de "
                        + selecionado.getNomeUsuario() + "?");

        Optional<ButtonType> resposta =
                confirmacao.showAndWait();

        if (resposta.isPresent()
                && resposta.get() == ButtonType.OK) {

            try {
                usuarioService.desativarUsuario(
                        selecionado.getId());

                mostrarAlerta(
                        Alert.AlertType.INFORMATION,
                        "Sucesso",
                        "Conta desativada com sucesso."
                );

                atualizarLista();

            } catch (IllegalArgumentException e) {

                mostrarAlerta(
                        Alert.AlertType.WARNING,
                        "Operação não permitida",
                        e.getMessage()
                );

            } catch (RuntimeException e) {

                e.printStackTrace();

                mostrarAlerta(
                        Alert.AlertType.ERROR,
                        "Erro",
                        "Não foi possível desativar a conta."
                );
            }
        }
    }

    private void mostrarAcessoNegado() {

        mostrarAlerta(
                Alert.AlertType.ERROR,
                "Acesso negado",
                "Somente a administradora pode executar esta operação."
        );
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