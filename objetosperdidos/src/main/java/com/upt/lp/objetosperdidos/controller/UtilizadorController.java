package com.upt.lp.objetosperdidos.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.upt.lp.objetosperdidos.dto.UtilizadorAtualizacaoDTO;
import com.upt.lp.objetosperdidos.dto.UtilizadorDTO;
import com.upt.lp.objetosperdidos.dto.UtilizadorLoginDTO;
import com.upt.lp.objetosperdidos.dto.UtilizadorRegistoDTO;
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
    public ResponseEntity<UtilizadorDTO> registrar(@RequestBody UtilizadorRegistoDTO dados) {
        Utilizador criado = utilizadorService.registrar(dados);
        URI location = URI.create("/api/utilizadores/" + criado.getIdUtilizador());
        return ResponseEntity.created(location).body(UtilizadorMapper.toDTO(criado));
    }

    @PostMapping("/login")
    public UtilizadorDTO login(@RequestBody UtilizadorLoginDTO credenciais) {
        return UtilizadorMapper.toDTO(
                utilizadorService.autenticar(credenciais.email(), credenciais.password()));
    }

    @PutMapping("/{id}")
    public UtilizadorDTO atualizar(@PathVariable Integer id,
                                   @RequestBody UtilizadorAtualizacaoDTO dados) {
        return UtilizadorMapper.toDTO(utilizadorService.atualizarDadosPessoais(id, dados));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        utilizadorService.deleteUtilizador(id);
    }
}