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

	public MensagemService(MensagemRepository mensagemRepository) {
		this.mensagemRepository = mensagemRepository;
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
	    	Mensagem mensagem = new Mensagem();
	    	 mensagem.setTexto(m.getTexto());
	    	 mensagem.setChat(m.getChat());
	    	 mensagem.setEmissor(m.getEmissor());
	    	 mensagem.setHora(m.getHora());
	    	 
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