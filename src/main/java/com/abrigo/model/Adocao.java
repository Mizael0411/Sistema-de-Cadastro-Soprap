package com.abrigo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Adocao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_adocao;
    private Date data_adocao;

}

public int getId_adocao() {
        return id_adocao;
    }

    public void setId_adocao(int id_adocao) {
        this.id_adocao = id_adocao;
    }

    public Date getData_adocao() {
        return data_adocao;
    }

    public void setData_adocao(Date data_adocao) {
        this.data_adocao = data_adocao;
    }
}