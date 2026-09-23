package com.abrigo.controller;

import javafx.fxml.FXML;

/**
 * Controller responsável pela tela de menu principal e navegação entre as telas.
 * - Contendo as telas de cadastro, movimentações e relatórios
 * Camada: Controller
 * Arquivo Relacionado: main-Layout.fxml
 * @author Mizael
 */
public class MenuPrincipalController {

    @FXML
    private void abrirCadastro() {
        // Carrega apenas o menu lateral de cadastros sem abrir telas centrais automaticamente
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
       NavigationManager.getInstance().navegarConteudo("selecao-animal-ficha");
    }

    @FXML
    private void sair() {
        System.exit(0);
    }
}