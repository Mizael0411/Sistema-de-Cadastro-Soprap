package com.abrigo.repository;

import java.util.List;

import com.abrigo.dto.AnimalNaoAdotadoDTO;
import com.abrigo.database.JPAUtil;

import com.abrigo.model.Animal;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class AnimalRepository {

    // Tela 1: um animal é "não adotado" enquanto não existir nenhum
    // registro em Adocao apontando pra ele. Não há coluna de status.
    public List<AnimalNaoAdotadoDTO> findAnimaisSemAdocao() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AnimalNaoAdotadoDTO> query = em.createQuery("""
                    select new com.abrigo.dto.AnimalNaoAdotadoDTO(
                        a.id, a.nome, 'Nao Adotado'
                    )
                    from Animal a
                    where not exists (
                        select 1 from Adocao ad where ad.animal = a
                    )
                    """, AnimalNaoAdotadoDTO.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Animal findById(Long aLong) {
        return null;
    }
}
