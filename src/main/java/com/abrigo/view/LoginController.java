package com.abrigo.view;

import com.abrigo.App;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller responsável pela tela de login.
 * Camada: Controller
 * Arquivo Relacionado: login.fxml
 *
 * @author Mizael
 */
public class LoginController implements Initializable {

    @FXML private ComboBox<String> cbUsuario;
    @FXML private PasswordField txtSenha;

    // Simula os usuários que viriam do banco de dados (RF08.12)
    // Quando o banco estiver pronto, essa lista/mapa sai daqui e
    // passa a vir de um UsuarioService (Fake -> Impl)
    private final Map<String, String> usuariosFake = Map.of(
            "admin", "1234"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cbUsuario.setItems(javafx.collections.FXCollections.observableArrayList(
                List.copyOf(usuariosFake.keySet())
        ));
    }

    @FXML
    private void onEntrarClick() {
        String usuario = cbUsuario.getValue();
        String senha = txtSenha.getText();

        if (usuario == null) {
            System.out.println("Selecione um usuário");
            return;
        }

        String senhaCorreta = usuariosFake.get(usuario);

        if (senhaCorreta != null && senhaCorreta.equals(senha)) {
            try {
                App.trocarCena("main-layout");
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("Usuário ou senha inválidos");
        }
    }
}