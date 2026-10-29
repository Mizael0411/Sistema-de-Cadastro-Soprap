package com.abrigo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "adocao")
public class Adocao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_adocao")
    private Long id;

    @Column(name = "data_adocao", nullable = false)
    private LocalDate dataAdocao;

    @Column(name = "nome_tutor", nullable = false)
    private String nomeTutor;

    @Column(name = "telefone_tutor")
    private String telefoneTutor;

    @Column(name = "adocao_especial", nullable = false)
    private Boolean adocaoEspecial;

    @Column(name = "status_adocao", nullable = false)
    private String statusAdocao;

    @Column(name = "status_registro", nullable = false, length = 20)
    private String statusRegistro = "ATIVA";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_animal", nullable = false)
    private Animal animal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_lar_temp", nullable = true)
    private LarTemp larTemp;

    public Adocao() {
    }

    public Adocao(
            LocalDate dataAdocao,
            String nomeTutor,
            Boolean adocaoEspecial,
            Animal animal,
            String statusAdocao,
            LarTemp larTemp) {

        this.dataAdocao = dataAdocao;
        this.nomeTutor = nomeTutor;
        this.adocaoEspecial = adocaoEspecial;
        this.animal = animal;
        this.statusAdocao = statusAdocao;
        this.larTemp = larTemp;
        this.statusRegistro = "ATIVA";
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDataAdocao() {
        return dataAdocao;
    }

    public void setDataAdocao(LocalDate dataAdocao) {
        this.dataAdocao = dataAdocao;
    }

    public String getNomeTutor() {
        return nomeTutor;
    }

    public void setNomeTutor(String nomeTutor) {
        this.nomeTutor = nomeTutor;
    }

    public String getTelefoneTutor() {
        return telefoneTutor;
    }

    public void setTelefoneTutor(String telefoneTutor) {
        this.telefoneTutor = telefoneTutor;
    }

    public Boolean getAdocaoEspecial() {
        return adocaoEspecial;
    }

    public void setAdocaoEspecial(Boolean adocaoEspecial) {
        this.adocaoEspecial = adocaoEspecial;
    }

    public String getStatusAdocao() {
        return statusAdocao;
    }

    public void setStatusAdocao(String statusAdocao) {
        this.statusAdocao = statusAdocao;
    }

    public String getStatusRegistro() {
        return statusRegistro;
    }

    public void setStatusRegistro(String statusRegistro) {
        this.statusRegistro = statusRegistro;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public LarTemp getLarTemp() {
        return larTemp;
    }

    public void setLarTemp(LarTemp larTemp) {
        this.larTemp = larTemp;
    }
}
