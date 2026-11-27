package com.upt.lp.objetosperdidos.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chats")
public class ChatController {
	
	private final ChatService chatService;

	
    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    
    @GetMapping
    public List<Chat> getAll() {
        return chatService.getAllChats();
    }

    
    @GetMapping("/{id}")
    public Chat getById(@PathVariable Integer id) {
        return chatService.getChat(id);
    }

    
    @PostMapping("/anuncios/{anuncioId}/utilizadores/{utilizadorId}")
    public ResponseEntity<Chat> reivindicar(@PathVariable Integer anuncioId, @PathVariable Integer utilizadorId) {
        Chat created = chatService.reivindicar(anuncioId, utilizadorId);
        URI location = URI.create("/api/chats/" + created.getIdChat());
        return ResponseEntity.created(location).body(created);
    }

    
    @GetMapping("/utilizador/{utilizadorId}")
    public List<Chat> getChatsDoUtilizador(@PathVariable Integer utilizadorId) {
        return chatService.listarChatsDoUtilizador(utilizadorId);
    }

    
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Integer id) {
        chatService.deleteChat(id);
    }
}
