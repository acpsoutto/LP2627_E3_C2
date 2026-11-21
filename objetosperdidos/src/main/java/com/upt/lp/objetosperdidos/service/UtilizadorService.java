package com.upt.lp.objetosperdidos.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.validator.routines.EmailValidator;
import org.mindrot.jbcrypt.BCrypt;
import org.passay.*;
// ALTERADO: "import org.springframework.*;" não existe. Os imports certos são estes dois:
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.stereotype.Service;

// NOVO: imports dos DTOs usados nos métodos
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
        if (utilizadorRepository.findByEmail(GESTOR_EMAIL).isEmpty()) {
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

    // ALTERADO: recebe o UtilizadorRegistoDTO (nome, email, password) em vez da entidade.
    // O cliente deixa de poder enviar campos como "tipo" ou "id".
    public Utilizador registrar(UtilizadorRegistoDTO dados) {
        // ALTERADO: dados.getNome() passa a dados.nome() (records usam nome(), não getNome())
        if (dados.nome() == null || dados.nome().isBlank()
                || dados.email() == null || dados.email().isBlank()
                || dados.password() == null || dados.password().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Nome, email e password são obrigatórios.");
        }
        if (!emailValido(dados.email())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de email inválido.");
        }
        if (!passwordValida(dados.password())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A password deve ter pelo menos uma maiúscula e um carácter especial.");
        }
        if (utilizadorRepository.findByEmail(dados.email()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email já registado.");
        }

        Utilizador novo = new Utilizador(
                dados.nome().trim(),
                dados.email().trim(),
                gerarHash(dados.password()),
                TipoUtilizador.NORMAL   // o tipo é sempre definido aqui, nunca pelo cliente
        );
        return utilizadorRepository.save(novo);
    }

    // Sem alterações na lógica. Nota: a API não tem sessão (é stateless), por isso
    // o login só valida credenciais. Se uma user story exigir "estar autenticado",
    // será preciso um token (fora do âmbito do tutorial).
    public Utilizador autenticar(String email, String password) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Email e password são obrigatórios.");
        }

        Utilizador u = utilizadorRepository.findByEmail(email).orElse(null);
        if (u == null || !BCrypt.checkpw(password, u.getPassword())) {
            int tentativas = tentativasPorEmail.getOrDefault(email, 0) + 1;
            if (tentativas >= 3) {
                tentativasPorEmail.put(email, 0);
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Três tentativas falhadas.");
            }
            tentativasPorEmail.put(email, tentativas);
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas.");
        }

        if (u.getTipo() == TipoUtilizador.BANIDO) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Acesso negado. Esta conta encontra-se banida.");
        }

        tentativasPorEmail.remove(email);
        return u;
    }

    // ALTERADO: recebe UtilizadorAtualizacaoDTO em vez da entidade (mesmo motivo do registo).
    public Utilizador atualizarDadosPessoais(Integer id, UtilizadorAtualizacaoDTO dados) {
        Utilizador existente = getUtilizador(id);

        if (dados.nome() != null && !dados.nome().isBlank()) {
            existente.setNome(dados.nome().trim());
        }

        if (dados.email() != null && !dados.email().isBlank()) {
            if (!emailValido(dados.email())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Formato de email inválido.");
            }
            Utilizador outro = utilizadorRepository.findByEmail(dados.email().trim()).orElse(null);
            if (outro != null && outro.getIdUtilizador() != id) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "O novo email já existe no sistema.");
            }
            existente.setEmail(dados.email().trim());
        }

        if (dados.password() != null && !dados.password().isBlank()) {
            if (!passwordValida(dados.password())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "A nova password deve conter uma maiúscula e um carácter especial.");
            }
            existente.setPassword(gerarHash(dados.password()));
        }

        return utilizadorRepository.save(existente);
    }

    // ATENÇÃO: se o utilizador tiver anúncios, chats ou mensagens, o delete falha por
    // chave estrangeira e devolve 500. Na Componente 1 os anúncios eram "apagados" por
    // estado (APAGADO_*). Decidam em equipa: bloquear com 409, ou mudar o tipo para BANIDO.
    public void deleteUtilizador(Integer id) {
        Utilizador existente = getUtilizador(id);
        utilizadorRepository.delete(existente);
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
}