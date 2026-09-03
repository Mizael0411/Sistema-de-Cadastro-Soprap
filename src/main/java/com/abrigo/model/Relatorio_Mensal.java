package com.abrigo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Date;

@Entity
public class Relatorio_Mensal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id_relatorio;
    private Date data_relatorio;
    private float valor_final;
}

public int getId_relatorio() {
        return id_relatorio;
    }

    public void setId_relatorio(int id_relatorio) {
        this.id_relatorio = id_relatorio;
    }

    public Date getData_relatorio() {
        return data_relatorio;
    }

    public void setData_relatorio(Date data_relatorio) {
        this.data_relatorio = data_relatorio;
    }

    public float getValor_final() {
        return valor_final;
    }

    public void setValor_final(float valor_final) {
        this.valor_final = valor_final;
    }
}