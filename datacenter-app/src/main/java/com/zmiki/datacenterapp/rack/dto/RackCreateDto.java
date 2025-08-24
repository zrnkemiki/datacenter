package com.zmiki.datacenterapp.rack.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RackCreateDto {

    @NotBlank(message = "Rack name is required")
    private String name;

    private String description;

    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    @Min(value = 1, message = "Rack must have at least 1 unit")
    private int unit;

    @Positive(message = "Max power must be positive")
    private int maxPower;
}
