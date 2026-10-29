package com.abrigo.util;

import java.io.Console;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class GeradorSenha {

    public static void main(String[] args) {
        Console console = System.console();

        if(console == null){
            System.err.println("Execute pelo terminal para digitar a senha sem exibi-la.");
            return;
        }

        char[] senha = console.readPassword("Digite a senha inicial da administradora: ");

        if(senha == null || senha.length < 12) {
            System.err.println("Use uma senha com pelo menos 12 caracteres.");
            return;
        }

        try {
            String hash = BCrypt.withDefaults().hashToString(12, senha);
            System.out.println("Hash gerado:");
            System.out.println(hash);
        } finally {
            java.util.Arrays.fill(senha, '\0');
        }
    }
       
}
