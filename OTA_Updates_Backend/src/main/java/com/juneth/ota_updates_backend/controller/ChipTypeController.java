package com.juneth.ota_updates_backend.controller;

import com.juneth.ota_updates_backend.dto.request.CreateChipTypeRequest;
import com.juneth.ota_updates_backend.dto.request.UpdateChipTypeRequest;
import com.juneth.ota_updates_backend.dto.response.ChipTypeResponse;
import com.juneth.ota_updates_backend.service.ChipTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/chip-types")
@RequiredArgsConstructor
public class ChipTypeController {

    private final ChipTypeService chipTypeService;

    @PostMapping
    public ResponseEntity<ChipTypeResponse> createChipType(@Valid @RequestBody CreateChipTypeRequest request) {
        ChipTypeResponse response = chipTypeService.createChipType(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ChipTypeResponse>> getAllChipTypes() {
        return ResponseEntity.ok(chipTypeService.getAllChipTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChipTypeResponse> getChipTypeById(@PathVariable UUID id) {
        return ResponseEntity.ok(chipTypeService.getChipTypeById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChipTypeResponse> updateChipType(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateChipTypeRequest request) {
        return ResponseEntity.ok(chipTypeService.updateChipType(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChipType(@PathVariable UUID id) {
        chipTypeService.deleteChipType(id);
        return ResponseEntity.noContent().build();
    }
}
