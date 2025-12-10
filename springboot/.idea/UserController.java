package com.example.underground_api.controller;

import com.example.underground_api.config.JwtUtil;
import com.example.underground_api.entity.User;
import com.example.underground_api.entity.UserRole;
import com.example.underground_api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetailsService;//Spring Security 提供的用户信息接口，定义了用户的核心信息（用户名、密码、权限等）
import org.springframework.security.core.userdetails.UserDetails;//Spring Security 提供的接口，用于 “根据用户名加载用户信息”（核心方法 loadUserByUsername）。
import org.springframework.web.bind.annotation.*;
import com.example.underground_api.dto.LoginRequestDTO;

import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 * 提供用户相关的REST API接口
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    // Spring Security 的UserDetailsService（用于加载用户信息生成 token）
    @Autowired
    @Qualifier("customUserDetailsService")
    private UserDetailsService userDetailsService;
    /**
     * 用户注册
     * POST /api/users/register
     */
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody User user) {
        try {
            User registeredUser = userService.registerNewUser(user);
            return ResponseEntity.ok(registeredUser);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * 用户登录
     * POST /api/users/login
     */
//    @PostMapping("/login")
//    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequest) {
//        String username = loginRequest.getUsername();
//        String password = loginRequest.getPassword();
//
//        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
//            return ResponseEntity.badRequest().body(Map.of("error", "用户名和密码不能为空"));
//        }
//        //验证密码
//        boolean isValid = userService.validateUserCredentials(username, password);
//        if (isValid) {
//            // 加载用户信息（用于生成token）
//            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
//            // 生成JWT令牌
//            String token = jwtUtil.generateToken(userDetails);
//            // 返回token、用户名等信息
//            return ResponseEntity.ok(Map.of("message", "登录成功", "username", username, "token", token));
//        } else {
//            return ResponseEntity.status(401).body(Map.of("error", "用户名或密码错误"));
//        }
//    }
    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequestDTO loginRequest) {
        System.out.println("=== 登录调试 ===");

        try {
            boolean isValid = userService.validateUserCredentials(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
            );

            if (isValid) {
                System.out.println("密码验证成功");

                UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername());
                System.out.println("用户详情: " + userDetails.getUsername());
                System.out.println("权限: " + userDetails.getAuthorities());

                // 测试生成token
                String token = jwtUtil.generateToken(userDetails);
                System.out.println("生成的Token: " + token);

                // 测试验证token
                boolean tokenValid = jwtUtil.validateToken(token, userDetails);
                System.out.println("Token验证结果: " + tokenValid);

                return ResponseEntity.ok(Map.of(
                        "message", "登录成功",
                        "username", loginRequest.getUsername(),
                        "token", token
                ));
            } else {
                return ResponseEntity.status(401).body(Map.of("error", "用户名或密码错误"));
            }
        } catch (Exception e) {
            System.out.println("登录过程错误: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(Map.of("error", "服务器错误: " + e.getMessage()));
        }
    }

    /**
     * 获取所有用户列表
     * GET /api/users
     */
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * 根据ID获取用户
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> getUserById(@PathVariable Integer id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping("/username/{username}")
    public ResponseEntity<?> getUserByUsername(@PathVariable String username) {
        try {
            User user = userService.getUserByUsername(username)
                    .orElseThrow(() -> new RuntimeException("用户不存在"));
            return ResponseEntity.ok(user);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 更新用户信息
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        try {
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 修改用户密码
     * PATCH /api/users/{id}/password
     */
    @PatchMapping("/{id}/password")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<?> changePassword(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        String newPassword = request.get("newPassword");
        if (newPassword == null || newPassword.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "新密码不能为空"));
        }

        try {
            User updatedUser = userService.changeUserPassword(id, newPassword);
            return ResponseEntity.ok(updatedUser);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据角色查询用户
     * GET /api/users/role/{role}
     */
    @GetMapping("/role/{role}")
    @PreAuthorize("hasAuthority('admin')")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable UserRole role) {
        List<User> users = userService.getUsersByRole(role);
        return ResponseEntity.ok(users);
    }

    // 获取包含收藏路线的用户信息

    // 🌟 接口路径：/api/users/{id}/with-favorites（RESTful 风格，语义清晰）
    // 作用：根据用户id查询带收藏路线的用户信息
    @GetMapping("/{id}/with-favorites")
    public ResponseEntity<?> getUserWithFavoriteRoutes(@PathVariable Integer id) {
        // 调用 Service 中修改后的方法：getUserWithFavoriteRoutes
        return userService.getUserWithFavoriteRoutes(id)
                .map(ResponseEntity::ok) // 存在则返回 200 + 数据
                .orElse(ResponseEntity.notFound().build()); // 不存在则返回 404
    }

    // 🌟 接口路径：/api/users/all/with-favorites
    // 作用：查询所有带收藏路线的用户
    @GetMapping("/all/with-favorites")
    public ResponseEntity<List<User>> getAllUsersWithFavoriteRoutes() {
        List<User> users = userService.getAllUsersWithFavoriteRoutes();
        return ResponseEntity.ok(users); // 返回 200 + 列表数据
    }

}