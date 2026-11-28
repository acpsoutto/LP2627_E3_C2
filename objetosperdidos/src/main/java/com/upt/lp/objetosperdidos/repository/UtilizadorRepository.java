package com.upt.lp.objetosperdidos.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upt.lp.objetosperdidos.entity.TipoUtilizador;
import com.upt.lp.objetosperdidos.entity.Utilizador;

public interface UtilizadorRepository extends JpaRepository<Utilizador, Integer> {

    Optional<Utilizador> findByEmail(String email);
    long countByTipo(TipoUtilizador tipo);
}