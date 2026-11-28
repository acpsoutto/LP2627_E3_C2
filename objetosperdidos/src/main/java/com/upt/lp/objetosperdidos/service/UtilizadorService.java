package com.upt.lp.objetosperdidos.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;          // ou org.springframework.security.crypto.bcrypt.BCrypt
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.upt.lp.objetosperdidos.dto.UtilizadorAtualizacaoDTO;
import com.upt.lp.objetosperdidos.dto.UtilizadorRegistoDTO;
import com.upt.lp.objetosperdidos.entity.TipoUtilizador;
import com.upt.lp.objetosperdidos.entity.Utilizador;
import com.upt.lp.objetosperdidos.repository.UtilizadorRepository;

import jakarta.annotation.PostConstruct;

@Service
public class UtilizadorService {

    private static final String GESTOR_EMAIL = "gestor@upt.pt";
    private static final String GESTOR_PASSWORD = "Gestor@123";

    private final UtilizadorRepository utilizadorRepository;
    private final Map<String, Integer> tentativasPorEmail = new ConcurrentHashMap<>();

    private final PasswordValidator validadorPassword = new PasswordValidator(
            new CharacterRule(EnglishCharacterData.UpperCase, 1),
            new CharacterRule(EnglishCharacterData.Special, 1)
    );
    private final EmailValidator validadorEmail = EmailValidator.getInstance();

    public UtilizadorService(UtilizadorRepository utilizadorRepository) {
        this.utilizadorRepository = utilizadorRepository;
    }

    @PostConstruct
    public void criarGestorSeNaoExistir() {
        if (!utilizadorRepository.existsByEmail(GESTOR_EMAIL)) {
            Utilizador gestor = new Utilizador("Gestor", GESTOR_EMAIL, gerarHash(GESTOR_PASSWORD), TipoUtilizador.GESTOR);
            utilizadorRepository.save(gestor);
        }
    }
    
    public List<Utilizador> getAllUtilizadores() {
        return utilizadorRepository.findAll();
    }

    public Utilizador getUtilizador(Integer id) {
        return utilizadorRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Utilizador não encontrado"));
    }
    
    //REVER METODO
    public Utilizador registrar(UtilizadorRegistoDTO dados) {
        if (dados.nome() == null || dados.nome().isBlank()
                || dados.email() == null || dados.email().isBlank()
                || dados.password() == null || dados.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, email e password são obrigatórios.");
        }

        String nome = dados.nome().trim();
        String email = dados.email().trim();

        if (nome.length() > 100) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome demasiado longo.");
        }
        if (!emailValido(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de email inválido.");
        }
        if (!passwordValida(dados.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A password deve ter pelo menos uma maiúscula e um carácter especial.");
        }
        if (utilizadorRepository.existsByEmail(email)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já registado.");
        }

        Utilizador novo = new Utilizador(
                nome,
                email,
                gerarHash(dados.password()),
                TipoUtilizador.NORMAL   // o tipo é sempre definido aqui, nunca pelo cliente
        );
        return utilizadorRepository.save(novo);
    }

    
    //REVER
    public Utilizador autenticar(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email e password são obrigatórios.");
        }

        String emailLimpo = email.trim();

        Utilizador u = utilizadorRepository.findByEmail(emailLimpo).orElse(null);
        if (u == null || !BCrypt.checkpw(password, u.getPassword())) {
            int tentativas = tentativasPorEmail.getOrDefault(emailLimpo, 0) + 1;
            if (tentativas >= 3) {
                tentativasPorEmail.put(emailLimpo, 0);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Três tentativas falhadas.");
            }
            tentativasPorEmail.put(emailLimpo, tentativas);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        if (u.getTipo() == TipoUtilizador.BANIDO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado. Esta conta encontra-se banida.");
        }

        tentativasPorEmail.remove(emailLimpo);
        return u;
    }

    //REVER METODO
    public Utilizador atualizarDadosPessoais(Integer id, UtilizadorAtualizacaoDTO dados) {
        Utilizador existente = getUtilizador(id);

        String novoNome = dados.nome() != null ? dados.nome().trim() : null;
        String novoEmail = dados.email() != null ? dados.email().trim() : null;
        String novaPassword = dados.password();

        // 1. Validar tudo primeiro
        if (novoEmail != null && !novoEmail.isEmpty()) {
            if (!emailValido(novoEmail)) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de email inválido.");
            }
            Utilizador outro = utilizadorRepository.findByEmail(novoEmail).orElse(null);
            if (outro != null && outro.getIdUtilizador() != id.intValue()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "O novo email já existe no sistema.");
            }
        }

        if (novaPassword != null && !novaPassword.isBlank() && !passwordValida(novaPassword)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A nova password deve conter uma maiúscula e um carácter especial.");
        }

        // 2. Aplicar alterações
        if (novoNome != null && !novoNome.isEmpty()) {
            existente.setNome(novoNome);
        }
        if (novoEmail != null && !novoEmail.isEmpty()) {
            existente.setEmail(novoEmail);
        }
        if (novaPassword != null && !novaPassword.isBlank()) {
            existente.setPassword(gerarHash(novaPassword));
        }

        return utilizadorRepository.save(existente);
    }

    public void deleteUtilizador(Integer id) {
        Utilizador existente = getUtilizador(id);
        try {
            utilizadorRepository.delete(existente);
            utilizadorRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Não é possível apagar um utilizador com anúncios ou chats associados.");
        }
    }

    private boolean emailValido(String email) {
        return email != null && email.length() <= 100 && validadorEmail.isValid(email);
    }

    private boolean passwordValida(String password) {
        return validadorPassword.validate(new PasswordData(password)).isValid();
    }

    private String gerarHash(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }
    public Utilizador getAtivo(Integer id) {
        Utilizador u = getUtilizador(id);
        if (u.getTipo() == TipoUtilizador.BANIDO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Utilizador banido.");
        }
        return u;
    }
    public Utilizador alterarTipo(Integer idGestor, Integer idAlvo, TipoUtilizador novoTipo) {
        Utilizador gestor = getUtilizador(idGestor);
        if (gestor.getTipo() != TipoUtilizador.GESTOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Apenas gestores podem alterar o tipo.");
        }
        Utilizador alvo = getUtilizador(idAlvo);
        alvo.setTipo(novoTipo);
        return utilizadorRepository.save(alvo);
    }
}