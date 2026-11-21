package com.upt.lp.objetosperdidos.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.upt.lp.objetosperdidos.entity.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {}
