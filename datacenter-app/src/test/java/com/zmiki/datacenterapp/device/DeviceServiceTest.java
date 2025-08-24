package com.zmiki.datacenterapp.device;

import com.zmiki.datacenterapp.device.dto.DeviceCreateDto;
import com.zmiki.datacenterapp.device.dto.DeviceDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceServiceTest {

    private static final String TEST_DEVICE_NAME = "Test Device";
    private static final String TEST_DEVICE_DESCRIPTION = "Test Description";
    private static final String TEST_SERIAL_NUMBER = "DEV-001";
    private static final String TEST_DEVICE_2_NAME = "Test Device 2";
    private static final String TEST_DEVICE_2_SERIAL = "DEV-002";
    private static final String UPDATED_DEVICE_NAME = "Updated Device";
    private static final String UPDATED_DEVICE_DESCRIPTION = "Updated Description";
    private static final String DIFFERENT_SERIAL = "DIFFERENT-SERIAL";
    private static final String DEVICE_NOT_FOUND_MESSAGE = "Device not found";
    private static final String SERIAL_NUMBER_MISMATCH_MESSAGE = "Serial number in database must match serial number in request";
    private static final String DUPLICATE_SERIAL_MESSAGE = "Device with serial number " + TEST_SERIAL_NUMBER + " already exists";

    private static final int TEST_DEVICE_SLOTS = 2;
    private static final int TEST_DEVICE_POWER = 100;
    private static final int TEST_DEVICE_2_SLOTS = 1;
    private static final int TEST_DEVICE_2_POWER = 50;
    private static final int UPDATED_DEVICE_SLOTS = 4;
    private static final int UPDATED_DEVICE_POWER = 200;
    private static final int EXPECTED_DEVICE_COUNT = 2;

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceConverter deviceConverter;

    @InjectMocks
    private DeviceService deviceService;

    private Device device;
    private DeviceCreateDto deviceCreateDto;
    private DeviceDto deviceDto;
    private UUID deviceId;
    private String serialNumber;

    @BeforeEach
    void setUp() {
        deviceId = UUID.randomUUID();
        serialNumber = TEST_SERIAL_NUMBER;

        device = Device.builder()
                .id(deviceId)
                .name(TEST_DEVICE_NAME)
                .description(TEST_DEVICE_DESCRIPTION)
                .serialNumber(serialNumber)
                .units(TEST_DEVICE_SLOTS)
                .power(TEST_DEVICE_POWER)
                .build();

        deviceCreateDto = DeviceCreateDto.builder()
                .name(TEST_DEVICE_NAME)
                .description(TEST_DEVICE_DESCRIPTION)
                .serialNumber(serialNumber)
                .unit(TEST_DEVICE_SLOTS)
                .power(TEST_DEVICE_POWER)
                .build();

        deviceDto = DeviceDto.builder()
                .id(deviceId)
                .name(TEST_DEVICE_NAME)
                .description(TEST_DEVICE_DESCRIPTION)
                .serialNumber(serialNumber)
                .units(TEST_DEVICE_SLOTS)
                .power(TEST_DEVICE_POWER)
                .build();
    }

    @Test
    void createDevice_successfully() {
        when(deviceRepository.existsBySerialNumber(serialNumber)).thenReturn(false);
        when(deviceConverter.fromCreateDto(deviceCreateDto)).thenReturn(device);
        when(deviceRepository.save(device)).thenReturn(device);
        when(deviceConverter.toDto(device)).thenReturn(deviceDto);

        DeviceDto result = deviceService.createDevice(deviceCreateDto);

        assertNotNull(result);
        assertEquals(deviceDto, result);
        verify(deviceRepository).existsBySerialNumber(serialNumber);
        verify(deviceConverter).fromCreateDto(deviceCreateDto);
        verify(deviceRepository).save(device);
        verify(deviceConverter).toDto(device);
    }

    @Test
    void createDevice_serialNumberAlreadyExists_throwsException() {
        when(deviceRepository.existsBySerialNumber(serialNumber)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deviceService.createDevice(deviceCreateDto)
        );
        assertEquals(DUPLICATE_SERIAL_MESSAGE, exception.getMessage());

        verify(deviceRepository).existsBySerialNumber(serialNumber);
        verify(deviceConverter, never()).fromCreateDto(any());
        verify(deviceRepository, never()).save(any());
        verify(deviceConverter, never()).toDto(any());
    }

    @Test
    void updateDevice_successfully() {
        DeviceDto updateDto = DeviceDto.builder()
                .id(deviceId)
                .name(UPDATED_DEVICE_NAME)
                .description(UPDATED_DEVICE_DESCRIPTION)
                .serialNumber(serialNumber)
                .units(UPDATED_DEVICE_SLOTS)
                .power(UPDATED_DEVICE_POWER)
                .build();

        Device updatedDevice = Device.builder()
                .id(deviceId)
                .name(UPDATED_DEVICE_NAME)
                .description(UPDATED_DEVICE_DESCRIPTION)
                .serialNumber(serialNumber)
                .units(UPDATED_DEVICE_SLOTS)
                .power(UPDATED_DEVICE_POWER)
                .build();

        when(deviceRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(device));
        when(deviceRepository.save(any(Device.class))).thenReturn(updatedDevice);
        when(deviceConverter.toDto(updatedDevice)).thenReturn(updateDto);

        DeviceDto result = deviceService.updateDevice(serialNumber, updateDto);

        assertNotNull(result);
        assertEquals(updateDto, result);
        verify(deviceRepository).findBySerialNumber(serialNumber);
        verify(deviceRepository).save(device);
        verify(deviceConverter).toDto(updatedDevice);

        assertEquals(UPDATED_DEVICE_NAME, device.getName());
        assertEquals(UPDATED_DEVICE_DESCRIPTION, device.getDescription());
        assertEquals(UPDATED_DEVICE_SLOTS, device.getUnits());
        assertEquals(UPDATED_DEVICE_POWER, device.getPower());
    }

    @Test
    void updateDevice_serialNumberMismatch_throwsException() {
        DeviceDto updateDto = DeviceDto.builder()
                .serialNumber(DIFFERENT_SERIAL)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> deviceService.updateDevice(serialNumber, updateDto)
        );
        assertEquals(SERIAL_NUMBER_MISMATCH_MESSAGE, exception.getMessage());

        verify(deviceRepository, never()).findBySerialNumber(any());
        verify(deviceRepository, never()).save(any());
        verify(deviceConverter, never()).toDto(any());
    }

    @Test
    void updateDevice_deviceNotFound_throwsException() {
        when(deviceRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> deviceService.updateDevice(serialNumber, deviceDto)
        );
        assertEquals(DEVICE_NOT_FOUND_MESSAGE, exception.getMessage());

        verify(deviceRepository).findBySerialNumber(serialNumber);
        verify(deviceRepository, never()).save(any());
        verify(deviceConverter, never()).toDto(any());
    }

    @Test
    void deleteDevice_successfully() {
        when(deviceRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(device));
        doNothing().when(deviceRepository).delete(device);

        deviceService.deleteDevice(serialNumber);

        verify(deviceRepository).findBySerialNumber(serialNumber);
        verify(deviceRepository).delete(device);
    }

    @Test
    void deleteDevice_deviceNotFound_throwsException() {
        when(deviceRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> deviceService.deleteDevice(serialNumber)
        );
        assertEquals(DEVICE_NOT_FOUND_MESSAGE, exception.getMessage());

        verify(deviceRepository).findBySerialNumber(serialNumber);
        verify(deviceRepository, never()).delete(any());
    }

    @Test
    void getDevice_successfully() {
        when(deviceRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(device));
        when(deviceConverter.toDto(device)).thenReturn(deviceDto);

        DeviceDto result = deviceService.getDevice(serialNumber);

        assertNotNull(result);
        assertEquals(deviceDto, result);
        verify(deviceRepository).findBySerialNumber(serialNumber);
        verify(deviceConverter).toDto(device);
    }

    @Test
    void getDevice_deviceNotFound_throwsException() {
        when(deviceRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> deviceService.getDevice(serialNumber)
        );
        assertEquals(DEVICE_NOT_FOUND_MESSAGE, exception.getMessage());

        verify(deviceRepository).findBySerialNumber(serialNumber);
        verify(deviceConverter, never()).toDto(any());
    }

    @Test
    void getAllDevices_successfully() {
        Device device2 = Device.builder()
                .id(UUID.randomUUID())
                .name(TEST_DEVICE_2_NAME)
                .serialNumber(TEST_DEVICE_2_SERIAL)
                .units(TEST_DEVICE_2_SLOTS)
                .power(TEST_DEVICE_2_POWER)
                .build();

        DeviceDto deviceDto2 = DeviceDto.builder()
                .id(device2.getId())
                .name(TEST_DEVICE_2_NAME)
                .serialNumber(TEST_DEVICE_2_SERIAL)
                .units(TEST_DEVICE_2_SLOTS)
                .power(TEST_DEVICE_2_POWER)
                .build();

        List<Device> devices = Arrays.asList(device, device2);
        List<DeviceDto> expectedDtos = Arrays.asList(deviceDto, deviceDto2);

        when(deviceRepository.findAll()).thenReturn(devices);
        when(deviceConverter.toDto(device)).thenReturn(deviceDto);
        when(deviceConverter.toDto(device2)).thenReturn(deviceDto2);

        List<DeviceDto> result = deviceService.getAllDevices();

        assertNotNull(result);
        assertEquals(EXPECTED_DEVICE_COUNT, result.size());
        assertEquals(expectedDtos, result);
        verify(deviceRepository).findAll();
        verify(deviceConverter).toDto(device);
        verify(deviceConverter).toDto(device2);
    }
}
