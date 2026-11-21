package com.upt.lp.objetosperdidos.service;

import org.springframework.http.HttpStatus; 
import org.springframework.web.server.ResponseStatusException;
import com.upt.lp.objetosperdidos.entity.Mensagem;
import com.upt.lp.objetosperdidos.repository.MensagemRepository; 
import org.springframework.stereotype.Service; 
import java.util.List; 

@Service
public class MensagemService{
	private final MensagemRepository mensagemRepository;
	private final ChatRepository chatRepository;
	private final UtilizadorRepository utilizadorRepository;

	public MensagemService(MensagemRepository mensagemRepository,ChatRepository chatRepository,UtilizadorRepository utilizadorRepository) {
		this.mensagemRepository = mensagemRepository;
		this.chatRepository = chatRepository;
		this.utilizadorRepository = utilizadorRepository;
	}
	
	 public List<Mensagem> getAllMensagens() { 
	        return mensagemRepository.findAll(); 
	  }
	 
	 public Mensagem getMensagem(Long id) { 
	    	return mensagemRepository.findById(id) 
	    	.orElseThrow(() -> new ResponseStatusException( 
	    	HttpStatus.NOT_FOUND, "Mensagem not found")); 
	    } 
	 
	    public Mensagem createMensagem(Mensagem m) { 
	    	 if (m.getChat() == null 
		                || m.getChat().getId() == null) { 
		            throw new ResponseStatusException( 
		                HttpStatus.BAD_REQUEST, 
		                "A Chat identifier is required"); 
		        } 
	    	 if (m.getEmissor() == null 
		                || m.getEmissor().getId() == null) { 
		            throw new ResponseStatusException( 
		                HttpStatus.BAD_REQUEST, 
		                "A emissor identifier is required"); 
		        } 
	    	 Utilizdor emissor = utilizadorRepository 
	 	            .findById(m.getEmissor().getId()) 
	 	            .orElseThrow(() -> new ResponseStatusException( 
	 	                HttpStatus.NOT_FOUND, "Utilizador not found"));
	    	 
	    	 Chat chat = chatRepository 
		 	            .findById(m.getChat().getId()) 
		 	            .orElseThrow(() -> new ResponseStatusException( 
		 	                HttpStatus.NOT_FOUND, "Chat not found"));
	    	 
	    	 
	    	Mensagem mensagem = new Mensagem();
	    	 mensagem.setTexto(m.getTexto());
	    	 mensagem.setHora(m.getHora());
	    	 mensagem.setEmissor(emissor);
	    	 mensagem.setChat(chat);
	    	 
	    	 return mensagemRepository.save(mensagem);

	    } 

	 
	public Mensagem updateMensagem(Long id, Mensagem m) { 
	      Mensagem existing = mensagemRepository.findById(id) 
	          .orElseThrow(() -> new RuntimeException("Mensagem not found")); 
	      existing.setTexto(m.getTexto()); 
	      existing.setChat(m.getChat()); 
	      existing.setHora(m.getHora());
	      existing.setEmissor(m.getEmissor());
	      
	 
	      return mensagemRepository.save(existing); 
	  } 
	 
	public void deleteMensagem(Long id) {  
    	Mensagem existing = getMensagem(id); 
    	mensagemRepository.delete(existing); 
    } 
} 