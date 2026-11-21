package com.upt.lp.objetosperdidos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import com.upt.lp.objetosperdidos.entity.Chat;
import com.upt.lp.objetosperdidos.repository.ChatRepository; 



@Service
public class ChatService {
	
	private final ChatRepository chatRepository;

    public ChatService(ChatRepository chatRepository) {
        this.chatRepository = chatRepository;
    }
    
	public List<Chat> getAllChats() { 
		 return chatRepository.findAll();
	  }
	 
	public Chat getChat() { 
		 
	 } 
	 
	public Chat createChat() { 
	 } 
	 
	public Chat updateChat() { 
	} 
	 
	public void deleteChat() {  
	}
 } 

