package com.zmiki.datacenterapp.distribution;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DistributionRequest {
    @NotEmpty(message = "Device serial numbers list cannot be empty")
    private List<String> deviceSerialNumbers;

    @NotEmpty(message = "Rack serial numbers list cannot be empty")
    private List<String> rackSerialNumbers;
}
