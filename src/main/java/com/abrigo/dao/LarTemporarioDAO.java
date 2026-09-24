package com.abrigo.dao;

import java.util.ArrayList;
import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.model.LarTemp;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;

public class LarTemporarioDAO {

    public boolean salvar(LarTemp lar) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(lar);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao salvar lar temporário no banco: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }


    public List<LarTemp> listarTodos() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.createQuery("SELECT l FROM LarTemp l", LarTemp.class).getResultList();
        } catch (Exception e) {
            System.err.println("Erro ao listar lares temporários do banco: " + e.getMessage());
            return new ArrayList<>();
        } finally {
            em.close();
        }
    }

    public LarTemp buscarPorId(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            return em.find(LarTemp.class, id);
        } catch (Exception e) {
            System.err.println("Erro ao buscar lar temporário por ID: " + e.getMessage());
            return null;
        } finally {
            em.close();
        }
    }

    public boolean atualizar(LarTemp lar) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            em.merge(lar);
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao atualizar lar temporário " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }

    public boolean excluir(Long id) {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            em.getTransaction().begin();
            LarTemp lar = em.find(LarTemp.class, id);
            if (lar != null) {
                em.remove(lar);
            }
            em.getTransaction().commit();
            return true;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            System.err.println("Erro ao remover lar temporário: " + e.getMessage());
            return false;
        } finally {
            em.close();
        }
    }
}
