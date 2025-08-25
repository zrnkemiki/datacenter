package com.zmiki.datacenterapp.rack;

import com.zmiki.datacenterapp.rack.dto.RackCreateDto;
import com.zmiki.datacenterapp.rack.dto.RackDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/racks")
@RequiredArgsConstructor
public class RackController {

    private final RackService rackService;

    @PostMapping
    public ResponseEntity<RackDto> createRack(@Valid @RequestBody RackCreateDto rackCreateDto) {
        RackDto createdRack = rackService.createRack(rackCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRack);
    }

    @PutMapping("/{serialNumber}")
    public ResponseEntity<RackDto> updateRack(
            @PathVariable String serialNumber,
            @Valid @RequestBody RackDto rackDto) {
        RackDto updatedRack = rackService.updateRack(serialNumber, rackDto);
        return ResponseEntity.ok(updatedRack);
    }

    @DeleteMapping("/{serialNumber}")
    public ResponseEntity<Void> deleteRack(@PathVariable String serialNumber) {
        rackService.deleteRack(serialNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<RackDto> getRack(
            @PathVariable String serialNumber) {
        RackDto rack = rackService.getRack(serialNumber);
        return ResponseEntity.ok(rack);
    }

    @GetMapping
    public ResponseEntity<List<RackDto>> getAllRacks() {
        List<RackDto> racks = rackService.getAllRacks();
        return ResponseEntity.ok(racks);
    }
}
