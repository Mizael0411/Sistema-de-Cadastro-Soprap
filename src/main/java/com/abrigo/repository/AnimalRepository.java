package com.abrigo.repository;

import java.util.List;

import com.abrigo.database.JPAUtil;
import com.abrigo.dto.AnimalNaoAdotadoDTO;
import com.abrigo.model.Animal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

public class AnimalRepository {

    public List<AnimalNaoAdotadoDTO> findAnimaisSemAdocao() {
        EntityManager em = JPAUtil.getEntityManager();
        try {
            TypedQuery<AnimalNaoAdotadoDTO> query = em.createQuery("""
                    select new com.abrigo.dto.AnimalNaoAdotadoDTO(
                        a.id,
                        a.nome,
                        case 
                            when exists (
                                select 1
                                from Adocao ad
                                where ad.animal.id = a.id
                                    and ad.statusAdocao = 'Adotado Lar Temporario'
                                    and ad.statusRegistro = 'ATIVA'
                                )
                                then 'Em Lar Temporario'
                                else 'Nao Adotado'
                        end
                    )
                    from Animal a
                    where a.ativo = true
                        and not exists (
                            select 1
                            from Adocao ad
                            where ad.animal.id = a.id
                                and ad.statusAdocao = 'Adotado Definitivo'
                                and ad.statusRegistro = 'ATIVA'
                        )
                    """, AnimalNaoAdotadoDTO.class);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Animal findById(Long id) {
        if (id == null) {
            return null;
        }

        EntityManager em = JPAUtil.getEntityManager();

        try {
            return em.find(Animal.class, id);
        } finally {
            em.close();
        }
    }
}
