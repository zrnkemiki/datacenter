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
import com.zmiki.datacenterapp.rack.dto.RackDistributionDto;
import com.zmiki.datacenterapp.rack.dto.RackDto;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class DistributionService {

    private final DeviceConverter deviceConverter;
    private final RackConverter rackConverter;
    private final DeviceService deviceService;
    private final RackService rackService;

    public DistributionResult arrangeDevices(List<String> deviceSerialNumbers, List<String> rackSerialNumbers) {
        List<Device> devices = deviceService.getDevicesBySerialNumbers(deviceSerialNumbers);
        List<Rack> racks = rackService.getRacksBySerialNumbers(rackSerialNumbers);
        return packDevicesIntoRacks(devices, racks);
    }

    public DistributionResult packDevicesIntoRacks(List<Device> devices, List<Rack> racks) {
        if (racks.isEmpty()) {
            throw new NoRacksProvidedException("No racks provided for device distribution. Cannot proceed.");
        }
        if (devices.isEmpty()) {
            throw new NoDevicesProvidedException("No devices provided for distribution. Cannot proceed.");
        }

        List<Rack> racksCopy = racks.stream().map(Rack::copy).toList();

        int numberOfRacks = racksCopy.size();

        int[] usedPower = new int[numberOfRacks];
        int[] remainingUnits = new int[numberOfRacks];

        for (int i = 0; i < numberOfRacks; i++) {
            usedPower[i] = 0;
            remainingUnits[i] = racksCopy.get(i).getMaxUnits();
        }

        List<Device> sortedDevices = new ArrayList<>(devices);
        sortedDevices.sort(Comparator.comparingInt(Device::getPower).reversed());

        List<Device> unplacedDevices = new ArrayList<>();

        for (Device currentDevice : sortedDevices) {
            int bestIndex = -1;
            double bestPower = Double.POSITIVE_INFINITY;

            for (int i = 0; i < numberOfRacks; i++) {
                Rack currentRack = racksCopy.get(i);

                if (currentDevice.getUnits() > remainingUnits[i]) continue;
                if (usedPower[i] + currentDevice.getPower() > currentRack.getMaxPower()) continue;

                double powerAfter = ((double) (usedPower[i] + currentDevice.getPower())) / currentRack.getMaxPower();

                if (powerAfter < bestPower || (powerAfter == bestPower && remainingUnits[i] > (bestIndex == -1 ? -1 : remainingUnits[bestIndex]))) {
                    bestIndex = i;
                    bestPower = powerAfter;
                }
            }

            if (bestIndex == -1) {
                unplacedDevices.add(currentDevice);
            } else {
                racksCopy.get(bestIndex).getDevices().add(currentDevice);
                usedPower[bestIndex] += currentDevice.getPower();
                remainingUnits[bestIndex] -= currentDevice.getUnits();
            }
        }

        List<RackDistributionDto> racksWithDevices = new ArrayList<>();
        for (int i = 0; i < numberOfRacks; i++) {
            double percentageOfPowerUsage = 100.0 * usedPower[i] / racksCopy.get(i).getMaxPower();
            percentageOfPowerUsage = Math.round(percentageOfPowerUsage * 100.0) / 100.0;

            List<DeviceDto> deviceDtos = racksCopy.get(i).getDevices()
                    .stream()
                    .map(deviceConverter::toDto)
                    .collect(Collectors.toList());

            RackDto rackDto = rackConverter.toDto(racksCopy.get(i));

            racksWithDevices.add(RackDistributionDto.builder()
                    .rackName(rackDto.getName())
                    .rackSerialNumber(rackDto.getSerialNumber())
                    .devices(deviceDtos)
                    .powerUsagePercentage(percentageOfPowerUsage)
                    .build());
        }

        return DistributionResult.builder()
                .message(unplacedDevices.isEmpty() ? "Devices successfully distributed" : "Some devices could not be distributed to any rack.")
                .racksWithDevices(racksWithDevices)
                .unplacedDevices(unplacedDevices)
                .build();
    }
}
