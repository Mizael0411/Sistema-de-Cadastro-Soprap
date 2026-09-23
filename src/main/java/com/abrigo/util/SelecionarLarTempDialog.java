package com.abrigo.util;

import java.util.List;
import java.util.Optional;

import com.abrigo.dao.LarTemporarioDAO;
import com.abrigo.model.LarTemp;

import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;

public final class SelecionarLarTempDialog {

    private SelecionarLarTempDialog() {
    }

    // Abre um diálogo pequeno com um ComboBox listando "id - nome" de cada
    // lar temporário. Retorna vazio se o usuário cancelar ou não houver
    // nenhum lar cadastrado.
    public static Optional<LarTemp> abrir() {
        List<LarTemp> lares = new LarTemporarioDAO().listarTodos();
        if (lares.isEmpty()) {
            new Alert(AlertType.WARNING, "Nenhum lar temporário cadastrado.").showAndWait();
            return Optional.empty();
        }

        Dialog<LarTemp> dialog = new Dialog<>();
        dialog.setTitle("Lar Temporário");
        dialog.setHeaderText("Selecione o responsável pelo lar temporário");

        ComboBox<LarTemp> combo = new ComboBox<>(FXCollections.observableArrayList(lares));
        combo.setPrefWidth(280);
        combo.setCellFactory(lv -> criarCelula());
        combo.setButtonCell(criarCelula());
        combo.getSelectionModel().selectFirst();

        VBox conteudo = new VBox(10, new Label("Lar temporário:"), combo);
        conteudo.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(conteudo);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(botao -> botao == ButtonType.OK ? combo.getValue() : null);

        return dialog.showAndWait();
    }

    private static ListCell<LarTemp> criarCelula() {
        return new ListCell<>() {
            @Override
            protected void updateItem(LarTemp lar, boolean empty) {
                super.updateItem(lar, empty);
                setText(empty || lar == null ? null : lar.getId() + " - " + lar.getNome());
            }
        };
    }
}
