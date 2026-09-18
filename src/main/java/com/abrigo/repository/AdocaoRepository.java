package com.abrigo.repository;

import java.time.LocalDate;
import java.util.List;

import com.abrigo.dto.AdocaoDefinitivaDTO;
import com.abrigo.dto.AdocaoLarTemporarioDTO;
import com.abrigo.model.Adocao;
import com.abrigo.model.Animal;
import com.abrigo.model.LarTemp;
import com.abrigo.database.JPAUtil;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.TypedQuery;

public class AdocaoRepository {

    // Tela 2
    public List<AdocaoDefinitivaDTO> findAdocoesDefinitivas() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AdocaoDefinitivaDTO> query = em.createQuery("""
                    select new com.abrigo.dto.AdocaoDefinitivaDTO(
                        an.id, an.nome, a.statusAdocao, a.nomeTutor, a.id
                    )
                    from Adocao a
                    join a.animal an
                    where a.statusAdocao = 'Adotado Definitivo'
                    """, AdocaoDefinitivaDTO.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Tela 3
    public List<AdocaoLarTemporarioDTO> findAdocoesLarTemporario() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AdocaoLarTemporarioDTO> query = em.createQuery("""
                    select new com.abrigo.dto.AdocaoLarTemporarioDTO(
                        an.id, an.nome, a.statusAdocao, lt.id, a.nomeTutor, a.id
                    )
                    from Adocao a
                    join a.animal an
                    join a.larTemp lt
                    where a.statusAdocao = 'Adotado Lar Temporario'
                    """, AdocaoLarTemporarioDTO.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Usado pelo botão "Adotar Definitivo" na tela 1.
    // getReference evita carregar o Animal inteiro só pra usar como FK.
    public void registrarAdocaoDefinitiva(Long idAnimal, String nomeTutor) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Animal animalRef = em.getReference(Animal.class, idAnimal);
            Adocao adocao = new Adocao(LocalDate.now(), nomeTutor, false, animalRef, "Adotado Definitivo", null);
            em.persist(adocao);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Usado pelo botão "Adotar Lar Temporário" na tela 1.
    public void registrarAdocaoLarTemporario(Long idAnimal, Long idLarTemp, String nomeTutor) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Animal animalRef = em.getReference(Animal.class, idAnimal);
            LarTemp larTempRef = em.getReference(LarTemp.class, idLarTemp);
            Adocao adocao = new Adocao(LocalDate.now(), nomeTutor, false, animalRef, "Adotado Lar Temporario", larTempRef);
            em.persist(adocao);
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    // Usado pelo botão "Cancelar Adoção" nas telas 2 e 3.
    // Ao remover o registro, o animal volta a aparecer automaticamente
    // na tela de "sem adoção" (a query de lá é um NOT EXISTS).
    public void cancelarAdocao(Long idAdocao) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Adocao adocao = em.find(Adocao.class, idAdocao);
            if (adocao != null) {
                em.remove(adocao);
            }
            tx.commit();
        } catch (RuntimeException e) {
            if (tx.isActive()) {
                tx.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
