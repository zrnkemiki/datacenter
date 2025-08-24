package com.zmiki.datacenterapp.device;


import com.zmiki.datacenterapp.device.dto.DeviceCreateDto;
import com.zmiki.datacenterapp.device.dto.DeviceDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final DeviceConverter deviceConverter;

    @Transactional
    public DeviceDto createDevice(DeviceCreateDto deviceCreateDto) {
        if (deviceRepository.existsBySerialNumber(deviceCreateDto.getSerialNumber())) {
            throw new IllegalArgumentException("Device with serial number " + deviceCreateDto.getSerialNumber() + " already exists");
        }
        
        Device device = deviceConverter.fromCreateDto(deviceCreateDto);
        Device savedDevice = deviceRepository.save(device);
        return deviceConverter.toDto(savedDevice);
    }

    @Transactional
    public DeviceDto updateDevice(String serialNumber, DeviceDto deviceDto) {
        if (!serialNumber.equals(deviceDto.getSerialNumber())) {
            throw new IllegalArgumentException("Serial number in database must match serial number in request");
        }
        
        Device existingDevice = deviceRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        
        existingDevice.setName(deviceDto.getName());
        existingDevice.setDescription(deviceDto.getDescription());

        existingDevice.setUnits(deviceDto.getUnits());
        existingDevice.setPower(deviceDto.getPower());
        
        Device savedDevice = deviceRepository.save(existingDevice);
        return deviceConverter.toDto(savedDevice);
    }


    public void deleteDevice(String serialNumber) {
        Device device = deviceRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));

        deviceRepository.delete(device);
    }

    public DeviceDto getDevice(String serialNumber) {
        Device device = deviceRepository.findBySerialNumber(serialNumber)
                .orElseThrow(() -> new EntityNotFoundException("Device not found"));
        return deviceConverter.toDto(device);
    }

    public List<DeviceDto> getAllDevices() {
        return deviceRepository.findAll().stream()
                .map(deviceConverter::toDto)
                .collect(Collectors.toList());
    }

    public List<Device> getDevicesBySerialNumbers(List<String> serialNumbers) {
        List<Device> devices = new ArrayList<>();
        for (String serialNumber : serialNumbers) {
            Device device = deviceRepository.findBySerialNumber(serialNumber)
                    .orElseThrow(() -> new EntityNotFoundException("Device not found: " + serialNumber));
            devices.add(device);
        }
        return devices;
    }
}
