package com.zmiki.datacenterapp.rack;

import com.zmiki.datacenterapp.device.DeviceConverter;
import com.zmiki.datacenterapp.rack.dto.RackCreateDto;
import com.zmiki.datacenterapp.rack.dto.RackDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RackConverter {

    private final DeviceConverter deviceConverter;

    public Rack fromCreateDto(RackCreateDto dto) {
        if (dto == null) {
            return null;
        }
        return Rack.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .serialNumber(dto.getSerialNumber())
                .maxUnits(dto.getUnit())
                .maxPower(dto.getMaxPower())
                .build();
    }

    public Rack toEntity(RackDto dto) {
        if (dto == null) {
            return null;
        }
        Rack rack = Rack.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .serialNumber(dto.getSerialNumber())
                .maxUnits(dto.getUnit())
                .maxPower(dto.getMaxPower())
                .build();

        if (dto.getId() != null) {
            rack.setId(dto.getId());
        }

        return rack;
    }

    public RackDto toDto(Rack rack) {
        if (rack == null) {
            return null;
        }
        return RackDto.builder()
                .id(rack.getId())
                .name(rack.getName())
                .description(rack.getDescription())
                .serialNumber(rack.getSerialNumber())
                .unit(rack.getMaxUnits())
                .maxPower(rack.getMaxPower())
                .devices(rack.getDevices() != null ? rack.getDevices().stream()
                        .map(deviceConverter::toDto)
                        .collect(Collectors.toList()) : null)
                .build();
    }
}
