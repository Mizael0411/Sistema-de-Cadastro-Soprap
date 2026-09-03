package com.abrigo.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CriarTabelas {
    public static void criar(){
        String sqlAnimal = """
            CREATE TABLE IF NOT EXISTS animal (
            id_animal INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL,
            idade INTEGER NOT NULL,
            data_nascimento DATE NOT NULL,
            sexo TEXT NOT NULL,
            status_vacinacao TEXT NOT NULL,
            status_gravidez TEXT NOT NULL,
            data_ultima_vacinacao DATE NOT NULL);
                """;
        String sqlAdocao = """
            CREATE TABLE IF NOT EXISTS adocao (
            id_adocao INTEGER PRIMARY KEY AUTOINCREMENT,
            data_adocao DATE NOT NULL);
                """;
        String sqlDoacao = """
            CREATE TABLE IF NOT EXISTS doacao (
            id_doacao INTEGER PRIMARY KEY AUTOINCREMENT,
            descricao TEXT NOT NULL,
            valor REAL NOT NULL,
            tipo_doacao TEXT NOT NULL,
            data_doacao DATE NOT NULL,
            nome_doador TEXT NOT NULL);
                """;
        String sqlInsumos = """
            CREATE TABLE IF NOT EXISTS insumos (
            id_insumo INTEGER PRIMARY KEY AUTOINCREMENT,
            data_insumos DATE NOT NULL,
            valor REAL NOT NULL,
            descricao TEXT NOT NULL);
                """;
        String sqlLar = """
            CREATE TABLE IF NOT EXISTS lar (
            id_lar INTEGER PRIMARY KEY AUTOINCREMENT,
            nome TEXT NOT NULL,
            endereco TEXT NOT NULL,
            vagas INTEGER NOT NULL,
            cpf INTEGER NOT NULL,
            telefone INTEGER NOT NULL,
            aceita_doencas BOOLEAN NOT NULL);
                """;
        String sqlProdutos = """
            CREATE TABLE IF NOT EXISTS produtos (
            id_produto INTEGER PRIMARY KEY AUTOINCREMENT,
            data_compra DATE NOT NULL,
            valor_produto REAL NOT NULL);
                """;
        String sqlRelatorio = """
            CREATE TABLE IF NOT EXISTS relatorio_mensal (
            id_relatorio INTEGER PRIMARY KEY AUTOINCREMENT,
            data_relatorio DATE NOT NULL,
            valor_final REAL NOT NULL);
                """;
        String sqlVacina = """
            CREATE TABLE IF NOT EXISTS vacina (
            id_vacina INTEGER PRIMARY KEY AUTOINCREMENT,
            tipo_vacina TEXT NOT NULL,
            data_vacinacao DATE NOT NULL,
            dose REAL NOT NULL);
                """;

       try (
            Connection conexao = Conexao.conectar();
            Statement stmt = conexao.createStatement()
        ) {

            stmt.execute(sqlAnimal);
            stmt.execute(sqlAdocao);
            stmt.execute(sqlDoacao);
            stmt.execute(sqlInsumos);
            stmt.execute(sqlLar);
            stmt.execute(sqlProdutos);
            stmt.execute(sqlRelatorio);
            stmt.execute(sqlVacina);

            System.out.println("TABELAS CRIADAS COM SUCESSO!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        criar();
    }    

}
