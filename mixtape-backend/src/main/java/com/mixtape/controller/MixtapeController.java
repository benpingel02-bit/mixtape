package com.mixtape.controller;

import com.mixtape.dto.MixtapeRequest;
import com.mixtape.dto.MixtapeResponse;
import com.mixtape.service.MixtapeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/mixtapes")
public class MixtapeController {

    private final MixtapeService mixtapeService;

    public MixtapeController(MixtapeService mixtapeService) {
        this.mixtapeService = mixtapeService;
    }

    @GetMapping
    public List<MixtapeResponse> getAllPublic() {
        return mixtapeService.findAllPublic();
    }

    @GetMapping("/{id}")
    public ResponseEntity<MixtapeResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(mixtapeService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public List<MixtapeResponse> getByUser(@PathVariable Long userId) {
        return mixtapeService.findByUser(userId);
    }

    @PostMapping
    public ResponseEntity<MixtapeResponse> create(@Valid @RequestBody MixtapeRequest request) {
        MixtapeResponse response = mixtapeService.create(request);
        return ResponseEntity
            .created(URI.create("/api/mixtapes/" + response.id()))
            .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<MixtapeResponse> update(
        @PathVariable Long id,
        @Valid @RequestBody MixtapeRequest request) {
        return ResponseEntity.ok(mixtapeService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mixtapeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/lock")
    public ResponseEntity<MixtapeResponse> lock(@PathVariable Long id) {
        return ResponseEntity.ok(mixtapeService.lock(id));
    }
}