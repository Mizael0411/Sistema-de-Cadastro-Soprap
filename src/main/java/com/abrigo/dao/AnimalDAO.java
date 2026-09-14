package com.abrigo.dao;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Animal;
import jakarta.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

public class AnimalDAO {

    public boolean salvar(Animal animal) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(animal);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao salvar animal no banco: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public List<Animal> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT a FROM Animal a", Animal.class).getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao listar animais do banco: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public boolean atualizar(Animal animal) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(animal);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao atualizar animal: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public boolean deletar(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Animal animal = em.find(Animal.class, id);
            if (animal != null) {
                em.remove(animal);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao remover animal: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
}