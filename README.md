# Datacenter Application

## Run Command
```bash
docker compose up app
```

## SWAGGER URL http://localhost:8080/swagger-ui/index.html
## API for distribution suggestion http://localhost:8080/api/distribution/distribute-devices


### Request Body
```json
{
  "deviceSerialNumbers": [
    "SRV001",
    "SRV002",
    "SRV003",
    "SRV004",
    "SRV005",
    "SRV006",
    "SRV007",
    "SRV008",
    "SRV009",
    "SRV010",
    "SRV011",
    "SRV012",
    "SRV013",
    "SRV014",
    "SRV015",
    "SRV016",
    "SRV017",
    "SRV018",
    "SRV019",
    "SRV020",
    "SRV021",
    "SRV022",
    "SRV023",
    "SRV024",
    "SRV025",
    "SRV026",
    "SRV027",
    "SRV028",
    "SRV029",
    "SRV030",
    "SRV031",
    "SRV032",
    "SRV033",
    "SRV034",
    "SRV035",
    "SRV036",
    "SRV037",
    "SRV038",
    "SRV039",
    "SRV040"
  ],
  "rackSerialNumbers": [
    "RCK001",
    "RCK002",
    "RCK003",
    "RCK004",
    "RCK005",
    "RCK006",
    "RCK007",
    "RCK008",
    "RCK009",
    "RCK010"
  ]
}
```