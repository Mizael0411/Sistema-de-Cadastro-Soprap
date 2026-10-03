package com.abrigo.controller;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import com.abrigo.App;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class LoginController implements Initializable {

    @FXML private ComboBox<String> cbUsuario;
    @FXML private PasswordField txtSenha;
    @FXML private TextField txtSenhaVisivel;
    @FXML private ToggleButton btnToggleSenha;
    @FXML private ImageView imgIconeSenha;

    private Image imgCachorrinhoOculto;
    private Image imgCachorrinhoVisivel;

    private final Map<String, String> usuariosFake = Map.of(
            "admin", "1234"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        List<String> listaUsuarios = List.copyOf(usuariosFake.keySet());
        cbUsuario.setItems(FXCollections.observableArrayList(listaUsuarios));

        if (!listaUsuarios.isEmpty()) {
            cbUsuario.setValue(listaUsuarios.get(0));
        }

        // Carrega as imagens renomeadas a partir da pasta de recursos
        try {
            imgCachorrinhoOculto = new Image(getClass().getResourceAsStream("/com/abrigo/images/cachorro-olho-fechado.png"));
            imgCachorrinhoVisivel = new Image(getClass().getResourceAsStream("/com/abrigo/images/cachorro-olho-aberto.png"));
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícones do cachorrinho: " + e.getMessage());
        }

        // Sincroniza o conteúdo digitado entre o campo oculto e o visível
        txtSenha.textProperty().bindBidirectional(txtSenhaVisivel.textProperty());

        // Ação ao pressionar a tecla Enter em qualquer um dos campos
        txtSenha.setOnAction(event -> onEntrarClick());
        txtSenhaVisivel.setOnAction(event -> onEntrarClick());
    }

    @FXML
    private void onToggleSenhaClick() {
        if (btnToggleSenha.isSelected()) {
            // Exibe a senha em texto claro e altera a imagem para o cachorro com olho aberto
            txtSenhaVisivel.setVisible(true);
            txtSenha.setVisible(false);
            if (imgCachorrinhoVisivel != null) {
                imgIconeSenha.setImage(imgCachorrinhoVisivel);
            }
            txtSenhaVisivel.positionCaret(txtSenhaVisivel.getText().length());
        } else {
            // Oculta a senha em asteriscos e altera a imagem para o cachorro com olho fechado
            txtSenhaVisivel.setVisible(false);
            txtSenha.setVisible(true);
            if (imgCachorrinhoOculto != null) {
                imgIconeSenha.setImage(imgCachorrinhoOculto);
            }
            txtSenha.positionCaret(txtSenha.getText().length());
        }
    }

    @FXML
    private void onEntrarClick() {
        String usuario = cbUsuario.getValue();
        String senha = txtSenha.getText();

        if (usuario == null || usuario.isBlank()) {
            mostrarAlerta("Aviso", "Por favor, selecione um usuário para acessar o sistema.");
            return;
        }

        String senhaCorreta = usuariosFake.get(usuario);

        if (senhaCorreta != null && senhaCorreta.equals(senha)) {
            try {
                App.trocarCena("main-layout");
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlerta("Erro de Carregamento", "Não foi possível carregar o layout principal.");
            }
        } else {
            mostrarAlerta("Acesso Negado", "Senha incorreta. Tente novamente.");
            txtSenha.clear();
            if (btnToggleSenha.isSelected()) {
                txtSenhaVisivel.requestFocus();
            } else {
                txtSenha.requestFocus();
            }
        }
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}