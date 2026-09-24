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

    public void registrarAdocaoDefinitiva(Long idAnimal, String nomeTutor) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            if (possuiAdocao(em, idAnimal)) {
                throw new IllegalStateException("Este animal já possui uma adoção registrada.");
            }

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


    public void registrarAdocaoLarTemporario(Long idAnimal, Long idLarTemp, String nomeTutor) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();

            if (possuiAdocao(em, idAnimal)) {
                throw new IllegalStateException("Este animal já possui uma adoção registrada.");
            }

            LarTemp larTemp = em.find(LarTemp.class, idLarTemp);
            if (larTemp == null) {
                throw new IllegalStateException("Lar temporário não encontrado.");
            }
            Integer vagas = larTemp.getVagasDisponiveis();
            if (vagas == null || vagas <= 0) {
                throw new IllegalStateException("Este lar temporário não tem vagas disponíveis.");
            }
            larTemp.setVagasDisponiveis(vagas - 1); // entidade já managed, não precisa de merge

            Animal animalRef = em.getReference(Animal.class, idAnimal);
            Adocao adocao = new Adocao(LocalDate.now(), nomeTutor, false, animalRef, "Adotado Lar Temporario", larTemp);
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


    private boolean possuiAdocao(EntityManager em, Long idAnimal) {
        Long existentes = em.createQuery("""
                select count(ad)
                from Adocao ad
                where ad.animal.id = :idAnimal
                """, Long.class)
                .setParameter("idAnimal", idAnimal)
                .getSingleResult();
        return existentes > 0;
    }


    public void cancelarAdocao(Long idAdocao) {
        EntityManager em = JPAUtil.getEntityManager();
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            Adocao adocao = em.find(Adocao.class, idAdocao);
            if (adocao != null) {
                LarTemp larTemp = adocao.getLarTemp();
                if (larTemp != null) {
                    Integer vagas = larTemp.getVagasDisponiveis();
                    Integer capacidade = larTemp.getCapacidadeMaxima();
                    int novaVaga = (vagas != null ? vagas : 0) + 1;
                    if (capacidade != null) {
                        novaVaga = Math.min(novaVaga, capacidade); // nunca passa da capacidade máxima
                    }
                    larTemp.setVagasDisponiveis(novaVaga);
                }
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