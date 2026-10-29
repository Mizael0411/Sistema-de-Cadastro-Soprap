package com.abrigo.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import com.abrigo.App;
import com.abrigo.model.Usuario;
import com.abrigo.service.SessaoUsuario;
import com.abrigo.service.UsuarioService;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController implements Initializable {

    @FXML
    private ComboBox<String> cbUsuario;

    @FXML
    private PasswordField txtSenha;

    @FXML
    private TextField txtSenhaVisivel;

    @FXML
    private ToggleButton btnToggleSenha;

    @FXML
    private ImageView imgIconeSenha;

    private Image imgCachorrinhoOculto;
    private Image imgCachorrinhoVisivel;

    private final UsuarioService usuarioService = new UsuarioService();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        carregarUsuarios();
        carregarIconesSenha();

        txtSenha.textProperty()
                .bindBidirectional(txtSenhaVisivel.textProperty());

        txtSenha.setOnAction(event -> onEntrarClick());
        txtSenhaVisivel.setOnAction(event -> onEntrarClick());

        txtSenha.setVisible(true);
        txtSenha.setManaged(true);

        txtSenhaVisivel.setVisible(false);
        txtSenhaVisivel.setManaged(false);
    }

    private void carregarIconesSenha() {
        try {
            imgCachorrinhoOculto = new Image(
                    getClass().getResourceAsStream(
                            "/com/abrigo/images/cachorro-olho-fechado.png"));

            imgCachorrinhoVisivel = new Image(
                    getClass().getResourceAsStream(
                            "/com/abrigo/images/cachorro-olho-aberto.png"));

        } catch (Exception e) {
            System.err.println(
                    "Erro ao carregar ícones do cachorrinho: "
                    + e.getMessage());
        }
    }

    private void carregarUsuarios() {
        try {
            List<String> listaUsuarios = usuarioService
                    .listarUsuariosAtivos()
                    .stream()
                    .map(Usuario::getNomeUsuario)
                    .toList();

            cbUsuario.setItems(
                    FXCollections.observableArrayList(listaUsuarios));

            if (!listaUsuarios.isEmpty()) {
                cbUsuario.setValue(listaUsuarios.get(0));
            }

        } catch (RuntimeException e) {
            e.printStackTrace();
            cbUsuario.getItems().clear();

            mostrarAlerta(
                    "Erro de conexão",
                    "Não foi possível carregar os usuários. "
                    + "Verifique a conexão com o banco de dados.");
        }
    }

    @FXML
    private void onToggleSenhaClick() {
        boolean mostrarSenha = btnToggleSenha.isSelected();

        if (mostrarSenha) {
            txtSenha.setVisible(false);
            txtSenha.setManaged(false);

            txtSenhaVisivel.setManaged(true);
            txtSenhaVisivel.setVisible(true);

            if (imgCachorrinhoVisivel != null) {
                imgIconeSenha.setImage(imgCachorrinhoVisivel);
            }

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

            if (imgCachorrinhoOculto != null) {
                imgIconeSenha.setImage(imgCachorrinhoOculto);
            }

            Platform.runLater(() -> {
                txtSenha.requestFocus();
                txtSenha.positionCaret(
                        txtSenha.getText().length());
            });
        }
    }

    @FXML
    private void onEntrarClick() {
        String nomeUsuario = cbUsuario.getValue();

        String senha = txtSenha.getText();

        if (nomeUsuario == null || nomeUsuario.isBlank()) {
            mostrarAlerta(
                    "Aviso",
                    "Por favor, selecione um usuário para acessar o sistema.");
            cbUsuario.requestFocus();
            return;
        }

        if (senha == null || senha.isEmpty()) {
            mostrarAlerta("Aviso", "Digite sua senha.");

            if (btnToggleSenha.isSelected()) {
                txtSenhaVisivel.requestFocus();
            } else {
                txtSenha.requestFocus();
            }

            return;
        }

        try {
            Optional<Usuario> resultado =
                    usuarioService.autenticar(nomeUsuario, senha);

            if (resultado.isEmpty()) {
                mostrarAlerta(
                        "Acesso negado",
                        "Usuário ou senha inválidos, "
                        + "ou usuário inativo.");

                txtSenha.clear();

                if (btnToggleSenha.isSelected()) {
                    txtSenhaVisivel.requestFocus();
                } else {
                    txtSenha.requestFocus();
                }

                return;
            }

            Usuario usuarioAutenticado = resultado.get();
            SessaoUsuario.iniciar(usuarioAutenticado);

            try {
                App.trocarCena("main-layout");

            } catch (IOException e) {
                SessaoUsuario.encerrar();
                e.printStackTrace();

                mostrarAlerta(
                        "Erro de carregamento",
                        "Não foi possível carregar o layout principal.");
            }

        } catch (RuntimeException e) {
            e.printStackTrace();

            mostrarAlerta(
                    "Erro de autenticação",
                    "Não foi possível validar o acesso. "
                    + "Verifique a conexão com o banco de dados.");
        }
    }

    @FXML
    private void onSairAplicativoClick() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);

        alerta.setTitle("Sair do aplicativo");
        alerta.setHeaderText(null);
        alerta.setContentText(
                "Deseja realmente fechar o sistema SOPRAP?");

        alerta.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.OK) {
                SessaoUsuario.encerrar();
                Platform.exit();
            }
        });
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);

        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);

        alert.showAndWait();
    }
}