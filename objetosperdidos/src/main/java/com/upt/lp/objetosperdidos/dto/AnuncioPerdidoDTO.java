package com.upt.lp.objetosperdidos.dto;

import java.time.LocalDate;

import com.upt.lp.objetosperdidos.entity.Categoria;
import com.upt.lp.objetosperdidos.entity.Estado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

public interface AnuncioPerdidoDTO {

	record Request(
			@NotNull Integer utilizadorId,
			@NotBlank @Size(max = 100) String titulo,
			@NotNull Categoria categoria,
			@NotBlank @Size(max = 30) String cor,
			@NotBlank @Size(max = 500) String descricao,
			@NotNull @PastOrPresent LocalDate dataPerda) {
	}

	record Response(
			int id,
			String titulo,
			Categoria categoria,
			String cor,
			String descricao,
			LocalDate dataPerda,
			LocalDate dataPublicacao,
			Estado estado,
			int publicadorId,
			String publicadorNome) {
	}
}
