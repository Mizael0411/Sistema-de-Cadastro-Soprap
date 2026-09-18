package com.abrigo.dto;


public record AdocaoDefinitivaDTO(
        Long idAnimal,
        String nomeAnimal,
        String statusAdocao,
        String nomeTutor,
        Long idAdocao
) {}
