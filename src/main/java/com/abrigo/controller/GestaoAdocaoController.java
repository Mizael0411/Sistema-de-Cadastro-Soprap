package com.abrigo.controller;

import com.abrigo.model.Animal;
import com.abrigo.model.NavigationManager;
import com.abrigo.repository.AdocaoRepository;
import com.abrigo.util.FormatadorTelefone;
import com.abrigo.util.SelecionarLarTempDialog;

import javafx.animation.Interpolator;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class GestaoAdocaoController {

    @FXML
    private StackPane cardAdotados;

    @FXML
    private StackPane cardLarTemporario;

    @FXML
    private StackPane cardSemAdocao;

    private final AdocaoRepository adocaoRepository = new AdocaoRepository();

    private Animal animalParaTransferencia;

    @FXML
    public void initialize() {
        aplicarAnimacaoHover(cardAdotados);
        aplicarAnimacaoHover(cardLarTemporario);
        aplicarAnimacaoHover(cardSemAdocao);
    }

    public void carregarDadosParaTransferenciaAdocao(Animal animal) {
        this.animalParaTransferencia = animal;
    }

    @FXML
    private void abrirAdotados() {
        if (animalParaTransferencia != null) {
            registrarDefinitivaEAbrir(animalParaTransferencia);
        } else {
            NavigationManager.getInstance()
                    .navegarConteudo("telaAdocoesDefinitivas");
        }
    }

    @FXML
    private void abrirLarTemporario() {
        if (animalParaTransferencia != null) {
            registrarLarTemporarioEAbrir(animalParaTransferencia);
        } else {
            NavigationManager.getInstance()
                    .navegarConteudo("telaAdocoesLartemporario");
        }
    }

    @FXML
    private void abrirSemAdocao() {
        animalParaTransferencia = null;
        NavigationManager.getInstance()
                .navegarConteudo("telaAnimaisSemAdocao");
    }

    private void registrarDefinitivaEAbrir(Animal animal) {
        if (adocaoRepository.possuiLarTemporarioAtivo(animal.getId())) {
            new Alert(AlertType.WARNING,
                    animal.getNome() + " está em um lar temporário e não "
                            + "pode ser adotado definitivamente. "
                            + "Cancele o lar temporário antes de continuar.")
                    .showAndWait();
            return;
        }

        if (adocaoRepository.possuiAdocaoDefinitivaAtiva(animal.getId())) {
            new Alert(AlertType.WARNING,
                    animal.getNome() + " já foi adotado definitivamente.")
                    .showAndWait();
            return;
        }

        TextInputDialog dialogNome = new TextInputDialog();
        dialogNome.setTitle("Adoção Definitiva");
        dialogNome.setHeaderText("Adotar " + animal.getNome());
        dialogNome.setContentText("Nome do tutor:");

        dialogNome.showAndWait().ifPresent(nomeTutor -> {
            if (nomeTutor.isBlank()) {
                new Alert(AlertType.WARNING,
                        "Informe o nome do tutor.").showAndWait();
                return;
            }

            TextInputDialog dialogTelefone = new TextInputDialog();
            dialogTelefone.setTitle("Adoção Definitiva");
            dialogTelefone.setHeaderText("Contato do tutor");
            dialogTelefone.setContentText(
                    "Telefone do tutor: (XX) XXXX-XXXX ou (XX) 9XXXX-XXXX");

            dialogTelefone.getEditor().setTextFormatter(
                    FormatadorTelefone.criarTextFormatter());

            dialogTelefone.showAndWait().ifPresent(telefoneTutor -> {
                if (telefoneTutor.isBlank()) {
                    new Alert(AlertType.WARNING,
                            "Informe o telefone do tutor.").showAndWait();
                    return;
                }

                if (!FormatadorTelefone.telefoneValido(telefoneTutor)) {
                    new Alert(AlertType.WARNING,
                            "Informe um telefone válido com DDD: "
                                    + "10 dígitos para telefone fixo ou "
                                    + "11 dígitos para celular.")
                            .showAndWait();
                    return;
                }

                try {
                    adocaoRepository.registrarAdocaoDefinitiva(
                            animal.getId(),
                            nomeTutor.trim(),
                            FormatadorTelefone.somenteDigitos(telefoneTutor));

                } catch (IllegalStateException e) {
                    new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
                    return;
                } catch (RuntimeException e) {
                    new Alert(AlertType.ERROR,
                            "Não foi possível registrar a adoção.").showAndWait();
                    e.printStackTrace();
                    return;
                }

                animalParaTransferencia = null;

                AdocoesDefinitivasController destino =
                        (AdocoesDefinitivasController)
                        NavigationManager.getInstance()
                                .navegarConteudoEObterController(
                                        "telaAdocoesDefinitivas");

                if (destino != null) {
                    destino.destacarAnimalRecemAdicionado(animal.getId());
                }
            });
        });
    }

    private void registrarLarTemporarioEAbrir(Animal animal) {
        if (adocaoRepository.possuiAdocaoAtiva(animal.getId())) {
            new Alert(AlertType.WARNING,
                    animal.getNome() + " já possui um registro de adoção "
                            + "ativo (lar temporário ou definitiva).")
                    .showAndWait();
            return;
        }

        SelecionarLarTempDialog.abrir().ifPresent(larEscolhido -> {
            try {
                adocaoRepository.registrarAdocaoLarTemporario(
                        animal.getId(),
                        larEscolhido.getId(),
                        larEscolhido.getNome());

            } catch (IllegalStateException e) {
                new Alert(AlertType.ERROR, e.getMessage()).showAndWait();
                return;
            } catch (RuntimeException e) {
                new Alert(AlertType.ERROR,
                        "Não foi possível registrar a adoção.").showAndWait();
                e.printStackTrace();
                return;
            }

            animalParaTransferencia = null;

            AdocoesLarTemporarioController destino =
                    (AdocoesLarTemporarioController)
                    NavigationManager.getInstance()
                            .navegarConteudoEObterController(
                                    "telaAdocoesLartemporario");

            if (destino != null) {
                destino.destacarAnimalRecemAdicionado(animal.getId());
            }
        });
    }

    private void aplicarAnimacaoHover(StackPane card) {
        if (card == null) {
            return;
        }

        DropShadow sombraBase = new DropShadow(
                16, 0, 6, Color.rgb(160, 110, 50, 0.12));
        sombraBase.setSpread(0);
        card.setEffect(sombraBase);

        card.setOnMouseEntered((MouseEvent e) -> {
            TranslateTransition tt =
                    new TranslateTransition(Duration.millis(180), card);
            tt.setToY(-6);
            tt.setInterpolator(Interpolator.EASE_OUT);
            tt.play();

            ScaleTransition st =
                    new ScaleTransition(Duration.millis(180), card);
            st.setToX(1.03);
            st.setToY(1.03);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();

            DropShadow sombraHover = new DropShadow(
                    28, 0, 12, Color.rgb(233, 30, 99, 0.28));
            sombraHover.setSpread(0.02);
            card.setEffect(sombraHover);

            card.setCursor(javafx.scene.Cursor.HAND);
        });

        card.setOnMouseExited(e -> {
            TranslateTransition tt =
                    new TranslateTransition(Duration.millis(200), card);
            tt.setToY(0);
            tt.setInterpolator(Interpolator.EASE_OUT);
            tt.play();

            ScaleTransition st =
                    new ScaleTransition(Duration.millis(200), card);
            st.setToX(1.0);
            st.setToY(1.0);
            st.setInterpolator(Interpolator.EASE_OUT);
            st.play();

            card.setEffect(sombraBase);
            card.setCursor(javafx.scene.Cursor.DEFAULT);
        });

        card.setOnMousePressed(e -> {
            ScaleTransition st =
                    new ScaleTransition(Duration.millis(90), card);
            st.setToX(0.98);
            st.setToY(0.98);
            st.play();
        });

        card.setOnMouseReleased(e -> {
            ScaleTransition st =
                    new ScaleTransition(Duration.millis(120), card);
            st.setToX(1.03);
            st.setToY(1.03);
            st.play();
        });
    }
}