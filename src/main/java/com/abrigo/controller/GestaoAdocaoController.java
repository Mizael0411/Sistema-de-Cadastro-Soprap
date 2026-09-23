package com.abrigo.controller;

import com.abrigo.model.Animal;
import com.abrigo.repository.AdocaoRepository;
import com.abrigo.util.SelecionarLarTempDialog;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GestaoAdocaoController {

    @FXML private StackPane cardAdotados;
    @FXML private StackPane cardLarTemporario;
    @FXML private StackPane cardSemAdocao;

    private final AdocaoRepository adocaoRepository = new AdocaoRepository();

    // Guardado só enquanto essa tela está aberta: setado pelo Histórico antes
    // de navegar pra cá, e consumido assim que o usuário escolhe um card.
    private Animal animalParaTransferencia;

    @FXML
    public void initialize() {
        aplicarAnimacaoHover(cardAdotados);
        aplicarAnimacaoHover(cardLarTemporario);
        aplicarAnimacaoHover(cardSemAdocao);
    }

    // Chamado pelo HistoricoAnimaisCadastradosController.transferirAdocao()
    // logo após a navegação pra essa tela.
    public void carregarDadosParaTransferenciaAdocao(Animal animal) {
        this.animalParaTransferencia = animal;
    }

    @FXML
    private void abrirAdotados() {
        if (animalParaTransferencia != null) {
            registrarDefinitivaEAbrir(animalParaTransferencia);
        } else {
            NavigationManager.getInstance().navegarConteudo("telaAdocoesDefinitivas");
        }
    }

    @FXML
    private void abrirLarTemporario() {
        if (animalParaTransferencia != null) {
            registrarLarTemporarioEAbrir(animalParaTransferencia);
        } else {
            NavigationManager.getInstance().navegarConteudo("telaAdocoesLartemporario");
        }
    }

    @FXML
    private void abrirSemAdocao() {
        // Um animal recém-cadastrado já aparece aqui naturalmente (nenhum
        // registro em Adocao ainda) — não precisa registrar nada, só limpa
        // a transferência pendente caso o usuário desista dela.
        animalParaTransferencia = null;
        NavigationManager.getInstance().navegarConteudo("telaAnimaisSemAdocao");
    }

    private void registrarDefinitivaEAbrir(Animal animal) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Adoção Definitiva");
        dialog.setHeaderText("Adotar " + animal.getNome());
        dialog.setContentText("Nome do tutor:");

        dialog.showAndWait().ifPresent(nomeTutor -> {
            if (nomeTutor.isBlank()) {
                return;
            }
            adocaoRepository.registrarAdocaoDefinitiva(animal.getId(), nomeTutor);
            animalParaTransferencia = null;

            AdocoesDefinitivasController destino = (AdocoesDefinitivasController)
                    NavigationManager.getInstance().navegarConteudoEObterController("telaAdocoesDefinitivas");
            if (destino != null) {
                destino.destacarAnimalRecemAdicionado(animal.getId());
            }
        });
    }

    private void registrarLarTemporarioEAbrir(Animal animal) {
        SelecionarLarTempDialog.abrir().ifPresent(larEscolhido -> {
            adocaoRepository.registrarAdocaoLarTemporario(
                    animal.getId(), larEscolhido.getId(), larEscolhido.getNome());
            animalParaTransferencia = null;

            AdocoesLarTemporarioController destino = (AdocoesLarTemporarioController)
                    NavigationManager.getInstance().navegarConteudoEObterController("telaAdocoesLartemporario");
            if (destino != null) {
                destino.destacarAnimalRecemAdicionado(animal.getId());
            }
        });
    }

    // --- animação de hover, sem mudanças em relação ao que você já tinha ---
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
}