package com.mixtape.controller;

import com.mixtape.dto.TrackRequest;
import com.mixtape.dto.TrackResponse;
import com.mixtape.service.TrackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/mixtapes/{mixtapeId}/tracks")
public class TrackController {

    private final TrackService trackService;

    public TrackController(TrackService trackService) {
        this.trackService = trackService;
    }

    @GetMapping
    public List<TrackResponse> getByMixtape(@PathVariable Long mixtapeId) {
        return trackService.findByMixtape(mixtapeId);
    }

    @PostMapping
    public ResponseEntity<TrackResponse> addTrack(
            @PathVariable Long mixtapeId,
            @Valid @RequestBody TrackRequest request) {
        TrackResponse response = trackService.addTrack(request);
        return ResponseEntity
                .created(URI.create("/api/mixtapes/" + mixtapeId + "/tracks/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/{trackId}")
    public ResponseEntity<Void> deleteTrack(
            @PathVariable Long mixtapeId,
            @PathVariable Long trackId) {
        trackService.deleteTrack(trackId);
        return ResponseEntity.noContent().build();
    }
}