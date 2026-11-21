package com.upt.lp.objetosperdidos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.upt.lp.objetosperdidos.entity.AnuncioPerdido;
import com.upt.lp.objetosperdidos.entity.Categoria;
import com.upt.lp.objetosperdidos.entity.Estado;

public interface AnuncioPerdidoRepository extends JpaRepository<AnuncioPerdido, Integer> {

	@Query("""
			SELECT a FROM AnuncioPerdido a
			WHERE a.estado = :estado
			  AND (:categoria IS NULL OR a.categoria = :categoria)
			  AND (:cor IS NULL OR LOWER(a.cor) = LOWER(:cor))
			  AND (:titulo IS NULL OR LOWER(a.titulo) LIKE LOWER(CONCAT('%', :titulo, '%')))
			ORDER BY a.dataPublicacao DESC, a.idAnuncio DESC
			""")
	List<AnuncioPerdido> filtrar(@Param("estado") Estado estado,
			@Param("categoria") Categoria categoria,
			@Param("cor") String cor,
			@Param("titulo") String titulo);
}
