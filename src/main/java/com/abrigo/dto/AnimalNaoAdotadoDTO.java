package com.abrigo.dto;

// Tela 1: Animais sem adoção
// Campos: id_animal, nome, statusAdocao ("Nao Adotado")
public record AnimalNaoAdotadoDTO(
        Long idAnimal,
        String nome,
        String statusAdocao
) implements ItemAdocao {}
