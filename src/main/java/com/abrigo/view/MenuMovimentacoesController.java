package com.abrigo.view;

import javafx.fxml.FXML;
/**
 * Controller responsável pela tela de movimentações.
 * - Contendo as telas de regitro de doação, registro de adoção, compras, cadastro de animal e registro de lar temporário.
 * Camada: Controller
 * Arquivo Relacionado: Movimentacao.fxml
 * @author Mizael
 */
public class MenuMovimentacoesController {

    @FXML
    private void abrirRegistroDoacao() {
        NavigationManager.getInstance().navegarConteudo("registro-doacao");
    }

    @FXML
    private void abrirRegistroAdocao() {
        NavigationManager.getInstance().navegarConteudo("registro-adocao");
    }

    @FXML
    private void abrirCompras() {
        NavigationManager.getInstance().navegarConteudo("compras");
    }

    @FXML
    private void abrirCAnimal() {
        NavigationManager.getInstance().navegarConteudo("cadastro-animal");
    }

    @FXML
    private void abrirRGLartemporario() {
        NavigationManager.getInstance().navegarConteudo("registro-animal-lar-temporário");
    }



    @FXML
    private void voltar() {
        NavigationManager.getInstance().voltarMenu();
    }
}