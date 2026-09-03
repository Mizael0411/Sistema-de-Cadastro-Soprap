package com.abrigo.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexao {

    private static final String URL = "jdbc:sqlite:banco.db";

    public static Connection conectar() {
        try {
            Connection conexao = DriverManager.getConnection(URL);
            System.out.println("BANCO CONECTADO COM SUCESSO!");
            return conexao;

        } catch (SQLException e) {
            System.out.println("ERRO AO CONECTAR:");
            System.out.println(e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        Connection conexao = conectar();

        if (conexao != null) {
            System.out.println("Teste concluído: conexão funcionando.");

            try {
                conexao.close();
                System.out.println("Conexão fechada.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}