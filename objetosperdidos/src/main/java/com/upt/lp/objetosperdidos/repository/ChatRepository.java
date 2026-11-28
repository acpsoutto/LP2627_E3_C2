package com.upt.lp.objetosperdidos.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.upt.lp.objetosperdidos.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Integer> {
	
	List<Chat>findByUtilizadores_IdUtilizador(int idUtilizador);
	boolean existsByAnuncio_IdAnuncioAndUtilizadores_IdUtilizador(int idAnuncio, int idUtilizador);
}
