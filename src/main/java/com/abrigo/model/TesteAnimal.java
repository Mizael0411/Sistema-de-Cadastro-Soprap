package com.abrigo.model;

import java.time.LocalDate;

import com.abrigo.database.JPAUtil;

import jakarta.persistence.EntityManager;

public class TesteAnimal {
    public static void main(String[] args) {
        Animal animal = new Animal();
        animal.setNome("puppy");
        animal.setIdade(3);
        animal.setDataNascimento(LocalDate.of(2023, 1, 15));
        animal.setSexo("Macho");
        animal.setStatusVacinacao("Vacinado");
        animal.setStatusGravidez("Não se aplica");
        animal.setDataUltimaVacinacao(LocalDate.now());

        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(animal);
            em.getTransaction().commit();
            System.out.println("Sucesso: Tabela criada e animal salvo no Supabase!");
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Ocorreu um erro na persistência:");
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}