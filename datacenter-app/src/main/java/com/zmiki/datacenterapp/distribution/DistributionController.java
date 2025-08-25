package com.zmiki.datacenterapp.distribution;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/distribution")
@RequiredArgsConstructor
@Tag(name = "Distribution", description = "API for distributing and arranging devices across racks")
public class DistributionController {

    private final DistributionService distributionService;

    @PostMapping("/distribute-devices")
    @Operation(summary = "Distributes devices across racks")
    public ResponseEntity<DistributionResult> distributeDevices(@Valid @RequestBody DistributionRequest request) {
        DistributionResult result = distributionService.arrangeDevices(
                request.getDeviceSerialNumbers(),
                request.getRackSerialNumbers());
        return ResponseEntity.ok(result);
    }

}
