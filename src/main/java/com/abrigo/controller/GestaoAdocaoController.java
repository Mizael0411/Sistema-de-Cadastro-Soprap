package com.abrigo.controller;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GestaoAdocaoController {

    @FXML private StackPane cardAdotados;
    @FXML private StackPane cardLarTemporario;
    @FXML private StackPane cardSemAdocao;

    @FXML
    public void initialize() {
        aplicarAnimacaoHover(cardAdotados);
        aplicarAnimacaoHover(cardLarTemporario);
        aplicarAnimacaoHover(cardSemAdocao);
    }

    private void aplicarAnimacaoHover(StackPane card) {

        DropShadow sombraBase = new DropShadow(16, 0, 6,
                Color.rgb(160, 110, 50, 0.12));
        sombraBase.setSpread(0);
        card.setEffect(sombraBase);


        card.setOnMouseEntered((MouseEvent e) -> {

            TranslateTransition tt = new TranslateTransition(Duration.millis(180), card);
            tt.setToY(-6);
            tt.setInterpolator(Interpolator.EASE_OUT);
            tt.play();

            ScaleTransition st = new ScaleTransition(Duration.millis(180), card);
            st.setToX(1.03);
            st.setToY(1.03);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();


            DropShadow sombraHover = new DropShadow(28, 0, 12,
                    Color.rgb(233, 30, 99, 0.28));
            sombraHover.setSpread(0.02);
            card.setEffect(sombraHover);

            card.setCursor(javafx.scene.Cursor.HAND);
        });


        card.setOnMouseExited((MouseEvent e) -> {
            TranslateTransition tt = new TranslateTransition(Duration.millis(200), card);
            tt.setToY(0);
            tt.setInterpolator(Interpolator.EASE_OUT);
            tt.play();

            ScaleTransition st = new ScaleTransition(Duration.millis(200), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();

            card.setEffect(sombraBase);
            card.setCursor(javafx.scene.Cursor.DEFAULT);
        });


        card.setOnMousePressed(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(90), card);
            st.setToX(0.98);
            st.setToY(0.98);
            st.play();
        });
        card.setOnMouseReleased(e -> {
            ScaleTransition st = new ScaleTransition(Duration.millis(120), card);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });
    }


    @FXML
    private void abrirAdotados() {
        NavigationManager.getInstance().navegarConteudo("telaAdocoesDefinitivas");
    }

    @FXML
    private void abrirLarTemporario() {
        NavigationManager.getInstance().navegarConteudo("telaAdocoesLartemporario");
    }

    @FXML
    private void abrirSemAdocao() {
        NavigationManager.getInstance().navegarConteudo("telaAnimaisSemAdocao");
    }
}