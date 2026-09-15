package com.abrigo.controller;

import javafx.fxml.FXML;
/**
 * Controller responsável pela tela de relatórios, contendo os relatórios de financeiro, doações, adoções e animais.
 * Camada: Controller
     * Arquivo Relacionado: Relatorios.fxml
 * @author Mizael
 */
public class RelatorioMenuController {

    @FXML
    private void abrirRelFinanceiro() {
        NavigationManager.getInstance().navegarConteudo("relatorio-financeiro");
    }

    @FXML
    private void abrirRelDoacao() {
        NavigationManager.getInstance().navegarConteudo("relatorio-doacao");
    }

    @FXML
    private void abrirRelAdocao() {
        NavigationManager.getInstance().navegarConteudo("relatorio-adocao");
    }

    @FXML
    private void abrirRelAnimais() {
        NavigationManager.getInstance().navegarConteudo("relatorio-animais");
    }

    @FXML
    private void sair() {
        NavigationManager.getInstance().voltarMenu();
    }
}
