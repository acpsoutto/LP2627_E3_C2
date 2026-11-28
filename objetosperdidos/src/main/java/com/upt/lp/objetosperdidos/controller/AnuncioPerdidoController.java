package com.upt.lp.objetosperdidos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.upt.lp.objetosperdidos.dto.AnuncioPerdidoDTO.Request;
import com.upt.lp.objetosperdidos.dto.AnuncioPerdidoDTO.Response;
import com.upt.lp.objetosperdidos.entity.Categoria;
import com.upt.lp.objetosperdidos.service.AnuncioPerdidoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/anuncios-perdidos")
public class AnuncioPerdidoController {

	private final AnuncioPerdidoService service;

	public AnuncioPerdidoController(AnuncioPerdidoService service) {
		this.service = service;
	}

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

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void apagar(@PathVariable int id, @RequestParam int utilizadorId) {
		service.apagar(id, utilizadorId);
	}
}
