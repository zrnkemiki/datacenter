package com.zmiki.datacenterapp.distribution;

import com.zmiki.datacenterapp.device.Device;
import com.zmiki.datacenterapp.rack.dto.RackDistributionDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionResult {
    private String message;
    private List<RackDistributionDto> racksWithDevices;
    private List<Device> unplacedDevices;
}
