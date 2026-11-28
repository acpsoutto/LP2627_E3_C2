package com.upt.lp.objetosperdidos.dto;

import com.upt.lp.objetosperdidos.entity.TipoUtilizador;

public record UtilizadorDTO(
        Integer id,
        String nome,
        String email,
        TipoUtilizador tipo
) {}