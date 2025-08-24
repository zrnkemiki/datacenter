package com.zmiki.datacenterapp.device;

import com.zmiki.datacenterapp.rack.Rack;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "device")
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String description;

    @NotBlank
    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Min(1)
    @Column(nullable = false)
    private int units;

    @Positive
    @Column(name = "power", nullable = false)
    private int power;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "rack_id")
    private Rack rack;
}
