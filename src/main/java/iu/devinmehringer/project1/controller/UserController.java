package iu.devinmehringer.project1.controller;


import iu.devinmehringer.project1.dto.user.NotificationSettingsRequest;
import iu.devinmehringer.project1.dto.user.UserResponse;
import iu.devinmehringer.project1.mapper.UserMapper;
import iu.devinmehringer.project1.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired

    private final UserService userService;
    private final UserMapper userMapper;

    public UserController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
        return userService.getUser(id)
                .map(user -> ResponseEntity.ok(userMapper.toDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/notifications")
    public ResponseEntity<UserResponse> setNotifications(@PathVariable Long id,
                                                         @RequestBody NotificationSettingsRequest request) {
        return ResponseEntity.ok(userMapper.toDTO(userService.updateNotificationSettings(id, request)));
    }

}
