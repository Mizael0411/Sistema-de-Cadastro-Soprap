package com.abrigo.util;

import java.util.function.UnaryOperator;

import javafx.scene.control.TextFormatter;

public final class FormatadorTelefone {

    private FormatadorTelefone() {
    }

    public static String somenteDigitos(String telefone) {
        if (telefone == null) {
            return "";
        }

        return telefone.replaceAll("\\D", "");
    }

    public static boolean telefoneValido(String telefone) {
        String digitos = somenteDigitos(telefone);
        return digitos.length() == 10 || digitos.length() == 11;
    }

    public static String formatar(String telefone) {
        String digitos = somenteDigitos(telefone);

        if (digitos.length() == 10) {
            return "(" + digitos.substring(0, 2) + ") "
                    + digitos.substring(2, 6) + "-"
                    + digitos.substring(6, 10);
        }

        if (digitos.length() == 11) {
            return "(" + digitos.substring(0, 2) + ") "
                    + digitos.substring(2, 7) + "-"
                    + digitos.substring(7, 11);
        }

        return telefone == null ? "" : telefone;
    }

    public static String formatarParcial(String telefone) {
        String digitos = somenteDigitos(telefone);

        if (digitos.length() > 11) {
            digitos = digitos.substring(0, 11);
        }

        int tamanho = digitos.length();

        if (tamanho == 0) {
            return "";
        }

        if (tamanho <= 2) {
            return "(" + digitos;
        }

        String ddd = digitos.substring(0, 2);
        String numero = digitos.substring(2);

        if (tamanho <= 6) {
            return "(" + ddd + ") " + numero;
        }

        if (tamanho <= 10) {
            return "(" + ddd + ") "
                    + numero.substring(0, 4) + "-"
                    + numero.substring(4);
        }

        return "(" + ddd + ") "
                + numero.substring(0, 5) + "-"
                + numero.substring(5);
    }

    public static TextFormatter<String> criarTextFormatter() {
        UnaryOperator<TextFormatter.Change> filtro = change -> {
            String novoTexto = change.getControlNewText();
            String digitos = somenteDigitos(novoTexto);

            if (digitos.length() > 11) {
                return null;
            }

            String formatado = formatarParcial(novoTexto);

            change.setRange(0, change.getControlText().length());
            change.setText(formatado);
            change.setCaretPosition(formatado.length());
            change.setAnchor(formatado.length());

            return change;
        };

        return new TextFormatter<>(filtro);
    }
}