package com.abrigo.controller;

import java.net.URL;
import java.util.ResourceBundle;

import com.abrigo.model.NavigationManager;
import com.abrigo.model.Usuario;
import com.abrigo.service.SessaoUsuario;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

public class MainLayoutController implements Initializable {

    @FXML
    private StackPane areaMenu;

    @FXML
    private StackPane areaConteudo;

    @FXML
    private Label lblUsuarioAtual;

    @FXML
    private Label lblPerfilAtual;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        NavigationManager nav = NavigationManager.getInstance();

        nav.setMenuContainer(areaMenu);
        nav.setContentContainer(areaConteudo);
        nav.navegarMenu("menu-principal");
        nav.navegarConteudo("boas-vindas");

        carregarUsuarioAtual();
    }

    private void carregarUsuarioAtual() {
        Usuario usuario = SessaoUsuario.getUsuarioAtual();

        if (usuario == null) {
            lblUsuarioAtual.setText("Usuário não identificado");
            lblPerfilAtual.setText("");
            return;
        }

        lblUsuarioAtual.setText(usuario.getNomeUsuario());
        lblPerfilAtual.setText(formatarPerfil(usuario.getPerfil()));
    }

    private String formatarPerfil(String perfil) {
        if (perfil == null || perfil.isBlank()) {
            return "Funcionário";
        }

        if ("ADMINISTRADORA".equals(perfil)) {
            return "Administradora";
        }

        return "Funcionário";
    }
}