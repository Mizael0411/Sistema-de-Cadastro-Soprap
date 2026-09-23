package com.abrigo.dto;


public record AdocaoLarTemporarioDTO(
        Long idAnimal,
        String nomeAnimal,
        String statusAdocao,
        Long idLarTemp,
        String nomeTutor,
        Long idAdocao
) implements ItemAdocao {}
