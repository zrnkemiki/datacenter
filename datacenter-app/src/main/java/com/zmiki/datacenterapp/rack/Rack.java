package com.zmiki.datacenterapp.rack;

import com.zmiki.datacenterapp.device.Device;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "rack")
public class Rack {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 120)
    private String name;

    private String description;

    @NotBlank
    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Min(1)
    @Column(name = "max_units", nullable = false)
    private int maxUnits;

    @Positive
    @Column(name = "max_power", nullable = false)
    private int maxPower;

    @OneToMany(mappedBy = "rack", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Device> devices = new ArrayList<>();

    public Rack copy() {
        Rack r = Rack.builder()
                .name(name)
                .description(description)
                .serialNumber(serialNumber)
                .maxUnits(maxUnits)
                .maxPower(maxPower)
                .build();

        r.setDevices(new ArrayList<>());
        if (this.getDevices() != null ){
            r.getDevices().addAll(this.devices);
        }
        return r;
    }

}
