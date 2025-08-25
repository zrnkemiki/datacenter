package com.zmiki.datacenterapp.device;

import com.zmiki.datacenterapp.device.dto.DeviceCreateDto;
import com.zmiki.datacenterapp.device.dto.DeviceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public ResponseEntity<DeviceDto> createDevice(@Valid @RequestBody DeviceCreateDto deviceCreateDto) {
        DeviceDto createdDevice = deviceService.createDevice(deviceCreateDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDevice);
    }

    @PutMapping("/{serialNumber}")
    public ResponseEntity<DeviceDto> updateDevice(@PathVariable String serialNumber, @Valid @RequestBody DeviceDto deviceDto) {
        DeviceDto updatedDevice = deviceService.updateDevice(serialNumber, deviceDto);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{serialNumber}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String serialNumber) {
        deviceService.deleteDevice(serialNumber);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{serialNumber}")
    public ResponseEntity<DeviceDto> getDevice(@PathVariable String serialNumber) {
        DeviceDto device = deviceService.getDevice(serialNumber);
        return ResponseEntity.ok(device);
    }

    @GetMapping
    public ResponseEntity<List<DeviceDto>> getAllDevices() {
        List<DeviceDto> devices = deviceService.getAllDevices();
        return ResponseEntity.ok(devices);
    }
}
