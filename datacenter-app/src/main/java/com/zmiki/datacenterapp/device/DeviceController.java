package com.zmiki.datacenterapp.device;

import com.zmiki.datacenterapp.device.dto.DeviceCreateDto;
import com.zmiki.datacenterapp.device.dto.DeviceDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService deviceService;

    @PostMapping
    public DeviceDto createDevice(@Valid @RequestBody DeviceCreateDto deviceCreateDto) {
        return deviceService.createDevice(deviceCreateDto);
    }

    @PutMapping("/{serialNumber}")
    public DeviceDto updateDevice(@PathVariable String serialNumber, @Valid @RequestBody DeviceDto deviceDto) {
        return deviceService.updateDevice(serialNumber, deviceDto);
    }

    @DeleteMapping("/{serialNumber}")
    public void deleteDevice(@PathVariable String serialNumber) {
        deviceService.deleteDevice(serialNumber);
    }

    @GetMapping("/{serialNumber}")
    public DeviceDto getDevice(@PathVariable String serialNumber) {
        return deviceService.getDevice(serialNumber);
    }

    @GetMapping
    public List<DeviceDto> getAllDevices() {
        return deviceService.getAllDevices();
    }
}
