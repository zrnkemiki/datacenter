package com.zmiki.datacenterapp.rack;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RackRepository extends JpaRepository<Rack, UUID> {
    Optional<Rack> findBySerialNumber(String serialNumber);
}
