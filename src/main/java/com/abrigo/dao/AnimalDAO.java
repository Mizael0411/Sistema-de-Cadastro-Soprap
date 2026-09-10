package com.abrigo.dao;

import com.abrigo.database.Conexao;
import com.abrigo.model.Animal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {

    public List<Animal> listarTodos() {
        List<Animal> lista = new ArrayList<>();
        String sql = "SELECT * FROM animal";

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Animal a = new Animal();
               a.setId_animal(rs.getInt("id_animal"));
                a.setNome(rs.getString("nome"));
               a.setIdade(rs.getInt("idade"));
               a.setDataNascimento(rs.getDate("data_nascimento"));
               a.setSexo(rs.getString("sexo"));
               a.setStatusVacinacao(rs.getString("status_vacinacao"));
                a.setStatusGravidez(rs.getString("status_gravidez"));
               a.setData_ultima_vacinacao(rs.getDate("data_ultima_vacinacao"));
                lista.add(a);
            }

        } catch (SQLException e) {
            System.err.println("Erro ao listar animais do banco: " + e.getMessage());
        }

        return lista;
    }

    public boolean salvar(Animal animal) {
        String sql = """
            INSERT INTO animal
            (nome, idade, data_nascimento, sexo, status_vacinacao, status_gravidez, data_ultima_vacinacao)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = Conexao.conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, animal.getNome());
            stmt.setInt(2, animal.getIdade());
            stmt.setDate(3, new java.sql.Date(animal.getDataNascimento().getTime()));
            stmt.setString(4, animal.getSexo());
            stmt.setString(5, animal.getStatusVacinacao());
            stmt.setString(6, animal.getStatusGravidez());
            stmt.setDate(7, new java.sql.Date(animal.getData_ultima_vacinacao().getTime()));

            stmt.executeUpdate();
            System.out.println("Animal cadastrado com sucesso!");
            return true;

        } catch (SQLException e) {
            System.err.println("Erro ao cadastrar animal: " + e.getMessage());
            return false;
        }
    }
}