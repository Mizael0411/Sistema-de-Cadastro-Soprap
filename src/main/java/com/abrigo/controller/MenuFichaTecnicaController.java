package com.abrigo.controller;

import javafx.fxml.FXML;
/**
 * Controller responsável pela tela de movimentações.
 * - Contendo as telas de regitro de doação, registro de adoção, compras, cadastro de animal e registro de lar temporário.
 * Camada: Controller
 * Arquivo Relacionado: FichaTecnica.fxml
 * @author Mizael
 */
public class MenuFichaTecnicaController {



    @FXML
    private void voltar() {
        NavigationManager.getInstance().voltarMenu();
    }
}