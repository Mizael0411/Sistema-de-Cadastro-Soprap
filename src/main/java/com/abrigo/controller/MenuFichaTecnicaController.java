package com.abrigo.controller;

import com.abrigo.model.NavigationManager;

import javafx.fxml.FXML;

public class MenuFichaTecnicaController {
    
    @FXML
    private void voltar() {
        NavigationManager.getInstance().voltarMenu();
    }
}