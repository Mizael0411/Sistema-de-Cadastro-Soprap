package com.abrigo.controller;

import javafx.fxml.FXML;

public class MenuCadastroController {
 
    @FXML
    private void abrirCadastroAnimal() {
        NavigationManager.getInstance().navegarConteudo("cadastro-animal");
    }
    @FXML
    private void abrirCadastroLarTemporario() {
        NavigationManager.getInstance().navegarConteudo("cadastro-lar-temp");
    }

    @FXML
    private void abrirCadastroProdutos() {
        NavigationManager.getInstance().navegarConteudo("cadastro-produto");
    }

    @FXML
    private void abrirCadastroDoacoes() {
        NavigationManager.getInstance().navegarConteudo("cadastro-doacoes");
    }

    @FXML 
    private void abrirRegistrosDeVacinas(){
        NavigationManager.getInstance().navegarConteudo("registrar-vacinas");
    }

    @FXML
    private void voltar() {
        NavigationManager.getInstance().voltarMenu();

    }

    @FXML
    private void abrirAnimaisCadastrados() {
        NavigationManager.getInstance().navegarConteudo("animais-cadastrados");
    }

}
