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

public class LoginController implements Initializable {

    @FXML private ComboBox<String> cbUsuario;
    @FXML private PasswordField txtSenha;

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

        txtSenha.setOnAction(event -> onEntrarClick());
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
            txtSenha.requestFocus();
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