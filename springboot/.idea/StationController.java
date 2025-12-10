package com.example.underground_api.controller;

import com.example.underground_api.entity.Station;
import com.example.underground_api.service.StationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 站点管理控制器
 * 提供地铁站点相关的REST API接口
 */
@RestController
@RequestMapping("/api/stations")
public class StationController {

    @Autowired
    private StationService stationService;

    /**
     * 获取所有站点
     * GET /api/stations
     */
    @GetMapping
    public ResponseEntity<List<Station>> getAllStations() {
        System.out.println("=== 获取所有站点调试 ===");
        List<Station> stations = stationService.getAllStations();
        System.out.println("所有站点数量: " + stations.size());
        System.out.println("所有站点数据: " + stations);
        return ResponseEntity.ok(stations);
    }

    /**
     * 根据ID获取站点
     * GET /api/stations/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getStationById(@PathVariable Integer id) {
        return stationService.getStationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 根据名称搜索站点
     * GET /api/stations/search?name={keyword}
     */
    @GetMapping("/search")
    public ResponseEntity<List<Station>> searchStationsByName(@RequestParam String name) {
        List<Station> stations = stationService.searchStationsByName(name);
        return ResponseEntity.ok(stations);
    }

    /**
     * 创建新站点
     * POST /api/stations
     */
    @PostMapping
    public ResponseEntity<?> createStation(@RequestBody Station station) {
        try {
            Station createdStation = stationService.createStation(station);
            return ResponseEntity.ok(createdStation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 更新站点信息
     * PUT /api/stations/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> updateStation(@PathVariable Integer id, @RequestBody Station stationDetails) {
        try {
            Station updatedStation = stationService.updateStation(id, stationDetails);
            return ResponseEntity.ok(updatedStation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 删除站点
     * DELETE /api/stations/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteStation(@PathVariable Integer id) {
        try {
            stationService.deleteStation(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 根据线路ID获取站点
     * GET /api/stations/line/{lineId}
     */
    @GetMapping("/line/{lineId}")
    public ResponseEntity<List<Station>> getStationsByLine(@PathVariable Integer lineId) {
        System.out.println("=== 获取线路站点调试 ===");
        System.out.println("请求的线路ID: " + lineId);
        List<Station> stations = stationService.getStationsByLine(lineId);
        System.out.println("查询到的站点数量: " + stations.size());
        System.out.println("站点数据: " + stations);
        return ResponseEntity.ok(stations);
    }

    /**
     * 获取换乘站点
     * GET /api/stations/transfers
     */
    @GetMapping("/transfers")
    public ResponseEntity<List<Station>> getTransferStations() {
        List<Station> transferStations = stationService.getTransferStations();
        return ResponseEntity.ok(transferStations);
    }

    /**
     * 更新站点拥挤度
     * PATCH /api/stations/{id}/congestion
     */
    @PatchMapping("/{id}/congestion")
    public ResponseEntity<?> updateCongestion(@PathVariable Integer id, @RequestBody Map<String, Integer> request) {
        Integer congestionLevel = request.get("level");
        if (congestionLevel == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "拥挤度不能为空"));
        }

        try {
            Station updatedStation = stationService.updateCongestion(id, congestionLevel);
            return ResponseEntity.ok(updatedStation);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 半径范围内站点查询
     * GET /api/stations/nearby?lat=39.9&lng=116.4&radius=2.0
     */
    @GetMapping("/nearby")
    public ResponseEntity<?> getStationsWithinRadius(
            @RequestParam BigDecimal lat,
            @RequestParam BigDecimal lng,
            @RequestParam Double radius) {

        try {
            List<Station> stations = stationService.getStationsWithinRadius(lat, lng, radius);
            return ResponseEntity.ok(stations);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/for-map")
    public ResponseEntity<List<Station>> getStationsForMap() {
        List<Station> stations = stationService.getAllStations();
        return ResponseEntity.ok(stations);
    }

}