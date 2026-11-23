package com.upt.lp.objetosperdidos.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.upt.lp.objetosperdidos.entity.Chat;
import com.upt.lp.objetosperdidos.entity.Denuncia;
import com.upt.lp.objetosperdidos.entity.Estado;

public interface DenunciaRepository extends JpaRepository<Denuncia, Integer>{

	boolean existsByDenunciante_IdUtilizadorAndChat_IdChat(int idDenunciante, int idChat);

	List<Denuncia> findByEstado(Estado estado);
	
	List<Denuncia> findByDenunciado_IdUtilizador(int idUtilizador);
	
	long countByDenunciado_IdUtilizadorAndEstado(int idUtilizador, Estado estado);
	
	@Query("SELECT DISTINCT d.chat FROM Denuncia d WHERE d.estado = :estado")
    List<Chat> findChatsComDenunciasPorEstado(@Param("estado") Estado estado);
	
}
