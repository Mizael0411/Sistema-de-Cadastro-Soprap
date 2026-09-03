package com.abrigo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


import java.util.Date;

@Entity
public class Lar_Temp {
    private String nome;
    private String endereco;
    private int vagas;
    private int id_lar;
    private int cpf;
    private int telefone;
    private Boolean aceita_doenças;
}


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public int getVagas() {
        return vagas;
    }

    public void setVagas(int vagas) {
        this.vagas = vagas;
    }

    public int getId_lar() {
        return id_lar;
    }

    public void setId_lar(int id_lar) {
        this.id_lar = id_lar;
    }

    public int getCpf() {
        return cpf;
    }

    public void setCpf(int cpf) {
        this.cpf = cpf;
    }

    public int getTelefone() {
        return telefone;
    }

    public void setTelefone(int telefone) {
        this.telefone = telefone;
    }

    public Boolean getAceita_doenças() {
        return aceita_doenças;
    }

    public void setAceita_doenças(Boolean aceita_doenças) {
        this.aceita_doenças = aceita_doenças;
