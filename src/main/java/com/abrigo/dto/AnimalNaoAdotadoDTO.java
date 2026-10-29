package com.abrigo.dto;

public record AnimalNaoAdotadoDTO(
        Long idAnimal,
        String nome,
        String statusAdocao
) implements ItemAdocao {}
