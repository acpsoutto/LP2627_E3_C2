package com.upt.lp.objetosperdidos.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.upt.lp.objetosperdidos.entity.Mensagem;

public interface MensagemRepository extends JpaRepository<Mensagem, Long> {}