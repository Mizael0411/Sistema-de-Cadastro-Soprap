package com.abrigo.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.database.JPAUtil;
import com.abrigo.model.Animal;
import com.abrigo.model.Auditoria;
import com.abrigo.model.NavigationManager;
import com.abrigo.model.Usuario;
import com.abrigo.service.SessaoUsuario;

import jakarta.persistence.EntityManager;
import javafx.animation.Animation;
import javafx.animation.ScaleTransition;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class HistoricoAnimaisCadastradosController {

    @FXML
    private TableView<Animal> tabelaAnimais;

    @FXML
    private TableColumn<Animal, Long> colunaIdAnimal;

    @FXML
    private TableColumn<Animal, String> colunaNomeAnimal;

    @FXML
    private TableColumn<Animal, String> colunaEspecie;

    @FXML
    private TableColumn<Animal, String> colunaPorte;

    @FXML
    private TableColumn<Animal, Integer> colunaIdadeAnimal;

    @FXML
    private TableColumn<Animal, String> colunaSexo;

    @FXML
    private TableColumn<Animal, String> colunaStsVacinacao;

    @FXML
    private TableColumn<Animal, LocalDate> colunaDataUltimaVacinacao;

    @FXML
    private TableColumn<Animal, String> colunaStsGravidez;

    @FXML
    private TableColumn<Animal, String> colunaDeficiencia;

    @FXML
    private TableColumn<Animal, String> colunaDescricaoDeficiencia;

    @FXML
    private TextField txtBusca;

    private final AnimalDAO animalDAO = new AnimalDAO();

    private final ObservableList<Animal> todosAnimais =
            FXCollections.observableArrayList();

    private FilteredList<Animal> animaisFiltrados;

    @FXML
    public void initialize() {
        configurarColunas();
        configurarBusca();
        carregarTabelaAssincrono();
    }

    private void configurarColunas() {
        colunaIdAnimal.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getId()));

        colunaNomeAnimal.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getNome()));

        colunaEspecie.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getEspecie()));

        colunaPorte.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getPorte()));

        colunaIdadeAnimal.setCellValueFactory(c ->
                new SimpleObjectProperty<>(c.getValue().getIdade()));

        colunaSexo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getSexo()));

        colunaStsVacinacao.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatusVacinacao()));

        colunaDataUltimaVacinacao.setCellValueFactory(c ->
                new SimpleObjectProperty<>(
                        c.getValue().getDataUltimaConsulta()));

        colunaStsGravidez.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().getStatusGravidez()));

        colunaDeficiencia.setCellValueFactory(c -> {
            boolean possui = Boolean.TRUE.equals(
                    c.getValue().getPossuiDeficiencia());

            return new SimpleStringProperty(possui ? "Sim" : "Não");
        });

        colunaDescricaoDeficiencia.setCellValueFactory(c -> {
            Animal animal = c.getValue();

            if (Boolean.TRUE.equals(animal.getPossuiDeficiencia())
                    && animal.getDescricaoDeficiencia() != null
                    && !animal.getDescricaoDeficiencia().isBlank()) {

                return new SimpleStringProperty(
                        animal.getDescricaoDeficiencia());
            }

            return new SimpleStringProperty("—");
        });
    }

    private void configurarBusca() {
        animaisFiltrados = new FilteredList<>(todosAnimais, p -> true);
        tabelaAnimais.setItems(animaisFiltrados);

        if (txtBusca != null) {
            txtBusca.textProperty().addListener(
                    (obs, oldV, newV) -> aplicarFiltro(newV));
        }
    }

    private void aplicarFiltro(String termo) {
        if (termo == null || termo.isBlank()) {
            animaisFiltrados.setPredicate(a -> true);
            return;
        }

        String t = termo.trim().toLowerCase();

        animaisFiltrados.setPredicate(a -> {
            if (a.getId() != null
                    && String.valueOf(a.getId()).contains(t)) {
                return true;
            }

            return a.getNome() != null
                    && a.getNome().toLowerCase().contains(t);
        });
    }

    private void carregarTabelaAssincrono() {
        tabelaAnimais.setPlaceholder(criarPlaceholderCarregando());

        Task<List<Animal>> task = new Task<>() {
            @Override
            protected List<Animal> call() {
                return animalDAO.listarTodos();
            }
        };

        task.setOnSucceeded(e -> {
            todosAnimais.setAll(task.getValue());
            tabelaAnimais.setPlaceholder(criarPlaceholderVazio());

            aplicarFiltro(
                    txtBusca != null ? txtBusca.getText() : null);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();

            if (ex != null) {
                ex.printStackTrace();
            }

            tabelaAnimais.setPlaceholder(criarPlaceholderVazio());

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Erro",
                    "Não foi possível carregar a lista de animais.\n"
                            + (ex != null ? ex.getMessage() : ""));
        });

        Thread t = new Thread(task, "carregar-animais");
        t.setDaemon(true);
        t.start();
    }

    private void carregarTabela() {
        carregarTabelaAssincrono();
    }

    private Node criarPlaceholderVazio() {
        Label pata = new Label("🐾");
        pata.getStyleClass().add("empty-paw");

        Label texto = new Label("Nenhum animal cadastrado.");
        texto.getStyleClass().add("loading-text");

        VBox box = new VBox(10, pata, texto);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("loading-placeholder");

        return box;
    }

    private Node criarPlaceholderCarregando() {
        Label pata = new Label("🐾");
        pata.getStyleClass().add("loading-paw");

        ScaleTransition pulso =
                new ScaleTransition(Duration.millis(650), pata);

        pulso.setFromX(0.85);
        pulso.setFromY(0.85);
        pulso.setToX(1.25);
        pulso.setToY(1.25);
        pulso.setCycleCount(Animation.INDEFINITE);
        pulso.setAutoReverse(true);
        pulso.play();

        Label texto = new Label("Farejando bichinhos...");
        texto.getStyleClass().add("loading-text");

        VBox box = new VBox(10, pata, texto);
        box.setAlignment(Pos.CENTER);
        box.getStyleClass().add("loading-placeholder");

        return box;
    }

    @FXML
    private void inserir() {
        NavigationManager.getInstance()
                .navegarConteudo("cadastro-animal");
    }

    @FXML
    private void editar() {
        Animal selecionado =
                tabelaAnimais.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleção Necessária",
                    "Selecione um animal na tabela para editar.");
            return;
        }

        CadastroAnimalController controller =
                (CadastroAnimalController) NavigationManager.getInstance()
                        .navegarConteudoEObterController("cadastro-animal");

        if (controller != null) {
            controller.carregarDadosParaEdicao(selecionado);
        }
    }

    @FXML
    private void excluir() {
        Animal selecionado =
                tabelaAnimais.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleção Necessária",
                    "Selecione um animal na tabela para excluir.");
            return;
        }

        Alert confirmacao = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Deseja realmente excluir o animal \""
                        + selecionado.getNome() + "\"?\n"
                        + "O animal deixará de aparecer na listagem de ativos, "
                        + "mas seu histórico e suas vacinas serão preservados.",
                ButtonType.YES,
                ButtonType.NO);

        confirmacao.setHeaderText(null);
        confirmacao.setTitle("Confirmar Desativação");

        confirmacao.showAndWait().ifPresent(resposta -> {
            if (resposta == ButtonType.YES) {
                boolean ok =
                        excluirAnimalComDependencias(selecionado.getId());

                if (ok) {
                    carregarTabela();

                    mostrarAlerta(
                            Alert.AlertType.INFORMATION,
                            "Sucesso",
                            "Animal desativado com sucesso. "
                                    + "O histórico de vacinação foi preservado.");
                } else {
                    mostrarAlerta(
                            Alert.AlertType.ERROR,
                            "Erro",
                            "Não foi possível desativar o animal. "
                                    + "Verifique o console para mais detalhes.");
                }
            }
        });
    }

    private boolean excluirAnimalComDependencias(Long animalId) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            System.out.println("DEBUG 1: transação iniciada");

            Animal animal = em.find(Animal.class, animalId);

            System.out.println(
                    "DEBUG 2: animal encontrado = " + (animal != null));

            if (animal == null) {
                em.getTransaction().rollback();

                System.out.println(
                        "Animal não encontrado: id=" + animalId);

                return false;
            }

            if (Boolean.FALSE.equals(animal.getAtivo())) {
                em.getTransaction().rollback();

                System.out.println(
                        "Animal já está inativo: id=" + animalId);

                return false;
            }

            String nomeAnimal = animal.getNome();

            animal.setAtivo(false);

            Usuario usuario = SessaoUsuario.getUsuarioAtual();

            if (usuario == null) {
                throw new IllegalStateException(
                        "Não há usuário autenticado para registrar a auditoria.");
            }

            Auditoria auditoria = new Auditoria();
            auditoria.setUsuarioId(usuario.getId());
            auditoria.setNomeUsuario(usuario.getNome());
            auditoria.setOperacao("EXCLUSAO");
            auditoria.setModulo("Animais");
            auditoria.setRegistroId(animal.getId());
            auditoria.setDescricao(
                    "Animal desativado: " + nomeAnimal);
            auditoria.setDataHora(LocalDateTime.now());

            em.persist(auditoria);

            System.out.println(
                    "DEBUG 3: animal marcado como inativo");

            System.out.println(
                    "DEBUG 4: auditoria registrada");

            em.getTransaction().commit();

            System.out.println(
                    "DEBUG 5: transação concluída");

            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println(
                    "Erro ao desativar animal id=" + animalId
                            + ": " + e.getMessage());

            e.printStackTrace();

            mostrarAlerta(
                    Alert.AlertType.ERROR,
                    "Detalhes do erro",
                    e.getClass().getSimpleName()
                            + ": " + e.getMessage());

            return false;

        } finally {
            em.close();
        }
    }

    @FXML
    private void voltar() {
        NavigationManager.getInstance()
                .navegarConteudo("cadastro-animal");
    }

    @FXML
    private void transferirAdocao() {
        Animal selecionado =
                tabelaAnimais.getSelectionModel().getSelectedItem();

        if (selecionado == null) {
            mostrarAlerta(
                    Alert.AlertType.WARNING,
                    "Seleção Necessária",
                    "Selecione um animal na tabela para transferir para adoção.");
            return;
        }

        GestaoAdocaoController controller =
                (GestaoAdocaoController) NavigationManager.getInstance()
                        .navegarConteudoEObterController("gestao-adocao");

        if (controller != null) {
            controller.carregarDadosParaTransferenciaAdocao(selecionado);
        }
    }

    private void mostrarAlerta(
            Alert.AlertType tipo,
            String titulo,
            String msg) {

        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
