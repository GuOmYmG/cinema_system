package com.example.underground_api.controller;

import com.example.underground_api.dto.PathResult;
import com.example.underground_api.service.PathService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.underground_api.repository.StationRepository;

import java.util.List;
import java.util.Map;

import static java.lang.Boolean.parseBoolean;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    @Autowired
    private PathService pathService;

    @Autowired
    private StationRepository stationRepository; // 需要添加这个依赖

    /**
     * 查询路径
     * POST /api/routes/search
     */
//    @PostMapping("/search")
//    public ResponseEntity<?> searchRoute(@RequestBody Map<String, Object> request) {
//        try {
//            Integer startStationId = (Integer) request.get("startStationId");
//            Integer endStationId = (Integer) request.get("endStationId");
//            String searchType = (String) request.get("searchType");
//            System.out.println("查询路径"+searchType);
//
//            if (startStationId == null || endStationId == null) {
//                return ResponseEntity.badRequest().body(Map.of("error", "起点和终点不能为空"));
//            }
//
//            if (startStationId.equals(endStationId)) {
//                return ResponseEntity.badRequest().body(Map.of("error", "起点和终点不能相同"));
//            }
//
//            PathResult result;
//            if ("leastTransfer".equals(searchType)) {//两种换乘算法
//                result = pathService.findLeastTransferPath(startStationId, endStationId);
//            } else {
//                result = pathService.findShortestPath(startStationId, endStationId);
//            }
//
//            return ResponseEntity.ok(result);
//        } catch (RuntimeException e) {
//            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
//        } catch (Exception e) {
//            return ResponseEntity.internalServerError().body(Map.of("error", "路径查询失败"));
//        }
//    }
    @PostMapping("/search")
    public ResponseEntity<?> searchRoute(@RequestBody Map<String, Object> request) {
        try {
            Integer startStationId = (Integer) request.get("startStationId");
            Integer endStationId = (Integer) request.get("endStationId");
            String searchType = (String) request.get("searchType");
// 1. 打印完整请求体（关键：看后端收到的JSON中multiple是否为true）
            try {
                ObjectMapper mapper = new ObjectMapper();
                String jsonBody = mapper.writeValueAsString(request);
                System.out.println("=== 完整请求体JSON ===");
                System.out.println(jsonBody); // 应显示 {"multiple":true, ...}
            } catch (Exception e) {
                e.printStackTrace();
            }

            Object multipleObj = request.get("multiple");
            boolean returnMultiple = false;

            System.out.println("=== multiple解析详情 ===");
            System.out.println("值: " + multipleObj); // 应显示true
            System.out.println("类型: " + (multipleObj != null ? multipleObj.getClass() : "null")); // 应显示class java.lang.Boolean

//            // 安全转换（处理可能的类型或 null 问题）
//            if (multipleObj instanceof Boolean) {
//                returnMultiple = (Boolean) multipleObj;
//            }
            // 3. 强制转换（排除一切中间逻辑）
            if (multipleObj != null) {
                // 即使类型异常，也尝试转换（覆盖极端情况）
                returnMultiple = Boolean.parseBoolean(multipleObj.toString());
            }
            System.out.println("强制转换后returnMultiple: " + returnMultiple);
            System.out.println("Search type: " + searchType);
            System.out.println("Multiple requested: " + returnMultiple);
            System.out.println("Returning: " + (returnMultiple ? "multiple paths" : "single path"));

            if (startStationId == null || endStationId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "起点和终点不能为空"));
            }

            if (startStationId.equals(endStationId)) {
                return ResponseEntity.badRequest().body(Map.of("error", "起点和终点不能相同"));
            }

            if ("leastTransfer".equals(searchType) && returnMultiple) {
                // 返回多路径结果
                List<PathResult> results = pathService.findLeastTransferPaths(
                        startStationId, endStationId, 1.5); // 1.5倍时间容忍度
                return ResponseEntity.ok(results);
            } else {
                // 单路径查询
                PathResult result;
                if ("leastTransfer".equals(searchType)) {
                    result = pathService.findLeastTransferPath(startStationId, endStationId);
                } else {
                    result = pathService.findShortestPath(startStationId, endStationId);
                }
                return ResponseEntity.ok(result);
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "路径查询失败"));
        }
    }

    /**
     * 计算两个站点之间的距离
     * GET /api/routes/distance?station1=1&station2=2
     */
    @GetMapping("/distance")
    public ResponseEntity<?> calculateDistance(@RequestParam Integer station1,
                                               @RequestParam Integer station2) {
        try {
            com.example.underground_api.entity.Station s1 = stationRepository.findById(station1)
                    .orElseThrow(() -> new RuntimeException("站点1不存在"));
            com.example.underground_api.entity.Station s2 = stationRepository.findById(station2)
                    .orElseThrow(() -> new RuntimeException("站点2不存在"));

            double distance = pathService.calculateDistance(s1, s2);
            return ResponseEntity.ok(Map.of("distance", distance));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}