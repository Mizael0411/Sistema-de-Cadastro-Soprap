package com.abrigo.dao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Vacina;

import jakarta.persistence.EntityManager;

public class VacinaDAO {

    public boolean salvar(Vacina vacina) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(vacina);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao salvar vacina: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public boolean atualizar(Vacina vacina) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(vacina);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao atualizar vacina: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public boolean excluir(Long vacinaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            Vacina vacina = em.find(Vacina.class, vacinaId);
            if (vacina != null) {
                em.remove(vacina);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao excluir vacina: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public Vacina buscarPorId(Long vacinaId) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(Vacina.class, vacinaId);
        } catch (Exception e) {
            System.err.println("Erro ao buscar vacina por ID: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public List<Vacina> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        List<Vacina> vacinas = new ArrayList<>();
        try {
            vacinas = em.createQuery("SELECT v FROM Vacina v", Vacina.class)
                        .getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao listar todas as vacinas: " + e.getMessage());
        } finally {
            em.close();
        }
        return vacinas;
    }

    public List<Vacina> buscarPorAnimalId(Object animalId) {
        EntityManager em = JPAUtil.getEntityManager();
        List<Vacina> vacinas = new ArrayList<>();
        try {
            vacinas = em.createQuery("SELECT v FROM Vacina v WHERE v.animal.id = :animalId", Vacina.class)
                        .setParameter("animalId", animalId)
                        .getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao buscar histórico de vacina do animal: " + e.getMessage());
        } finally {
            em.close();
        }
        return vacinas;
    }

    public List<Vacina> listarPorAnimal(Long idAnimal) {
        if (idAnimal == null) return Collections.emptyList();
        
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT v FROM Vacina v WHERE v.animal.id = :idAnimal", Vacina.class)
                     .setParameter("idAnimal", idAnimal)
                     .getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao listar vacinas por animal: " + e.getMessage());
            return Collections.emptyList();
        } finally {
            em.close();
        }
    }
}