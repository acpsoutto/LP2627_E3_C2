package com.upt.lp.objetosperdidos.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.upt.lp.objetosperdidos.entity.Chat;
import com.upt.lp.objetosperdidos.entity.Denuncia;
import com.upt.lp.objetosperdidos.entity.Estado;
import com.upt.lp.objetosperdidos.entity.TipoUtilizador;
import com.upt.lp.objetosperdidos.entity.Utilizador;
import com.upt.lp.objetosperdidos.repository.ChatRepository;
import com.upt.lp.objetosperdidos.repository.DenunciaRepository;
import com.upt.lp.objetosperdidos.repository.UtilizadorRepository;

@Service
public class DenunciaService {

    private static final int LIMITE_DENUNCIAS_BAN = 5;

    private final DenunciaRepository denunciaRepository;
    private final UtilizadorRepository utilizadorRepository;
    private final ChatRepository chatRepository;

    public DenunciaService(
            DenunciaRepository denunciaRepository,
            UtilizadorRepository utilizadorRepository,
            ChatRepository chatRepository) {
        this.denunciaRepository = denunciaRepository;
        this.utilizadorRepository = utilizadorRepository;
        this.chatRepository = chatRepository;
    }

    @Transactional
    public Denuncia criarDenuncia(int idDenunciante, int idDenunciado, int idChat) {
        if (idDenunciante == idDenunciado) {
            throw new IllegalArgumentException("Um utilizador não pode denunciar-se a si próprio.");
        }

        Utilizador denunciante = utilizadorRepository.findById(idDenunciante)
                .orElseThrow(() -> new IllegalArgumentException("Denunciante não encontrado (ID: " + idDenunciante + ")."));

        Utilizador denunciado = utilizadorRepository.findById(idDenunciado)
                .orElseThrow(() -> new IllegalArgumentException("Denunciado não encontrado (ID: " + idDenunciado + ")."));

        Chat chat = chatRepository.findById(idChat)
                .orElseThrow(() -> new IllegalArgumentException("Chat não encontrado (ID: " + idChat + ")."));

        if (denunciaRepository.existsByDenunciante_IdUtilizadorAndChat_IdChat(idDenunciante, idChat)) {
            throw new IllegalStateException("Já existe uma denúncia deste utilizador neste chat.");
        }

        Denuncia denuncia = new Denuncia(denunciante, denunciado, chat);
        return denunciaRepository.save(denuncia);
    }

    public List<Denuncia> listarTodas() {
        return denunciaRepository.findAll();
    }

    public List<Denuncia> listarPendentes() {
        return denunciaRepository.findByEstado(Estado.PENDENTE);
    }

    public List<Chat> listarChatsDenunciadosPendentes() {
        return denunciaRepository.findChatsComDenunciasPorEstado(Estado.PENDENTE);
    }

    @Transactional
    public Denuncia resolverDenuncia(int idDenuncia, int opcao) {
        if (opcao != 1 && opcao != 2) {
            throw new IllegalArgumentException("Opção inválida. Use 1 para ACEITE ou 2 para DESCARTADO.");
        }

        Denuncia denuncia = denunciaRepository.findById(idDenuncia)
                .orElseThrow(() -> new IllegalArgumentException("Denúncia não encontrada (ID: " + idDenuncia + ")."));

        if (denuncia.getEstado() != Estado.PENDENTE) {
            throw new IllegalStateException("A denúncia #" + idDenuncia + " já foi resolvida (estado atual: " + denuncia.getEstado() + ").");
        }

        int idUtilizadorDenunciado = denuncia.getDenunciado().getIdUtilizador();

        if (opcao == 1) {
            denuncia.setEstado(Estado.ACEITE);
        } else {
            denuncia.setEstado(Estado.DESCARTADO);
        }

        Denuncia guardada = denunciaRepository.save(denuncia);

        if (opcao == 1) {
            atualizarBanUtilizador(idUtilizadorDenunciado);
        }

        return guardada;
    }

    @Transactional
    public Utilizador atualizarBanUtilizador(int idUtilizador) {
        Utilizador utilizador = utilizadorRepository.findById(idUtilizador)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado (ID: " + idUtilizador + ")."));

        long totalAceites = denunciaRepository.countByDenunciado_IdUtilizadorAndEstado(idUtilizador, Estado.ACEITE);

        if (totalAceites >= LIMITE_DENUNCIAS_BAN && utilizador.getTipo() != TipoUtilizador.BANIDO) {
            utilizador.setTipo(TipoUtilizador.BANIDO);
            return utilizadorRepository.save(utilizador);
        }

        return utilizador;
    }

    @Transactional
    public Utilizador banirUtilizadorDiretamente(int idUtilizador) {
        Utilizador utilizador = utilizadorRepository.findById(idUtilizador)
                .orElseThrow(() -> new IllegalArgumentException("Utilizador não encontrado (ID: " + idUtilizador + ")."));

        if (utilizador.getTipo() != TipoUtilizador.BANIDO) {
            utilizador.setTipo(TipoUtilizador.BANIDO);
            return utilizadorRepository.save(utilizador);
        }

        return utilizador;
    }
}