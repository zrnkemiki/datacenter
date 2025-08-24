package com.zmiki.datacenterapp.device.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceDto {
    private UUID id;

    @NotBlank(message = "Device name is required")
    private String name;

    private String description;

    @NotBlank(message = "Serial number is required")
    private String serialNumber;

    @Min(value = 1, message = "Device must take at least 1 unit")
    private int units;

    @Positive(message = "Power must be positive")
    private int power;
}
