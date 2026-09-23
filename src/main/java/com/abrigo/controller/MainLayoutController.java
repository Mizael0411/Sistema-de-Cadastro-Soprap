package com.abrigo.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;


public class MainLayoutController implements Initializable {

    @FXML private StackPane areaMenu;
    @FXML private StackPane areaConteudo;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        NavigationManager nav = NavigationManager.getInstance();
        nav.setMenuContainer(areaMenu);
        nav.setContentContainer(areaConteudo);
        nav.navegarMenu("menu-principal");
        nav.navegarConteudo("boas-vindas");
    }
}