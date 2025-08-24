package com.zmiki.datacenterapp.rack;

import com.zmiki.datacenterapp.rack.dto.RackCreateDto;
import com.zmiki.datacenterapp.rack.dto.RackDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RackService {

    private final RackRepository rackRepository;
    private final RackConverter rackConverter;

    public RackDto createRack(RackCreateDto rackCreateDto) {
        if (rackRepository.findBySerialNumber(rackCreateDto.getSerialNumber()).isPresent()) {
            throw new IllegalArgumentException("Rack with serial number " + rackCreateDto.getSerialNumber() + " already exists");
        }
        
        
        Rack rack = rackConverter.fromCreateDto(rackCreateDto);
        Rack savedRack = rackRepository.save(rack);
        return rackConverter.toDto(savedRack);
    }

    public RackDto updateRack(String serialNumber, RackDto rackDto) {
        if (!serialNumber.equals(rackDto.getSerialNumber())) {
            throw new IllegalArgumentException("Serial number in path must match serial number in request body");
        }
        
        Rack existingRack = rackRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new EntityNotFoundException("Rack not found"));
        
        existingRack.setName(rackDto.getName());
        existingRack.setDescription(rackDto.getDescription());
        existingRack.setSerialNumber(rackDto.getSerialNumber());
        existingRack.setMaxUnits(rackDto.getUnit());
        existingRack.setMaxPower(rackDto.getMaxPower());
        
        Rack savedRack = rackRepository.save(existingRack);
        return rackConverter.toDto(savedRack);
    }

    public void deleteRack(String serialNumber) {
        Rack rack = rackRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new EntityNotFoundException("Rack not found"));

        if (!rack.getDevices().isEmpty()) {
            throw new RuntimeException("Cannot delete rack with devices");
        }

        rackRepository.delete(rack);
    }

    public RackDto getRack(String serialNumber) {
        Rack rack = rackRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new EntityNotFoundException("Rack not found"));
        return rackConverter.toDto(rack);
    }

    public List<RackDto> getAllRacks() {
        return rackRepository.findAll().stream()
                .map(rackConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<Rack> getRacksBySerialNumbers(List<String> serialNumbers) {
        List<Rack> racks = new ArrayList<>();
        for (String serialNumber : serialNumbers) {
            Rack rack = rackRepository.findBySerialNumber(serialNumber)
                    .orElseThrow(() -> new EntityNotFoundException("Rack not found: " + serialNumber));
            racks.add(rack);
        }
        return racks;
    }
}
