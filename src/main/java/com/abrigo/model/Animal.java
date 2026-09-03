
package com.abrigo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Animal {
    private String nome;
    private int idade;
    private Date dataNascimento;
    private String sexo;
    private String statusVacinacao;
    private String statusGravidez;
    private int id_animal;
    private Date data_ultima_vacinacao;
    private String status_vacinacao;



    public String getStatusGravidez() {
        return statusGravidez;
    }

    public void setStatusGravidez(String statusGravidez) {
        this.statusGravidez = statusGravidez;
    }
    public int getId_animal(){
        return id_animal;
    }
    public void setId_animal(int id_animal){
        this.id_animal = id_animal;
    }
    public Date getData_ultima_vacinacao() {
        return data_ultima_vacinacao;
    }
    public void setData_ultima_vacinacao(Date data_ultima_vacinacao) {
        this.data_ultima_vacinacao = data_ultima_vacinacao;
    }
    public String getStatus_vacinacao() {
        return status_vacinacao;
    }
    public void setStatus_vacinacao(String status_vacinacao) {
        this.status_vacinacao = status_vacinacao;
    }

    public Date getDataNascimento() {
        return dataNascimento;
    }

    public void setdataNascimento(Date dataNascimento) {
        this.dataNascimento = dataNascimento;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
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

    public void setId(int i) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

}
