package com.upt.lp.objetosperdidos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.upt.lp.objetosperdidos.entity.Anuncio;
import com.upt.lp.objetosperdidos.entity.Chat;
import com.upt.lp.objetosperdidos.entity.Estado;
import com.upt.lp.objetosperdidos.entity.Utilizador;
import com.upt.lp.objetosperdidos.repository.ChatRepository;
import com.upt.lp.objetosperdidos.repository.MensagemRepository; 



@Service
public class ChatService {
	
	private final ChatRepository chatRepository;
	private final MensagemRepository mensagemRepository;
	private final AnuncioService anuncioService;
	private final UtilizadorService utilizadorService;

    public ChatService(ChatRepository chatRepository, MensagemRepository mensagemRepository, AnuncioService anuncioService, UtilizadorService utilizadorService) {
    	
        this.chatRepository = chatRepository;
        this.mensagemRepository = mensagemRepository;
        this.anuncioService=anuncioService;
        this.utilizadorService = utilizadorService;
    }
    
    
    public Chat getChat(int id) {
		return chatRepository.findById(id)
				.orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Chat não encontrado."));
	 } 
    
    @Transactional (readOnly = true)
	public List<Chat> getChatsDoUtilizador(int utilizadorId) { 
		 utilizadorService.getUtilizador(utilizadorId);
		 return chatRepository.findByUtilizadores_IdUtilizador(utilizadorId);
	  }
	
	
	//Reinvidicar um anuncio : cria o chat entre o dono e o interessado
    @Transactional
	public Chat criarChat(int AnuncioId, int interessadoId) { 
    	Anuncio anuncio = anuncioService.getAnuncio(anuncioId);
    	Utilizador interessado = utilizadorService.getAtivo(interessadoId);
    	
    	if(anuncio.getEstado()!= Estado.PENDENTE) {
    		throw new ResponseStatusException(HttpStatus.CONFLICT, "O anuncio já está resolvido.");
    	}
    	if(anuncio.getUtilizador().getIdUtilizador() == interessadoId) {
    		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nao podes revindicar o teu próprio anuncio.");
    	}
    	if(chatRepository.existsByAnuncio_IdAnuncioAndUtilizadores_IdUtilizador(anuncioId, interessadoId)) {
    		throw new ResponseStatusException(HttpStatus.CONFLICT, "Já reivindicaste este anuncio");
    	}
    	return chatRepository.save(new Chat(anuncio, interessado));
    			
	 } 
 } 

