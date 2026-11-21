package com.upt.lp.objetosperdidos.controller;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.*;
import com.upt.lp.objetosperdidos.dto.UtilizadorDTO;
import com.upt.lp.objetosperdidos.entity.Utilizador;
import com.upt.lp.objetosperdidos.mapper.UtilizadorMapper;
import com.upt.lp.objetosperdidos.service.UtilizadorService;

@RestController
@RequestMapping("/api/utilizadores")
public class UtilizadorController {

    private final UtilizadorService utilizadorService;

    public UtilizadorController(UtilizadorService utilizadorService) {
        this.utilizadorService = utilizadorService;
    }

    @GetMapping
    public List<UtilizadorDTO> getAll() {
        return utilizadorService.getAllUtilizadores()
                .stream()
                .map(UtilizadorMapper::toDTO)
                .toList();
    }
    
    @GetMapping("/{id}")
    public UtilizadorDTO getById(@PathVariable Integer id) {
        return UtilizadorMapper.toDTO(utilizadorService.getUtilizador(id));
    }
    
    @PostMapping
    public ResponseEntity<Utilizador> registrar(@RequestBody Utilizador utilizador) {
        Utilizador created = utilizadorService.registrar(utilizador);
        URI location = URI.create("/api/utilizadores/" + created.getIdUtilizador());
        return ResponseEntity.created(location).body(created);
    }

    @PostMapping("/login")
    public Utilizador login(@RequestBody Map<String, String> credenciais) {
        return utilizadorService.autenticar(credenciais.get("email"), credenciais.get("password"));
    }

    @PutMapping("/{id}")
    public Utilizador atualizar(@PathVariable Integer id, @RequestBody Utilizador dados) {
        return utilizadorService.atualizarDadosPessoais(id, dados);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        utilizadorService.deleteUtilizador(id);
    }
}