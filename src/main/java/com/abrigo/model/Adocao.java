package com.abrigo.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
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

    @Column(name = "adocao_especial", nullable = false)
    private Boolean adocaoEspecial;

    @OneToOne
    @JoinColumn(name = "id_animal", nullable = false)
    private Animal animal;

    public Adocao() {
    }

    public Adocao(LocalDate dataAdocao, String nomeTutor, Boolean adocaoEspecial, Animal animal) {
        this.dataAdocao = dataAdocao;
        this.nomeTutor = nomeTutor;
        this.adocaoEspecial = adocaoEspecial;
        this.animal = animal;
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

    public Boolean getAdocaoEspecial() {
        return adocaoEspecial;
    }

    public void setAdocaoEspecial(Boolean adocaoEspecial) {
        this.adocaoEspecial = adocaoEspecial;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
}