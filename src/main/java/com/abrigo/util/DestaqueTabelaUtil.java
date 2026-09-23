package com.abrigo.util;

import java.util.List;
import java.util.function.Supplier;

import com.abrigo.dto.ItemAdocao;

import javafx.css.PseudoClass;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;

public final class DestaqueTabelaUtil {

    private static final PseudoClass DESTAQUE = PseudoClass.getPseudoClass("destaque");

    private DestaqueTabelaUtil() {
    }

        public static <T extends ItemAdocao> void configurarRowFactory(TableView<T> tabela, Supplier<Long> idParaDestacar) {
        tabela.setRowFactory(tv -> {
            TableRow<T> row = new TableRow<>();
            row.itemProperty().addListener((obs, oldItem, newItem) -> {
                Long id = idParaDestacar.get();
                boolean destacar = newItem != null && id != null && id.equals(newItem.idAnimal());
                row.pseudoClassStateChanged(DESTAQUE, destacar);
            });
            return row;
        });
    }
    public static void selecionarEExibir(TableView<? extends ItemAdocao> tabela,
                                          List<? extends ItemAdocao> dados,
                                          Long idAnimal) {
        if (idAnimal == null) {
            return;
        }
        for (int i = 0; i < dados.size(); i++) {
            if (idAnimal.equals(dados.get(i).idAnimal())) {
                tabela.getSelectionModel().select(i);
                tabela.scrollTo(i);
                return;
            }
        }
    }
}
