package com.abrigo.controller;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.database.JPAUtil;
import com.abrigo.model.Animal;
import com.abrigo.model.Vacina;

import jakarta.persistence.EntityManager;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public class HistoricoVacinasController {

    @FXML private ComboBox<Animal> cbAnimal;
    @FXML private TableView<Vacina> tabelaHistorico;
    @FXML private TableColumn<Vacina, String> colunaVacina;
    @FXML private TableColumn<Vacina, String> colunaDose;
    @FXML private TableColumn<Vacina, String> colunaData;
    @FXML private Button voltarButton;

    private final AnimalDAO animalDAO = new AnimalDAO();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML 
    void initialize() {
        colunaVacina.setCellValueFactory(
            cellData -> new SimpleStringProperty(cellData.getValue().getTipoVacina())
        );

        if (colunaDose != null) {
            colunaDose.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDose())
            );
        }

        colunaData.setCellValueFactory(
            cellData -> new SimpleStringProperty(
                cellData.getValue().getDataVacinacao() != null 
                    ? cellData.getValue().getDataVacinacao().format(formatter) 
                    : ""
            )
        );

        carregarAnimais();

        cbAnimal.valueProperty().addListener((obs, antigo, selecionado) -> {
            if (selecionado != null) {
                carregarHistoricoDoAnimal(selecionado);
            } else {
                tabelaHistorico.getItems().clear();
            }
        });
    }

    private void carregarAnimais() {
        List<Animal> animais = animalDAO.listarTodos();
        cbAnimal.setItems(FXCollections.observableArrayList(animais));
    }

    // Busca as vacinas diretamente com EntityManager para não depender do VacinaDAO
    private void carregarHistoricoDoAnimal(Animal animal) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Vacina> vacinas = new ArrayList<>();
        try {
            vacinas = em.createQuery("SELECT v FROM Vacina v WHERE v.animal.id = :animalId", Vacina.class)
                        .setParameter("animalId", animal.getId())
                        .getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar histórico de vacinas: " + e.getMessage());
        } finally {
            em.close();
        }

        tabelaHistorico.setItems(FXCollections.observableArrayList(vacinas));
    }

    @FXML 
    void voltar() {
        NavigationManager.getInstance().navegarConteudo("registrar-vacinas");
    }
}