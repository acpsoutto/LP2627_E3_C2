package com.upt.lp.objetosperdidos.dto;

import java.time.LocalDateTime;

import com.upt.lp.objetosperdidos.entity.Estado;
import com.upt.lp.objetosperdidos.entity.Denuncia;


public record DenunciaDTO(
		int idDenuncia,
		int idDenunciante,
		String nomeDenunciante,
		int idDenunciado,
		String nomeDenunciado,
		int idChat,
		Estado estado,
		LocalDateTime dataHora
){
	public static DenunciaDTO fromEntity(Denuncia d) {
		return new DenunciaDTO(
				d.getIdDenuncia(),
				d.getDenunciante() != null ? d.getDenunciante().getIdUtilizador() : 0,
				d.getDenunciante() != null ? d.getDenunciante().getNome() : null,
				d.getDenunciado() != null ? d.getDenunciado().getIdUtilizador() : 0,
				d.getDenunciado() != null ? d.getDenunciado().getNome() : null,
				d.getChat() != null ? d.getChat().getIdChat() : 0,
				d.getEstado(),
				d.getDataHora()
				);
	}
}
