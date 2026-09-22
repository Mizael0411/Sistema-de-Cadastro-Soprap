package com.abrigo.controller;

import javafx.fxml.FXML;

public class MenuPrincipalController {

    @FXML
    private void abrirCadastro() {
        NavigationManager.getInstance().navegarMenu("menu-cadastro");
    }

    @FXML
    private void abrirRelatorios() {
        NavigationManager.getInstance().navegarMenu("relatorios");
    }

    @FXML
    private void abrirFichaTecnicaAnimais() {
       NavigationManager.getInstance().navegarConteudo("selecao-animal-ficha");
    }

    @FXML
    private void sair() {
        System.exit(0);
    }
}