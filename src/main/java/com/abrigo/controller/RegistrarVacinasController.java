package com.abrigo.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import com.abrigo.dao.AnimalDAO;
import com.abrigo.dao.VacinaDAO;
import com.abrigo.model.Animal;
import com.abrigo.model.NavigationManager;
import com.abrigo.model.Vacina;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.util.StringConverter;

public class RegistrarVacinasController {

    @FXML
    private ComboBox<Animal> cbAnimal;

    @FXML
    private ComboBox<String> cbTipoVacina;

    @FXML
    private ComboBox<String> cbDose;

    @FXML
    private DatePicker dpDataVacinacao;

    @FXML
    private DatePicker dpDataProximaVacinacao;

    private final VacinaDAO vacinaDAO = new VacinaDAO();
    private final AnimalDAO animalDAO = new AnimalDAO();

    private Vacina vacinaEmEdicao;

    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu");

    @FXML
    public void initialize() {

        configurarCampoData(dpDataVacinacao);
        configurarCampoData(dpDataProximaVacinacao);

        dpDataVacinacao.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (empty || date == null) {
                    setDisable(false);
                    setStyle("");
                    return;
                }

                boolean dataFutura = date.isAfter(LocalDate.now());
                setDisable(dataFutura);

                setStyle(dataFutura
                        ? "-fx-background-color: #f8d7da; "
                          + "-fx-text-fill: #721c24;"
                        : "");
            }
        });

        dpDataProximaVacinacao.setDayCellFactory(
            picker -> new DateCell() {
                @Override
                public void updateItem(LocalDate date, boolean empty) {
                    super.updateItem(date, empty);

                    if (empty || date == null) {
                        setDisable(false);
                        setStyle("");
                        return;
                    }

                    boolean dataPassada =
                            date.isBefore(LocalDate.now());

                    setDisable(dataPassada);

                    setStyle(dataPassada
                            ? "-fx-background-color: #f8d7da; "
                              + "-fx-text-fill: #721c24;"
                            : "");
                }
            }
        );

        cbDose.setItems(FXCollections.observableArrayList(
            "1ª Dose",
            "2ª Dose",
            "3ª Dose",
            "Reforço Anual",
            "Dose Única"
        ));

        cbTipoVacina.setItems(FXCollections.observableArrayList(
            "Raiva",
            "Virose"
        ));

        List<Animal> animais = animalDAO.listarTodos();

        System.out.println(
            "\n===== ANIMAIS DISPONÍVEIS PARA VACINAÇÃO ====="
        );

        for (Animal animal : animais) {
            System.out.println(
                "Animal: " + animal.getNome()
                + " | ID: " + animal.getId()
                + " | Ativo: " + animal.getAtivo()
            );
        }

        System.out.println(
            "===== FIM DO DIAGNÓSTICO =====\n"
        );

        cbAnimal.setItems(
            FXCollections.observableArrayList(
                animais.stream()
                    .filter(animal ->
                        Boolean.TRUE.equals(animal.getAtivo())
                    )
                    .toList()
            )
        );

        Animal animalParaVacinar =
            NavigationManager.getInstance().getAnimalParaVacinar();

        if (animalParaVacinar != null
                && Boolean.TRUE.equals(animalParaVacinar.getAtivo())) {
            cbAnimal.setValue(animalParaVacinar);
        }

        Vacina edicao =
            NavigationManager.getInstance().getVacinaParaEdicao();

        if (edicao != null) {
            preencherCamposEdicao(edicao);
        }
    }

    private void configurarCampoData(DatePicker datePicker) {

        datePicker.setEditable(true);

        datePicker.setConverter(new StringConverter<LocalDate>() {

            @Override
            public String toString(LocalDate data) {
                return data == null
                        ? ""
                        : FORMATO_DATA.format(data);
            }

            @Override
            public LocalDate fromString(String texto) {

                if (texto == null || texto.trim().isEmpty()) {
                    return null;
                }

                return LocalDate.parse(
                    texto.trim(),
                    FORMATO_DATA
                );
            }
        });
    }

    private LocalDate lerDataDigitada(DatePicker datePicker) {

        String texto = datePicker.getEditor().getText();

        if (texto == null || texto.trim().isEmpty()) {
            datePicker.setValue(null);
            return null;
        }

        try {
            LocalDate data = LocalDate.parse(
                texto.trim(),
                FORMATO_DATA
            );

            datePicker.setValue(data);
            return data;

        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private void preencherCamposEdicao(Vacina vacina) {

        this.vacinaEmEdicao = vacina;

        if (vacina.getAnimal() != null) {

            Animal animal = vacina.getAnimal();

            if (!cbAnimal.getItems().contains(animal)) {
                cbAnimal.getItems().add(animal);
            }

            cbAnimal.setValue(animal);

            if (!Boolean.TRUE.equals(animal.getAtivo())) {
                bloquearCampos();
            }
        }

        cbTipoVacina.setValue(vacina.getTipoVacina());

        dpDataVacinacao.setValue(
            vacina.getDataVacinacao()
        );

        dpDataProximaVacinacao.setValue(
            vacina.getDataProximaVacinacao()
        );

        if (vacina.getDose() != null
                && !vacina.getDose().isEmpty()) {

            if (!cbDose.getItems().contains(vacina.getDose())) {
                cbDose.getItems().add(vacina.getDose());
            }

            cbDose.setValue(vacina.getDose());
        }

        System.out.println(
            "Modo de edição de vacina ativado. ID: "
            + vacina.getId()
        );
    }

    private void bloquearCampos() {

        cbAnimal.setDisable(true);
        cbTipoVacina.setDisable(true);
        cbDose.setDisable(true);
        dpDataVacinacao.setDisable(true);
        dpDataProximaVacinacao.setDisable(true);

        mostrarAlerta(
            "Registro bloqueado",
            "O animal associado a esta vacina foi desativado. "
            + "Você pode consultar os dados, mas não pode "
            + "alterar este registro."
        );
    }

    @FXML
    private void salvar() {

        if (cbAnimal.getValue() == null) {
            mostrarAlerta("Validação", "Selecione um animal.");
            return;
        }

        Animal animalSelecionado = cbAnimal.getValue();

        if (vacinaEmEdicao != null
                && vacinaEmEdicao.getAnimal() != null
                && !Boolean.TRUE.equals(
                    vacinaEmEdicao.getAnimal().getAtivo()
                )) {

            mostrarAlerta(
                "Registro bloqueado",
                "Esta vacinação pertence a um animal desativado "
                + "e não pode ser alterada."
            );
            return;
        }

        if (!Boolean.TRUE.equals(animalSelecionado.getAtivo())) {
            mostrarAlerta(
                "Validação",
                "Não é possível registrar ou alterar uma "
                + "vacinação para um animal desativado."
            );
            return;
        }

        String tipoVacina = cbTipoVacina.getValue();

        if (tipoVacina == null
                || (!tipoVacina.equalsIgnoreCase("Raiva")
                && !tipoVacina.equalsIgnoreCase("Virose"))) {

            mostrarAlerta(
                "Validação",
                "Selecione Raiva ou Virose."
            );
            return;
        }

        if (cbDose.getValue() == null
                || cbDose.getValue().trim().isEmpty()) {

            mostrarAlerta(
                "Validação",
                "Selecione a dose da vacina."
            );
            return;
        }

        LocalDate dataVacinacao =
                lerDataDigitada(dpDataVacinacao);

        if (dataVacinacao == null) {
            mostrarAlerta(
                "Validação",
                "Informe uma data válida para a aplicação "
                + "no formato dd/MM/aaaa."
            );
            return;
        }

        LocalDate hoje = LocalDate.now();

        if (dataVacinacao.isAfter(hoje)) {
            mostrarAlerta(
                "Validação",
                "A data da aplicação não pode estar no futuro."
            );
            return;
        }

        String textoProximaData =
                dpDataProximaVacinacao.getEditor().getText();

        LocalDate proximaData =
                lerDataDigitada(dpDataProximaVacinacao);

        if (textoProximaData != null
                && !textoProximaData.trim().isEmpty()
                && proximaData == null) {

            mostrarAlerta(
                "Validação",
                "Digite uma data válida para a próxima vacinação "
                + "no formato dd/MM/aaaa."
            );
            return;
        }

        if (proximaData != null) {

            if (proximaData.isBefore(hoje)) {
                mostrarAlerta(
                    "Validação",
                    "A data da próxima vacinação não pode "
                    + "estar no passado."
                );
                return;
            }

            if (proximaData.isBefore(dataVacinacao)) {
                mostrarAlerta(
                    "Validação",
                    "A próxima vacinação não pode ser anterior "
                    + "à data da aplicação."
                );
                return;
            }
        }

        Vacina vacina = (vacinaEmEdicao != null)
                ? vacinaEmEdicao
                : new Vacina();

        vacina.setAnimal(animalSelecionado);
        vacina.setTipoVacina(tipoVacina);
        vacina.setDose(cbDose.getValue());
        vacina.setDataVacinacao(dataVacinacao);

        vacina.setDataProximaVacinacao(proximaData);

        vacina.setStatusVacinacao("Concluída");

        boolean eraEdicao = vacina.getId() != null;

        System.out.println("===== SALVAR VACINA =====");
        System.out.println(
            "Operação: " + (eraEdicao ? "ATUALIZAR" : "CADASTRAR")
        );
        System.out.println("ID da vacina: " + vacina.getId());
        System.out.println(
            "ID do animal: " + animalSelecionado.getId()
        );
        System.out.println("Tipo: " + tipoVacina);
        System.out.println("Data da aplicação: " + dataVacinacao);
        System.out.println("Próxima vacinação: " + proximaData);
        System.out.println("=========================");

        boolean sucesso = eraEdicao
                ? vacinaDAO.atualizar(vacina)
                : vacinaDAO.salvar(vacina);

        if (sucesso) {

            mostrarAlerta(
                "Sucesso",
                eraEdicao
                    ? "Vacina atualizada com sucesso!"
                    : "Vacina registrada com sucesso!"
            );

            abrirHistorico();

        } else {
            mostrarAlerta(
                "Erro",
                "Não foi possível salvar o registro da vacina. "
                + "Verifique o console para mais detalhes."
            );
        }
    }

    @FXML
    private void abrirHistorico() {
        NavigationManager.getInstance()
            .navegarConteudo("historico-vacinas");
    }

    private void mostrarAlerta(String titulo, String mensagem) {

        Alert.AlertType tipoAlerta =
            "Erro".equalsIgnoreCase(titulo)
                ? Alert.AlertType.ERROR
                : "Validação".equalsIgnoreCase(titulo)
                    ? Alert.AlertType.WARNING
                    : Alert.AlertType.INFORMATION;

        Alert alert = new Alert(tipoAlerta);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}