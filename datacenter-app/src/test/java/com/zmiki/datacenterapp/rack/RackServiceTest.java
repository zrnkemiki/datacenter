package com.zmiki.datacenterapp.rack;

import com.zmiki.datacenterapp.device.Device;
import com.zmiki.datacenterapp.rack.dto.RackCreateDto;
import com.zmiki.datacenterapp.rack.dto.RackDto;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RackServiceTest {

    private static final String TEST_RACK_NAME = "Test Rack";
    private static final String TEST_RACK_DESCRIPTION = "Test Description";
    private static final String TEST_SERIAL_NUMBER = "RACK-001";
    private static final String TEST_RACK_2_NAME = "Test Rack 2";
    private static final String TEST_RACK_2_SERIAL = "RACK-002";
    private static final String UPDATED_RACK_NAME = "Updated Rack";
    private static final String UPDATED_RACK_DESCRIPTION = "Updated Description";
    private static final String DIFFERENT_SERIAL = "DIFFERENT-SERIAL";
    private static final String RACK_NOT_FOUND_MESSAGE = "Rack not found";
    private static final String SERIAL_NUMBER_MISMATCH_MESSAGE = "Serial number in path must match serial number in request body";
    private static final String DUPLICATE_SERIAL_MESSAGE = "Rack with serial number " + TEST_SERIAL_NUMBER + " already exists";
    private static final String CANNOT_DELETE_WITH_DEVICES_MESSAGE = "Cannot delete rack with devices";

    private static final int TEST_RACK_SLOTS = 10;
    private static final int TEST_RACK_MAX_POWER = 1000;
    private static final int TEST_RACK_2_SLOTS = 5;
    private static final int TEST_RACK_2_MAX_POWER = 500;
    private static final int UPDATED_RACK_SLOTS = 20;
    private static final int UPDATED_RACK_MAX_POWER = 2000;
    private static final int EXPECTED_RACK_COUNT = 2;

    @Mock
    private RackRepository rackRepository;

    @Mock
    private RackConverter rackConverter;

    @InjectMocks
    private RackService rackService;

    private Rack rack;
    private RackCreateDto rackCreateDto;
    private RackDto rackDto;
    private String serialNumber;

    @BeforeEach
    void setUp() {
        serialNumber = TEST_SERIAL_NUMBER;

        rack = Rack.builder()
                .name(TEST_RACK_NAME)
                .description(TEST_RACK_DESCRIPTION)
                .serialNumber(serialNumber)
                .maxUnits(TEST_RACK_SLOTS)
                .maxPower(TEST_RACK_MAX_POWER)
                .devices(new ArrayList<>())
                .build();

        rackCreateDto = RackCreateDto.builder()
                .name(TEST_RACK_NAME)
                .description(TEST_RACK_DESCRIPTION)
                .serialNumber(serialNumber)
                .unit(TEST_RACK_SLOTS)
                .maxPower(TEST_RACK_MAX_POWER)
                .build();

        rackDto = RackDto.builder()
                .name(TEST_RACK_NAME)
                .description(TEST_RACK_DESCRIPTION)
                .serialNumber(serialNumber)
                .unit(TEST_RACK_SLOTS)
                .maxPower(TEST_RACK_MAX_POWER)
                .devices(new ArrayList<>())
                .build();
    }

    @Test
    void createRack_successfully() {
        when(rackRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());
        when(rackConverter.fromCreateDto(rackCreateDto)).thenReturn(rack);
        when(rackRepository.save(rack)).thenReturn(rack);
        when(rackConverter.toDto(rack)).thenReturn(rackDto);

        RackDto result = rackService.createRack(rackCreateDto);

        assertNotNull(result);
        assertEquals(rackDto, result);
        verify(rackRepository).findBySerialNumber(serialNumber);
        verify(rackConverter).fromCreateDto(rackCreateDto);
        verify(rackRepository).save(rack);
        verify(rackConverter).toDto(rack);
    }

    @Test
    void createRack_serialNumberAlreadyExists_throwsException() {
        when(rackRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(rack));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> rackService.createRack(rackCreateDto)
        );
        assertEquals(DUPLICATE_SERIAL_MESSAGE, exception.getMessage());

        verify(rackRepository).findBySerialNumber(serialNumber);
        verify(rackConverter, never()).fromCreateDto(any());
        verify(rackRepository, never()).save(any());
        verify(rackConverter, never()).toDto(any());
    }

    @Test
    void updateRack_successfully() {
        RackDto updateDto = RackDto.builder()
                .name(UPDATED_RACK_NAME)
                .description(UPDATED_RACK_DESCRIPTION)
                .serialNumber(serialNumber)
                .unit(UPDATED_RACK_SLOTS)
                .maxPower(UPDATED_RACK_MAX_POWER)
                .devices(new ArrayList<>())
                .build();

        Rack updatedRack = Rack.builder()
                .name(UPDATED_RACK_NAME)
                .description(UPDATED_RACK_DESCRIPTION)
                .serialNumber(serialNumber)
                .maxUnits(UPDATED_RACK_SLOTS)
                .maxPower(UPDATED_RACK_MAX_POWER)
                .devices(new ArrayList<>())
                .build();

        when(rackRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(rack));
        when(rackRepository.save(any(Rack.class))).thenReturn(updatedRack);
        when(rackConverter.toDto(updatedRack)).thenReturn(updateDto);

        RackDto result = rackService.updateRack(serialNumber, updateDto);

        assertNotNull(result);
        assertEquals(updateDto, result);
        verify(rackRepository).findBySerialNumber(serialNumber);
        verify(rackRepository).save(rack);
        verify(rackConverter).toDto(updatedRack);

        assertEquals(UPDATED_RACK_NAME, rack.getName());
        assertEquals(UPDATED_RACK_DESCRIPTION, rack.getDescription());
        assertEquals(UPDATED_RACK_SLOTS, rack.getMaxUnits());
        assertEquals(UPDATED_RACK_MAX_POWER, rack.getMaxPower());
    }

    @Test
    void updateRack_serialNumberMismatch_throwsException() {
        RackDto updateDto = RackDto.builder()
                .serialNumber(DIFFERENT_SERIAL)
                .build();

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> rackService.updateRack(serialNumber, updateDto)
        );
        assertEquals(SERIAL_NUMBER_MISMATCH_MESSAGE, exception.getMessage());

        verify(rackRepository, never()).findBySerialNumber(any());
        verify(rackRepository, never()).save(any());
        verify(rackConverter, never()).toDto(any());
    }

    @Test
    void updateRack_rackNotFound_throwsException() {
        when(rackRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rackService.updateRack(serialNumber, rackDto)
        );
        assertEquals(RACK_NOT_FOUND_MESSAGE, exception.getMessage());

        verify(rackRepository).findBySerialNumber(serialNumber);
        verify(rackRepository, never()).save(any());
        verify(rackConverter, never()).toDto(any());
    }

    @Test
    void deleteRack_successfully() {
        when(rackRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(rack));
        doNothing().when(rackRepository).delete(rack);

        rackService.deleteRack(serialNumber);

        verify(rackRepository).findBySerialNumber(serialNumber);
        verify(rackRepository).delete(rack);
    }

    @Test
    void deleteRack_rackNotFound_throwsException() {
        when(rackRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rackService.deleteRack(serialNumber)
        );
        assertEquals(RACK_NOT_FOUND_MESSAGE, exception.getMessage());

        verify(rackRepository).findBySerialNumber(serialNumber);
        verify(rackRepository, never()).delete(any());
    }

    @Test
    void deleteRack_withDevices_throwsException() {
        Device device = Device.builder()
                .id(UUID.randomUUID())
                .name("Test Device")
                .serialNumber("DEV-001")
                .build();
        rack.getDevices().add(device);

        when(rackRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(rack));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> rackService.deleteRack(serialNumber)
        );
        assertEquals(CANNOT_DELETE_WITH_DEVICES_MESSAGE, exception.getMessage());

        verify(rackRepository).findBySerialNumber(serialNumber);
        verify(rackRepository, never()).delete(any());
    }

    @Test
    void getRack_successfully() {
        when(rackRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.of(rack));
        when(rackConverter.toDto(rack)).thenReturn(rackDto);

        RackDto result = rackService.getRack(serialNumber);

        assertNotNull(result);
        assertEquals(rackDto, result);
        verify(rackRepository).findBySerialNumber(serialNumber);
        verify(rackConverter).toDto(rack);
    }

    @Test
    void getRack_rackNotFound_throwsException() {
        when(rackRepository.findBySerialNumber(serialNumber)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> rackService.getRack(serialNumber)
        );
        assertEquals(RACK_NOT_FOUND_MESSAGE, exception.getMessage());

        verify(rackRepository).findBySerialNumber(serialNumber);
        verify(rackConverter, never()).toDto(any());
    }

    @Test
    void getAllRacks_successfully() {
        Rack rack2 = Rack.builder()
                .id(UUID.randomUUID())
                .name(TEST_RACK_2_NAME)
                .serialNumber(TEST_RACK_2_SERIAL)
                .maxUnits(TEST_RACK_2_SLOTS)
                .maxPower(TEST_RACK_2_MAX_POWER)
                .devices(new ArrayList<>())
                .build();

        RackDto rackDto2 = RackDto.builder()
                .id(rack2.getId())
                .name(TEST_RACK_2_NAME)
                .serialNumber(TEST_RACK_2_SERIAL)
                .unit(TEST_RACK_2_SLOTS)
                .maxPower(TEST_RACK_2_MAX_POWER)
                .devices(new ArrayList<>())
                .build();

        List<Rack> racks = Arrays.asList(rack, rack2);
        List<RackDto> expectedDtos = Arrays.asList(rackDto, rackDto2);

        when(rackRepository.findAll()).thenReturn(racks);
        when(rackConverter.toDto(rack)).thenReturn(rackDto);
        when(rackConverter.toDto(rack2)).thenReturn(rackDto2);

        List<RackDto> result = rackService.getAllRacks();

        assertNotNull(result);
        assertEquals(EXPECTED_RACK_COUNT, result.size());
        assertEquals(expectedDtos, result);
        verify(rackRepository).findAll();
        verify(rackConverter).toDto(rack);
        verify(rackConverter).toDto(rack2);
    }

    @Test
    void getAllRacks_emptyList_returnsEmptyList() {
        when(rackRepository.findAll()).thenReturn(Collections.emptyList());

        List<RackDto> result = rackService.getAllRacks();

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(rackRepository).findAll();
        verify(rackConverter, never()).toDto(any());
    }
}


