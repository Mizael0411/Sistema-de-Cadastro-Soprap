package com.abrigo.model;

import java.util.Date;

public class Vacina {
    private int id_vacina;
    private String tipo_vacina;
    private Date data_vacinacao;
    private float dose;
}

public int getId_vacina() {
        return id_vacina;
    }

    public void setId_vacina(int id_vacina) {
        this.id_vacina = id_vacina;
    }

    public String getTipo_vacina() {
        return tipo_vacina;
    }

    public void setTipo_vacina(String tipo_vacina) {
        this.tipo_vacina = tipo_vacina;
    }

    public Date getData_vacinacao() {
        return data_vacinacao;
    }

    public void setData_vacinacao(Date data_vacinacao) {
        this.data_vacinacao = data_vacinacao;
    }

    public float getDose() {
        return dose;
    }

    public void setDose(float dose) {
        this.dose = dose;
    }
}