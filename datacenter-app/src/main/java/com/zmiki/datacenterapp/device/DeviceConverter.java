package com.zmiki.datacenterapp.device;

import com.zmiki.datacenterapp.device.dto.DeviceCreateDto;
import com.zmiki.datacenterapp.device.dto.DeviceDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class DeviceConverter {

    public Device fromCreateDto(DeviceCreateDto dto) {
        if (dto == null) {
            return null;
        }

        return Device.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .serialNumber(dto.getSerialNumber())
                .units(dto.getUnit())
                .power(dto.getPower())
                .build();
    }

    public Device toEntity(DeviceDto dto) {
        if (dto == null) {
            return null;
        }

        Device device = Device.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .serialNumber(dto.getSerialNumber())
                .units(dto.getUnits())
                .power(dto.getPower())
                .build();

        if (dto.getId() != null) {
            device.setId(dto.getId());
        }

        return device;
    }

    public DeviceDto toDto(Device device) {
        if (device == null) {
            return null;
        }

        return DeviceDto.builder()
                .id(device.getId())
                .name(device.getName())
                .description(device.getDescription())
                .serialNumber(device.getSerialNumber())
                .units(device.getUnits())
                .power(device.getPower())
                .build();
    }
}
