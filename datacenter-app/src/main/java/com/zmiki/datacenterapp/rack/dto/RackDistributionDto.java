package com.zmiki.datacenterapp.rack.dto;

import com.zmiki.datacenterapp.device.dto.DeviceDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RackDistributionDto {
    private String rackName;
    private String rackSerialNumber;
    private double powerUsagePercentage;
    private List<DeviceDto> devices;
}
