package com.abrigo.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "vacina")
public class Vacina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_vacina")
    private Long id; 

    @Column(name = "tipo_vacina", nullable = false)
    private String tipoVacina; 

    @Column(name = "data_vacinacao", nullable = false)
    private LocalDate dataVacinacao; 

    @Column(nullable = false)
    private String dose; 

    @ManyToOne
    @JoinColumn(name = "id_animal", nullable = false)
    private Animal animal; 

    public Vacina() {
    }

    public Vacina(String tipoVacina, LocalDate dataVacinacao, String dose, Animal animal) {
        this.tipoVacina = tipoVacina;
        this.dataVacinacao = dataVacinacao;
        this.dose = dose;
        this.animal = animal;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTipoVacina() {
        return tipoVacina;
    }

    public void setTipoVacina(String tipoVacina) {
        this.tipoVacina = tipoVacina;
    }

    public LocalDate getDataVacinacao() {
        return dataVacinacao;
    }

    public void setDataVacinacao(LocalDate dataVacinacao) {
        this.dataVacinacao = dataVacinacao;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }
}