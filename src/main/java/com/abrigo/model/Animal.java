
package com.abrigo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id_animal;

    private String nome;
    private Integer idade;
    private Date dataNascimento;
    private String sexo;
    private String statusVacinacao;
    private String statusGravidez;
    private Date data_ultima_vacinacao;

    public Integer getId_animal() {
        return id_animal;
    }

    public void setId_animal(Integer id_animal) {
        this.id_animal = id_animal;
    }

    public Integer getId() {
        return id_animal;
    }

    public void setId(int id) {
        this.id_animal = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public Integer getIdade() {
        return idade;
    }

    public void setIdade(Integer idade) {
        this.idade = idade;
    }

    public Date getDataNascimento() {

        return dataNascimento;
    }

    public void setDataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String getStatusVacinacao() {
        return statusVacinacao;
    }

    public void setStatusVacinacao(String statusVacinacao) {
        this.statusVacinacao = statusVacinacao;
    }

    public String getStatusGravidez() {
        return statusGravidez;
    }

    public void setStatusGravidez(String statusGravidez) {
        this.statusGravidez = statusGravidez;
    }

    public Date getData_ultima_vacinacao() {
        return data_ultima_vacinacao;
    }

    public void setData_ultima_vacinacao(Date data_ultima_vacinacao) {
        this.data_ultima_vacinacao = data_ultima_vacinacao;
    }
}
