package com.zmiki.datacenterapp.distribution;

import com.zmiki.datacenterapp.device.Device;
import com.zmiki.datacenterapp.device.DeviceConverter;
import com.zmiki.datacenterapp.device.DeviceService;
import com.zmiki.datacenterapp.device.dto.DeviceDto;
import com.zmiki.datacenterapp.exception.NoDevicesProvidedException;
import com.zmiki.datacenterapp.exception.NoRacksProvidedException;
import com.zmiki.datacenterapp.rack.Rack;
import com.zmiki.datacenterapp.rack.RackConverter;
import com.zmiki.datacenterapp.rack.RackService;
import com.zmiki.datacenterapp.rack.dto.RackDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DistributionServiceTest {

    private static final String SERVER_1 = "SRV001";
    private static final String SERVER_2 = "SRV002";
    private static final String SERVER_3 = "SRV003";
    private static final String SERVER_4 = "SRV004";
    private static final String SERVER_5 = "SRV005";

    private static final String RACK_A = "RCK001";
    private static final String RACK_B = "RCK002";
    private static final String RACK_C = "RCK003";

    private static final int LOW_POWER = 200;
    private static final int MEDIUM_POWER = 500;
    private static final int HIGH_POWER = 800;
    private static final int MAX_POWER = 1200;

    private static final int SMALL_UNITS = 1;
    private static final int MEDIUM_UNITS = 2;
    private static final int LARGE_UNITS = 4;
    private static final int HUGE_UNITS = 15;

    private static final int STANDARD_RACK_POWER = 1000;
    private static final int MEDIUM_RACK_POWER = 1500;
    private static final int LARGE_RACK_POWER = 2000;

    private static final int SMALL_RACK_UNITS = 8;
    private static final int STANDARD_RACK_UNITS = 10;
    private static final int LARGE_RACK_UNITS = 12;

    @Mock
    private DeviceConverter deviceConverter;

    @Mock
    private RackConverter rackConverter;

    @Mock
    private DeviceService deviceService;

    @Mock
    private RackService rackService;

    private DistributionService distributionService;

    @BeforeEach
    void setUp() {
        distributionService = new DistributionService(
                deviceConverter,
                rackConverter,
                deviceService,
                rackService);

        setupMockConverters();
    }

    private void setupMockConverters() {
        DeviceDto mockDeviceDto = DeviceDto.builder()
                .serialNumber("MOCK")
                .name("Mock Device")
                .power(100)
                .units(1)
                .build();

        RackDto mockRackDto = RackDto.builder()
                .serialNumber("MOCK")
                .name("Mock Rack")
                .maxPower(1000)
                .unit(10)
                .build();

        lenient().when(deviceConverter.toDto(any(Device.class))).thenReturn(mockDeviceDto);
        lenient().when(rackConverter.toDto(any(Rack.class))).thenReturn(mockRackDto);
    }

    private Device createDevice(String serialNumber, int power, int units) {
        return Device.builder()
                .id(UUID.randomUUID())
                .name("Test Device " + serialNumber)
                .serialNumber(serialNumber)
                .power(power)
                .units(units)
                .build();
    }

    private Rack createRack(String serialNumber, int maxPower, int maxUnits) {
        return Rack.builder()
                .id(UUID.randomUUID())
                .name("Test Rack " + serialNumber)
                .serialNumber(serialNumber)
                .maxPower(maxPower)
                .maxUnits(maxUnits)
                .build();
    }


    @Test
    void arrangeDevices_successful() {
        List<String> deviceSerials = Arrays.asList(SERVER_1, SERVER_2);
        List<String> rackSerials = List.of(RACK_A);

        Device mockDevice1 = createDevice(SERVER_1, MEDIUM_POWER, MEDIUM_UNITS);
        Device mockDevice2 = createDevice(SERVER_2, LOW_POWER, SMALL_UNITS);
        List<Device> devices = Arrays.asList(mockDevice1, mockDevice2);

        Rack mockRack1 = createRack(RACK_A, STANDARD_RACK_POWER, STANDARD_RACK_UNITS);
        List<Rack> racks = List.of(mockRack1);

        when(deviceService.getDevicesBySerialNumbers(deviceSerials)).thenReturn(devices);
        when(rackService.getRacksBySerialNumbers(rackSerials)).thenReturn(racks);

        DistributionResult result = distributionService.arrangeDevices(deviceSerials, rackSerials);

        assertThat(result).isNotNull();
        assertThat(result.getRacksWithDevices()).hasSize(1);
        assertThat(result.getUnplacedDevices()).isEmpty();
        assertThat(result.getMessage()).isEqualTo("Devices successfully distributed");
    }

    @Test
    void arrangeDevices_no_racks_provided_throws_exception() {
        Device device = createDevice(SERVER_1, MEDIUM_POWER, MEDIUM_UNITS);
        List<Device> devices = List.of(device);
        List<Rack> racks = Collections.emptyList();

        assertThatThrownBy(() -> distributionService.packDevicesIntoRacks(devices, racks))
                .isInstanceOf(NoRacksProvidedException.class)
                .hasMessage("No racks provided for device distribution. Cannot proceed.");
    }

    @Test
    void arrangeDevices_no_devices_provided_throws_exception() {
        List<Device> devices = Collections.emptyList();
        Rack rack = createRack(RACK_A, STANDARD_RACK_POWER, STANDARD_RACK_UNITS);
        List<Rack> racks = List.of(rack);

        assertThatThrownBy(() -> distributionService.packDevicesIntoRacks(devices, racks))
                .isInstanceOf(NoDevicesProvidedException.class)
                .hasMessage("No devices provided for distribution. Cannot proceed.");
    }

    @Test
    void arrangeDevices_both_empty_throws_racks_exception_first() {
        List<Device> devices = Collections.emptyList();
        List<Rack> racks = Collections.emptyList();

        assertThatThrownBy(() -> distributionService.packDevicesIntoRacks(devices, racks))
                .isInstanceOf(NoRacksProvidedException.class)
                .hasMessage("No racks provided for device distribution. Cannot proceed.");
    }

    @Test
    void arrangeDevices_device_units_too_large() {
        Device bigDevice = createDevice(SERVER_1, MEDIUM_POWER, HUGE_UNITS);
        List<Device> devices = List.of(bigDevice);

        Rack smallRack = createRack(RACK_A, STANDARD_RACK_POWER, STANDARD_RACK_UNITS);
        List<Rack> racks = List.of(smallRack);

        DistributionResult result = distributionService.packDevicesIntoRacks(devices, racks);

        assertThat(result.getRacksWithDevices()).hasSize(1);
        assertThat(result.getRacksWithDevices().getFirst().getDevices()).isEmpty();
        assertThat(result.getUnplacedDevices()).containsExactly(bigDevice);
        assertThat(result.getMessage()).isEqualTo("Some devices could not be distributed to any rack.");
    }

    @Test
    void arrangeDevices_power_over_limit() {
        Device powerHungryDevice = createDevice(SERVER_1, MAX_POWER, MEDIUM_UNITS);
        List<Device> devices = List.of(powerHungryDevice);

        Rack lowPowerRack = createRack(RACK_A, STANDARD_RACK_POWER, STANDARD_RACK_UNITS);
        List<Rack> racks = List.of(lowPowerRack);

        DistributionResult result = distributionService.packDevicesIntoRacks(devices, racks);

        assertThat(result.getRacksWithDevices()).hasSize(1);
        assertThat(result.getRacksWithDevices().getFirst().getDevices()).isEmpty();
        assertThat(result.getUnplacedDevices()).containsExactly(powerHungryDevice);
        assertThat(result.getMessage()).isEqualTo("Some devices could not be distributed to any rack.");
    }

    @Test
    void arrangeDevices_equal_distribution() {
        Device highPowerDevice = createDevice(SERVER_1, HIGH_POWER, MEDIUM_UNITS);
        Device lowPowerDevice = createDevice(SERVER_2, LOW_POWER, SMALL_UNITS);
        List<Device> devices = Arrays.asList(highPowerDevice, lowPowerDevice);

        Rack rack1 = createRack(RACK_A, STANDARD_RACK_POWER, STANDARD_RACK_UNITS);
        Rack rack2 = createRack(RACK_B, STANDARD_RACK_POWER, STANDARD_RACK_UNITS);
        List<Rack> racks = Arrays.asList(rack1, rack2);

        DistributionResult result = distributionService.packDevicesIntoRacks(devices, racks);

        assertThat(result.getRacksWithDevices()).hasSize(2);
        assertThat(result.getUnplacedDevices()).isEmpty();
        assertThat(result.getMessage()).isEqualTo("Devices successfully distributed");

        assertThat(result.getRacksWithDevices().get(0).getDevices()).hasSize(1);
        assertThat(result.getRacksWithDevices().get(1).getDevices()).hasSize(1);
    }

    @Test
    void arrangeDevices_complex_distribution() {
        List<String> deviceSerials = Arrays.asList(SERVER_1, SERVER_2, SERVER_3, SERVER_4, SERVER_5);
        List<String> rackSerials = Arrays.asList(RACK_A, RACK_B, RACK_C);

        Device server1 = createDevice(SERVER_1, MAX_POWER, LARGE_UNITS);
        Device server2 = createDevice(SERVER_2, HIGH_POWER, MEDIUM_UNITS);
        Device server3 = createDevice(SERVER_3, MEDIUM_POWER, MEDIUM_UNITS);
        Device server4 = createDevice(SERVER_4, LOW_POWER, SMALL_UNITS);
        Device server5 = createDevice(SERVER_5, LOW_POWER, SMALL_UNITS);
        List<Device> devices = Arrays.asList(server1, server2, server3, server4, server5);

        Rack rack1 = createRack(RACK_A, LARGE_RACK_POWER, LARGE_RACK_UNITS);
        Rack rack2 = createRack(RACK_B, MEDIUM_RACK_POWER, STANDARD_RACK_UNITS);
        Rack rack3 = createRack(RACK_C, STANDARD_RACK_POWER, SMALL_RACK_UNITS);
        List<Rack> racks = Arrays.asList(rack1, rack2, rack3);

        when(deviceService.getDevicesBySerialNumbers(deviceSerials)).thenReturn(devices);
        when(rackService.getRacksBySerialNumbers(rackSerials)).thenReturn(racks);

        DistributionResult result = distributionService.arrangeDevices(deviceSerials, rackSerials);

        assertThat(result.getRacksWithDevices()).hasSize(3);
        assertThat(result.getUnplacedDevices()).isEmpty();
        assertThat(result.getMessage()).isEqualTo("Devices successfully distributed");

        assertThat(result.getRacksWithDevices().get(0).getDevices()).hasSize(2);
        assertThat(result.getRacksWithDevices().get(1).getDevices()).hasSize(2);
        assertThat(result.getRacksWithDevices().get(2).getDevices()).hasSize(1);

        assertThat(result.getRacksWithDevices().get(0).getPowerUsagePercentage()).isGreaterThan(65);
        assertThat(result.getRacksWithDevices().get(0).getPowerUsagePercentage()).isLessThan(85);
        assertThat(result.getRacksWithDevices().get(1).getPowerUsagePercentage()).isGreaterThan(65);
        assertThat(result.getRacksWithDevices().get(1).getPowerUsagePercentage()).isLessThan(75);
        assertThat(result.getRacksWithDevices().get(2).getPowerUsagePercentage()).isGreaterThan(45);
        assertThat(result.getRacksWithDevices().get(2).getPowerUsagePercentage()).isLessThan(55);
    }
}
