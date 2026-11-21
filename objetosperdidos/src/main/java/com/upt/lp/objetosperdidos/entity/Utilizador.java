package com.upt.lp.objetosperdidos.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

@Entity
@Table(name = "utilizador")
public class Utilizador {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUtilizador")
    private int idUtilizador;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(nullable = false, length = 150)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TipoUtilizador tipo;

    @JsonIgnore
    @OneToMany(mappedBy = "utilizador", cascade = CascadeType.PERSIST)
    private List<Anuncio> anuncios = new ArrayList<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "utilizadores")
    private List<Chat> chats = new ArrayList<>();

    public Utilizador() {}

    public Utilizador(String nome, String email, String password, TipoUtilizador tipo) {
        this.nome = nome;
        this.email = email;
        this.password = password;
        this.tipo = tipo;
    }

    public int getIdUtilizador() { 
    	return idUtilizador; 
    	}
    public String getNome() { 
    	return nome; 
    	}
    public void setNome(String nome) { 
    	this.nome = nome; 
    	}
    public String getEmail() { 
    	return email; 
    	}
    public void setEmail(String email) { 
    	this.email = email; 
    	}
    public String getPassword() { 
    	return password; 
    	}
    public void setPassword(String password) { 
    	this.password = password; 
    	}
    public TipoUtilizador getTipo() { 
    	return tipo; 
    	}
    public void setTipo(TipoUtilizador tipo) { 
    	this.tipo = tipo; 
    	}
    public List<Anuncio> getAnuncios() { 
    	return anuncios; 
    	}
    public List<Chat> getChats() { 
    	return chats; 
    	}
}