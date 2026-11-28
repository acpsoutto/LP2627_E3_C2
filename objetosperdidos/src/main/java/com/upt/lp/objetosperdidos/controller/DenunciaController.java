package com.upt.lp.objetosperdidos.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.upt.lp.objetosperdidos.entity.Chat;
import com.upt.lp.objetosperdidos.entity.Denuncia;
import com.upt.lp.objetosperdidos.entity.Utilizador;
import com.upt.lp.objetosperdidos.service.DenunciaService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@RestController
@RequestMapping("/api/denuncias")
public class DenunciaController {

    public record CriarDenunciaRequest(
            @NotNull Integer idDenunciante,
            @NotNull Integer idDenunciado,
            @NotNull Integer idChat
    ) {}

    private final DenunciaService denunciaService;

    public DenunciaController(DenunciaService denunciaService) {
        this.denunciaService = denunciaService;
    }

    @PostMapping
    public ResponseEntity<Denuncia> criarDenuncia(@Valid @RequestBody CriarDenunciaRequest request) {
        Denuncia criada = denunciaService.criarDenuncia(
                request.idDenunciante(),
                request.idDenunciado(),
                request.idChat()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(criada);
    }

    @GetMapping
    public ResponseEntity<List<Denuncia>> listarDenuncias() {
        return ResponseEntity.ok(denunciaService.listarTodas());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<Denuncia>> listarPendentes() {
        return ResponseEntity.ok(denunciaService.listarPendentes());
    }

    @GetMapping("/chats-pendentes")
    public ResponseEntity<List<Chat>> listarChatsDenunciadosPendentes() {
        return ResponseEntity.ok(denunciaService.listarChatsDenunciadosPendentes());
    }

    @PatchMapping("/{idDenuncia}/resolver")
    public ResponseEntity<Denuncia> resolverDenuncia(
            @PathVariable int idDenuncia,
            @RequestParam int opcao) {
        Denuncia resolvida = denunciaService.resolverDenuncia(idDenuncia, opcao);
        return ResponseEntity.ok(resolvida);
    }

    @PatchMapping("/utilizadores/{idUtilizador}/banir")
    public ResponseEntity<Utilizador> banirUtilizadorDiretamente(@PathVariable int idUtilizador) {
        Utilizador banido = denunciaService.banirUtilizadorDiretamente(idUtilizador);
        return ResponseEntity.ok(banido);
    }
}