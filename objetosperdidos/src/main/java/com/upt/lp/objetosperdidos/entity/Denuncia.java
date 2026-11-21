package com.upt.lp.objetosperdidos.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.persistence.*;

/**
 * Representa uma denúncia submetida por um {@link Utilizador} contra outro
 * no contexto de um {@link Chat}.
 * <p>
 * Esta classe é uma entidade JPA, mapeada na tabela {@code denuncia}, com uma
 * restrição única para impedir múltiplas denúncias do mesmo denunciante no mesmo chat.
 */

@Entity
@Table(
		name = "denuncia",
		uniqueConstraints = {
				@UniqueConstraint(columnNames = { "idDenunciante", "idChat" })
		}
)
public class Denuncia {
	
	/** Identificador único da denúncia (gerado automaticamente pela base de dados). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDenuncia")
    private int idDenuncia;
    
    /** Utilizador que efetuou a denúncia (coluna {@code idDenunciante}). Obrigatório. */
    @ManyToOne
    @JoinColumn(name = "idDenunciante", nullable = false)
    private Utilizador denunciante;
    
    /** Utilizador alvo da denúncia (coluna {@code idDenunciado}). Obrigatório. */
    @ManyToOne
    @JoinColumn(name = "idDenunciado", nullable = false)
    private Utilizador denunciado;

    /** Chat associado à denúncia (coluna {@code idChat}). Obrigatório. */
    @ManyToOne
    @JoinColumn(name = "idChat", nullable = false)
    private Chat chat;

    /** Estado atual da denúncia. Por omissão inicia em {@link Estado#PENDENTE}. */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private Estado estado = Estado.PENDENTE;

    /** Data e hora em que a denúncia foi registada. Obrigatória. */
    @Column(nullable = false)
    private LocalDateTime dataHora;
    
    /**
     * Construtor vazio, necessário para o JPA/Hibernate criar instâncias da entidade.
     */
    public Denuncia() {
    }
    
    /**
     * Cria uma nova denúncia com estado {@link Estado#PENDENTE} e data/hora atual.
     *
     * @param denunciante utilizador que denuncia
     * @param denunciado  utilizador denunciado
     * @param chat        chat onde ocorreu a situação
     */
    public Denuncia(Utilizador denunciante, Utilizador denunciado, Chat chat) {
        this.denunciante = denunciante;
        this.denunciado = denunciado;
        this.chat = chat;
        this.estado = Estado.PENDENTE;
        this.dataHora = LocalDateTime.now();
    }
    
    public int getIdDenuncia() {
        return idDenuncia;
    }

    public Utilizador getDenunciante() {
        return denunciante;
    }

    public void setDenunciante(Utilizador denunciante) {
        this.denunciante = denunciante;
    }

    public Utilizador getDenunciado() {
        return denunciado;
    }

    public void setDenunciado(Utilizador denunciado) {
        this.denunciado = denunciado;
    }
    
    public Chat getChat() {
        return chat;
    }

    public void setChat(Chat chat) {
        this.chat = chat;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public void setDataHora(LocalDateTime dataHora) {
        this.dataHora = dataHora;
    }

    @Override
    public String toString() {
        DateTimeFormatter formato = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return "Denuncia #" + idDenuncia
                + " [" + estado + "] "
                + denunciante + " -> " + denunciado
                + " (Chat #" + (chat != null ? chat.getIdChat() : null) + ") "
                + dataHora.format(formato);
    }
}
