package com.abrigo.controller;

import com.abrigo.model.NavigationManager;
import com.abrigo.service.SessaoUsuario;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

public class MenuPrincipalController {

    @FXML
    private Button btnGerenciarUsuarios;

    @FXML
    private Button btnAuditoriaSistema;

    @FXML
    private void initialize() {
        boolean administradora = SessaoUsuario.isAdministradora();

        if (btnGerenciarUsuarios != null) {
            btnGerenciarUsuarios.setDisable(administradora);
            btnGerenciarUsuarios.setManaged(administradora);
            btnGerenciarUsuarios.setDisable(false);
        }

        if (btnAuditoriaSistema != null) {
            btnAuditoriaSistema.setVisible(administradora);
            btnAuditoriaSistema.setManaged(administradora);
        }
    }

    @FXML
    private void abrirCadastro() {
        NavigationManager.getInstance().navegarMenu("menu-cadastro");
    }

    @FXML
    private void abrirRelatorios() {
        NavigationManager.getInstance().navegarMenu("relatorios");
    }

    @FXML
    private void abrirGestaoAdocao() {
        NavigationManager.getInstance().navegarMenu("menu-principal");
        NavigationManager.getInstance().navegarConteudo("gestao-adocao");
    }

    @FXML
    private void abrirFichaTecnicaAnimais() {
        NavigationManager.getInstance()
            .navegarConteudo("selecao-animal-ficha");
    }

    @FXML
    private void abrirGerenciamentoUsuarios() {
        if (!SessaoUsuario.isAdministradora()) {
            mostrarAcessoNegado(
                "Somente a administradora pode gerenciar usuários."
            );
            return;
        }

        NavigationManager.getInstance()
            .navegarConteudo("gestao-usuarios");
    }

    @FXML
    private void abrirAuditoriaSistema() {
        if (!SessaoUsuario.isAdministradora()) {
            mostrarAcessoNegado(
                "Somente a administradora pode consultar a auditoria."
            );
            return;
        }

        NavigationManager.getInstance()
            .navegarConteudo("auditoria-sistema");
    }

    private void mostrarAcessoNegado(String mensagem) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Acesso negado");
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    @FXML
    private void sair() {
        try {
            SessaoUsuario.encerrar();
            com.abrigo.App.trocarCena("login");
        } catch (java.io.IOException e) {
            e.printStackTrace();

            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Erro");
            alerta.setHeaderText(null);
            alerta.setContentText(
                "Não foi possível voltar à tela de login."
            );
            alerta.showAndWait();
        }
    }
}
