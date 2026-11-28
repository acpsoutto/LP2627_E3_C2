package com.upt.lp.objetosperdidos.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "chat")
public class Chat {
	
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idChat")
    private int idChat;

	
    @ManyToOne
    @JoinColumn(name = "idAnuncio", nullable = false)
    private Anuncio anuncio;

    @Column(nullable = false)
    private LocalDate dataCriacao;

    @JsonIgnore
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Mensagem> mensagens = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "chat_utilizador",
        joinColumns = @JoinColumn(name = "idChat"),
        inverseJoinColumns = @JoinColumn(name = "idUtilizador")
    )
    private List<Utilizador> utilizadores = new ArrayList<>();
    
    
    public Chat(Anuncio anuncio, Utilizador interessado) {
        this.anuncio = anuncio;
        this.dataCriacao = LocalDate.now();
        addUtilizador(anuncio.getUtilizador());
        addUtilizador(interessado);
    }
    
    
    public void addUtilizador(Utilizador u) {
        if (utilizadores.size() < 2 && !utilizadores.contains(u)) {
            utilizadores.add(u);
        }
        if (!u.getChats().contains(this)) {
            u.getChats().add(this);
        }
    }

    public boolean pertenceAoChat(Utilizador u) {
        for (Utilizador participante : utilizadores) {
            if (participante.getIdUtilizador() == u.getIdUtilizador()) {
                return true;
            }
        }
        return false;
    }

    
    public void adicionarMensagem(Mensagem m) {
        mensagens.add(m);
        m.setChat(this);

        if (m.getEmissor().getIdUtilizador() == anuncio.getUtilizador().getIdUtilizador()
                && m.getTexto().equalsIgnoreCase("Este anuncio ficou resolvido")) {
            anuncio.setEstado(Estado.RESOLVIDO);
            anuncio.setDataResolucao(LocalDate.now());
        }
    }

    
    @JsonIgnore
    public Utilizador getCriador() {
        return anuncio.getUtilizador();
    }

    @JsonIgnore
    public Utilizador getInteressado() {
        for (Utilizador u : utilizadores) {
            if (u.getIdUtilizador() != anuncio.getUtilizador().getIdUtilizador()) {
                return u;
            }
        }
        return null;
    }

    public int getIdChat() { 
    	return idChat; 
    }
    
    public Anuncio getAnuncio() { 
    	return anuncio; 
    }
    
    public void setAnuncio(Anuncio anuncio){ 
    	this.anuncio = anuncio; 
    }
    
    public LocalDate getDataCriacao() { 
    	return dataCriacao; 
    }
    
    public List<Mensagem> getMensagens() { 
    	return mensagens; 
    }
    
    public List<Utilizador> getUtilizador() { 
    	return utilizadores; 
    }
    
}

