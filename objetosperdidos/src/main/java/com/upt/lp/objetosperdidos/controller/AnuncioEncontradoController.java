package com.upt.lp.objetosperdidos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.upt.lp.objetosperdidos.dto.AnuncioEncontradoDTO.Request;
import com.upt.lp.objetosperdidos.dto.AnuncioEncontradoDTO.Response;
import com.upt.lp.objetosperdidos.entity.Categoria;
import com.upt.lp.objetosperdidos.service.AnuncioEncontradoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/anuncios-encontrados")
public class AnuncioEncontradoController {

	private final AnuncioEncontradoService service;

	public AnuncioEncontradoController(AnuncioEncontradoService service) {
		this.service = service;
	}

	// GET /api/anuncios-encontrados?categoria=LIVROS&cor=azul&titulo=cha
	@GetMapping
	public List<Response> listar(@RequestParam(required = false) Categoria categoria,
			@RequestParam(required = false) String cor,
			@RequestParam(required = false) String titulo) {
		return service.listar(categoria, cor, titulo);
	}

	@GetMapping("/{id}")
	public Response obter(@PathVariable int id) {
		return service.obter(id);
	}

	@PostMapping
	public ResponseEntity<Response> criar(@Valid @RequestBody Request r) {
		Response criado = service.criar(r);
		URI location = ServletUriComponentsBuilder.fromCurrentRequest()
				.path("/{id}").buildAndExpand(criado.id()).toUri();
		return ResponseEntity.created(location).body(criado);
	}

	@PutMapping("/{id}")
	public Response atualizar(@PathVariable int id, @Valid @RequestBody Request r) {
		return service.atualizar(id, r);
	}

	// DELETE /api/anuncios-encontrados/5?utilizadorId=1
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void apagar(@PathVariable int id, @RequestParam int utilizadorId) {
		service.apagar(id, utilizadorId);
	}
}
