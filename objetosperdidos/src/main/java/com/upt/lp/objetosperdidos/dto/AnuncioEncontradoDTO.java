package com.upt.lp.objetosperdidos.dto;

import java.time.LocalDate;

import com.upt.lp.objetosperdidos.entity.Categoria;
import com.upt.lp.objetosperdidos.entity.Estado;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface AnuncioEncontradoDTO {

	/** Corpo do POST/PUT. A data de publicação e o estado são definidos pelo servidor. */
	record Request(
			@NotNull Integer utilizadorId,
			@NotBlank @Size(max = 100) String titulo,
			@NotNull Categoria categoria,
			@NotBlank @Size(max = 30) String cor,
			@NotBlank @Size(max = 500) String descricao,
			@NotBlank @Size(max = 100) String localizacao) {
	}

	/** Resposta: não expõe a entidade Utilizador (password, ciclos JSON). */
	record Response(
			int id,
			String titulo,
			Categoria categoria,
			String cor,
			String descricao,
			String localizacao,
			LocalDate dataPublicacao,
			Estado estado,
			int publicadorId,
			String publicadorNome) {
	}
}
