package com.zmiki.datacenterapp.rack;

import com.zmiki.datacenterapp.rack.dto.RackCreateDto;
import com.zmiki.datacenterapp.rack.dto.RackDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/racks")
@RequiredArgsConstructor
public class RackController {

    private final RackService rackService;

    @PostMapping
    public RackDto createRack(@Valid @RequestBody RackCreateDto rackCreateDto) {
        return rackService.createRack(rackCreateDto);
    }

    @PutMapping("/{serialNumber}")
    public RackDto updateRack(
            @PathVariable String serialNumber,
            @Valid @RequestBody RackDto rackDto) {
        return rackService.updateRack(serialNumber, rackDto);
    }

    @DeleteMapping("/{serialNumber}")
    public void deleteRack(@PathVariable String serialNumber) {
        rackService.deleteRack(serialNumber);
    }

    @GetMapping("/{serialNumber}")
    public RackDto getRack(
            @PathVariable String serialNumber) {
        return rackService.getRack(serialNumber);
    }

    @GetMapping
    public List<RackDto> getAllRacks() {
        return rackService.getAllRacks();
    }
}
