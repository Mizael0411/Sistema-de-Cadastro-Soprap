package com.abrigo.util;

import java.util.List;
import java.util.Optional;

import com.abrigo.dao.LarTemporarioDAO;
import com.abrigo.model.LarTemp;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.VBox;

public final class SelecionarLarTempDialog {

    private SelecionarLarTempDialog() {
    }

    // Abre um diálogo pequeno com uma tabela (ID / Responsável) de lares
    // temporários. O "responsável" já é o próprio tutor da adoção temporária,
    // então não pergunta nome de tutor separado.
    public static Optional<LarTemp> abrir() {
        List<LarTemp> lares = new LarTemporarioDAO().listarTodos().stream()
                .filter(l -> l.getVagasDisponiveis() != null && l.getVagasDisponiveis() > 0)
                .toList();

        if (lares.isEmpty()) {
            new Alert(AlertType.WARNING, "Nenhum lar temporário com vaga disponível no momento.").showAndWait();
            return Optional.empty();
        }

        TableView<LarTemp> tabela = new TableView<>(FXCollections.observableArrayList(lares));
        tabela.setPrefSize(420, 260);
        tabela.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);

        TableColumn<LarTemp, Long> colunaId = new TableColumn<>("ID");
        colunaId.setCellValueFactory(d -> new SimpleLongProperty(d.getValue().getId()).asObject());
        colunaId.setPrefWidth(50);

        TableColumn<LarTemp, String> colunaResponsavel = new TableColumn<>("Responsável");
        colunaResponsavel.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getNome()));
        colunaResponsavel.setPrefWidth(230);

        TableColumn<LarTemp, Integer> colunaVagas = new TableColumn<>("Vagas");
        colunaVagas.setCellValueFactory(d -> new SimpleIntegerProperty(d.getValue().getVagasDisponiveis()).asObject());
        colunaVagas.setPrefWidth(60);

        tabela.getColumns().setAll(colunaId, colunaResponsavel, colunaVagas);
        tabela.getSelectionModel().selectFirst();

        Dialog<LarTemp> dialog = new Dialog<>();
        dialog.setTitle("Lar Temporário");
        dialog.setHeaderText("Selecione o responsável pelo lar temporário");

        VBox conteudo = new VBox(10, new Label("Escolha na tabela abaixo:"), tabela);
        conteudo.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(conteudo);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        // Duplo clique numa linha já confirma, sem precisar clicar em OK.
        tabela.setRowFactory(tv -> {
            TableRow<LarTemp> row = new TableRow<>();
            row.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2 && !row.isEmpty()) {
                    dialog.setResult(row.getItem());
                    dialog.close();
                }
            });
            return row;
        });

        dialog.setResultConverter(botao ->
                botao == ButtonType.OK ? tabela.getSelectionModel().getSelectedItem() : null);

        return dialog.showAndWait();
    }
}