package com.upt.lp.objetosperdidos.service;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import com.upt.lp.objetosperdidos.dto.AnuncioEncontradoDTO.Request;
import com.upt.lp.objetosperdidos.dto.AnuncioEncontradoDTO.Response;
import com.upt.lp.objetosperdidos.entity.AnuncioEncontrado;
import com.upt.lp.objetosperdidos.entity.Categoria;
import com.upt.lp.objetosperdidos.entity.Estado;
import com.upt.lp.objetosperdidos.entity.Utilizador;
import com.upt.lp.objetosperdidos.repository.AnuncioEncontradoRepository;
import com.upt.lp.objetosperdidos.repository.UtilizadorRepository;

@Service
public class AnuncioEncontradoService {

	private final AnuncioEncontradoRepository repo;
	private final UtilizadorRepository utilizadorRepo;

	public AnuncioEncontradoService(AnuncioEncontradoRepository repo, UtilizadorRepository utilizadorRepo) {
		this.repo = repo;
		this.utilizadorRepo = utilizadorRepo;
	}

	@Transactional(readOnly = true)
	public List<Response> listar(Categoria categoria, String cor, String titulo) {
		String t = (titulo == null || titulo.isBlank()) ? null : titulo.trim();
		String c = (cor == null || cor.isBlank()) ? null : cor.trim();
		if (t != null && t.length() < 3) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"A pesquisa por título exige pelo menos 3 letras");
		}
		return repo.filtrar(Estado.PENDENTE, categoria, c, t).stream().map(this::toResponse).toList();
	}

	@Transactional(readOnly = true)
	public Response obter(int id) {
		return toResponse(buscar(id));
	}

	@Transactional
	public Response criar(Request r) {
		Utilizador u = utilizadorRepo.findById(r.utilizadorId()).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Utilizador não existe"));
		// O construtor define estado PENDENTE e dataPublicacao = hoje
		AnuncioEncontrado a = new AnuncioEncontrado(u, r.titulo().trim(), r.categoria(),
				r.descricao().trim(), r.cor().trim(), r.localizacao().trim());
		return toResponse(repo.save(a));
	}

	@Transactional
	public Response atualizar(int id, Request r) {
		AnuncioEncontrado a = buscar(id);
		verificarPublicador(a, r.utilizadorId());
		a.setTitulo(r.titulo().trim());
		a.setCategoria(r.categoria());
		a.setCor(r.cor().trim());
		a.setDescricao(r.descricao().trim());
		a.setLocalizacao(r.localizacao().trim());
		return toResponse(repo.save(a));
	}

	/** Apagar = eliminação lógica (usa os estados APAGADO_* do enum). */
	@Transactional
	public void apagar(int id, int utilizadorId) {
		AnuncioEncontrado a = buscar(id);
		verificarPublicador(a, utilizadorId);
		a.setEstado(a.getEstado() == Estado.RESOLVIDO ? Estado.APAGADO_RESOLVIDO : Estado.APAGADO_PENDENTE);
		repo.save(a);
	}

	// ---------- auxiliares ----------

	private AnuncioEncontrado buscar(int id) {
		AnuncioEncontrado a = repo.findById(id).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Anúncio não encontrado"));
		if (a.getEstado() == Estado.APAGADO_PENDENTE || a.getEstado() == Estado.APAGADO_RESOLVIDO) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Anúncio não encontrado");
		}
		return a;
	}

	private void verificarPublicador(AnuncioEncontrado a, Integer utilizadorId) {
		if (utilizadorId == null || a.getUtilizador().getIdUtilizador() != utilizadorId) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Só o publicador pode alterar o anúncio");
		}
	}

	private Response toResponse(AnuncioEncontrado a) {
		return new Response(a.getIdAnuncio(), a.getTitulo(), a.getCategoria(), a.getCor(), a.getDescricao(),
				a.getLocalizacao(), a.getDataPublicacao(), a.getEstado(),
				a.getUtilizador().getIdUtilizador(), a.getUtilizador().getNome());
	}
}
