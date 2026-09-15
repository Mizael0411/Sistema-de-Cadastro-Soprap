package com.abrigo.view;

import javafx.fxml.FXML;

public class MenuCadastroController {
    /**
     * Controller responsável pela tela de cadastros. Contendo as telas para cadstro de animais, lar temporário, produtos e doações.
     * Camada: Controller
     * Arquivo Relacionado: menu-cadastro.fxml
     * @author Mizael
     */
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
