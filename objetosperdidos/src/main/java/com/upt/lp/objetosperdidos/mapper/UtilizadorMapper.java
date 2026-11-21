package com.upt.lp.objetosperdidos.mapper;

import com.upt.lp.objetosperdidos.dto.UtilizadorDTO;
import com.upt.lp.objetosperdidos.entity.Utilizador;

public class UtilizadorMapper {

    public static UtilizadorDTO toDTO(Utilizador utilizador) {
        return new UtilizadorDTO(
            utilizador.getIdUtilizador(),
            utilizador.getNome(),
            utilizador.getEmail(),
            utilizador.getTipo()
        );
    }
}