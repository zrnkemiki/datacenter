INSERT INTO rack (id, name, description, serial_number, max_units, max_power)
VALUES (gen_random_uuid(), 'Rack1', 'Production rack 1', 'RCK001', 20, 3000),
       (gen_random_uuid(), 'Rack2', 'Production rack 2', 'RCK002', 20, 2500),
       (gen_random_uuid(), 'Rack3', 'Production rack 3', 'RCK003', 20, 4000),
       (gen_random_uuid(), 'Rack4', 'Production rack 4', 'RCK004', 20, 3500),
       (gen_random_uuid(), 'Rack5', 'Production rack 5', 'RCK005', 20, 3000);

INSERT INTO device (id, name, description, serial_number, units, power, rack_id)
VALUES (gen_random_uuid(), 'Server1', 'Server Test 1', 'SRV001', 2, 300, NULL),
       (gen_random_uuid(), 'Server2', 'Server Test 2', 'SRV002', 3, 500, NULL),
       (gen_random_uuid(), 'Server3', 'Server Test 3', 'SRV003', 4, 1200, NULL),
       (gen_random_uuid(), 'Server4', 'Server Test 4', 'SRV004', 1, 300, NULL),
       (gen_random_uuid(), 'Server5', 'Server Test 5', 'SRV005', 2, 500, NULL),
       (gen_random_uuid(), 'Server6', 'Server Test 6', 'SRV006', 3, 1200, NULL),
       (gen_random_uuid(), 'Server7', 'Server Test 7', 'SRV007', 2, 300, NULL),
       (gen_random_uuid(), 'Server8', 'Server Test 8', 'SRV008', 1, 500, NULL),
       (gen_random_uuid(), 'Server9', 'Server Test 9', 'SRV009', 4, 1200, NULL),
       (gen_random_uuid(), 'Server10', 'Server Test 10', 'SRV010', 2, 300, NULL),
       (gen_random_uuid(), 'Server11', 'Server Test 11', 'SRV011', 3, 500, NULL),
       (gen_random_uuid(), 'Server12', 'Server Test 12', 'SRV012', 2, 1200, NULL),
       (gen_random_uuid(), 'Server13', 'Server Test 13', 'SRV013', 1, 300, NULL),
       (gen_random_uuid(), 'Server14', 'Server Test 14', 'SRV014', 2, 500, NULL),
       (gen_random_uuid(), 'Server15', 'Server Test 15', 'SRV015', 3, 1200, NULL),
       (gen_random_uuid(), 'Server16', 'Server Test 16', 'SRV016', 2, 300, NULL),
       (gen_random_uuid(), 'Server17', 'Server Test 17', 'SRV017', 4, 500, NULL),
       (gen_random_uuid(), 'Server18', 'Server Test 18', 'SRV018', 1, 1200, NULL),
       (gen_random_uuid(), 'Server19', 'Server Test 19', 'SRV019', 2, 300, NULL),
       (gen_random_uuid(), 'Server20', 'Server Test 20', 'SRV020', 3, 500, NULL);
