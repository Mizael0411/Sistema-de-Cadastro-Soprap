package com.abrigo.dao;

import java.util.ArrayList;
import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.Adocao;

import jakarta.persistence.EntityManager;

public class AdocaoDAO {

    public boolean salvar(Adocao adocao) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.persist(adocao);
            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println("Erro ao salvar adoção: " + e.getMessage());
            return false;

        } finally {
            em.close();
        }
    }

    public List<Adocao> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.createQuery(
                "SELECT a FROM Adocao a ORDER BY a.dataAdocao DESC",
                Adocao.class
            ).getResultList();

        } catch (Exception e) {
            System.err.println("Erro ao listar adoções: " + e.getMessage());
            return new ArrayList<>();

        } finally {
            em.close();
        }
    }

    public Adocao buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Adocao.class, id);

        } catch (Exception e) {
            System.err.println("Erro ao buscar adoção por ID: " + e.getMessage());
            return null;

        } finally {
            em.close();
        }
    }

    public Adocao buscarPorAnimalId(Long idAnimal) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            List<Adocao> resultados = em.createQuery(
                "SELECT a FROM Adocao a " +
                "LEFT JOIN FETCH a.larTemp " +
                "WHERE a.animal.id = :idAnimal " +
                "ORDER BY a.dataAdocao DESC",
                Adocao.class
            )
            .setParameter("idAnimal", idAnimal)
            .setMaxResults(1)
            .getResultList();

            return resultados.isEmpty() ? null : resultados.get(0);

        } catch (Exception e) {
            System.err.println(
                "Erro ao buscar adoção do animal: " + e.getMessage()
            );
            return null;

        } finally {
            em.close();
        }
    }

    public boolean atualizar(Adocao adocao) {
        EntityManager em = JPAUtil.getEntityManager();

        try {
            em.getTransaction().begin();
            em.merge(adocao);
            em.getTransaction().commit();
            return true;

        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }

            System.err.println("Erro ao atualizar adoção: " + e.getMessage());
            return false;

        } finally {
            em.close();
        }
    }
}
