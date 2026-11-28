package com.upt.lp.objetosperdidos.dto;

import jakarta.validation.constraints.NotNull;

public record CriarDenunciaDTO (
		
		@NotNull Integer idDenunciante,
		@NotNull Integer idDenunciado,
		@NotNull Integer idChat
)
{}
