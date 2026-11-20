package com.upt.lp.objetosperdidos.controller;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import com.upt.lp.objetosperdidos.entity.Mensagem; 
import com.upt.lp.objetosperdidos.service.MensagemService; 
import org.springframework.web.bind.annotation.*; 
import java.util.List; 
 
@RestController 
@RequestMapping("/api/courses") 
public class MensagemController { 
 
    private final MensagemService mensagemService; 
 
    public MensagemController(MensagemService mensagemService) { 
        this.mensagemService = mensagemService; 
    } 
 
    @GetMapping 
    public List<Mensagem> getAll() { 
        return mensagemService.getAllMensagens(); 
    } 
 
    @GetMapping("/{id}") 
    public Mensagem getById(@PathVariable Long id) { 
        return mensagemService.getMensagem(id); 
    } 
 
    @PostMapping
	 public ResponseEntity<Mensagem> create(@RequestBody Mensagem m) {
		Mensagem created = mensagemService.createMensagem(m);
		URI location = URI.create("/api/departments/" + created.getIdMensagem());
		return ResponseEntity.created(location).body(created);
	 }
 
    @PutMapping("/{id}") 
    public Mensagem update(@PathVariable Long id, @RequestBody Mensagem m) { 
        return mensagemService.updateMensagem(id, m); 
    } 
 
    @DeleteMapping("/{id}") 
    public void delete(@PathVariable Long id) { 
        mensagemService.deleteMensagem(id); 
    } 
} 
 