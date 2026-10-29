package com.nak.demo.controller;

import com.nak.demo.dto.base.Response;
import com.nak.demo.dto.user.ChangePasswordDto;
import com.nak.demo.dto.user.UpdateUserDto;
import com.nak.demo.dto.user.UserDto;
import com.nak.demo.dto.user.UserResponseDto;
import com.nak.demo.entity.User;
import com.nak.demo.service.UserService;
import com.nak.demo.service.security.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private AuthService authService;

    // ===== For the logged-in user (any role) =====

    @GetMapping("/me")
    public ResponseEntity<Response> me(@AuthenticationPrincipal User currentUser) {
        UserResponseDto dto = userService.getUser(currentUser.getId());
        return ResponseEntity.ok(Response.success("200", "success", "successfully retrieved profile", dto));
    }

    @PatchMapping("/me/password")
    public ResponseEntity<Response> changeMyPassword(@AuthenticationPrincipal User currentUser,
                                                     @Valid @RequestBody ChangePasswordDto dto) {
        authService.changePassword(dto, currentUser.getId());
        return ResponseEntity.ok(Response.success("200", "success",
                "successfully changed password, please login again on your other devices"));
    }

    // ===== Admin only =====

    @GetMapping()
    public ResponseEntity<Response> listUsers() {
        List<UserResponseDto> users = userService.listUser();
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully retrieved user",users));
    }


    @GetMapping("/{user_id}")
    public ResponseEntity<Response> getUser(@PathVariable("user_id") Long userId){
        UserResponseDto user = userService.getUser(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully retrieved user",user));
    }

    @PutMapping("/{user_id}")
    public ResponseEntity<Response> updateUser(@PathVariable("user_id") Long userId,
                                               @Valid @RequestBody UpdateUserDto payload) {
        userService.updateUser(payload, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully updated user",userId));
    }

    @DeleteMapping("/{user_id}")
    public ResponseEntity<Response> deleteUser(@PathVariable("user_id") Long userId){
        userService.deleteUser(userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully deleted user",userId));
    }

    @PatchMapping("/{user_id}/change-password")
    public ResponseEntity<Response> changePassword(@PathVariable("user_id") Long userId,
                                                   @Valid @RequestBody ChangePasswordDto dto){
        authService.changePassword(dto, userId);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully changed password"));
    }

    @GetMapping("/search")
    public ResponseEntity<Response> findUser(@RequestParam(value = "name", required = false) String name){
        List<UserResponseDto> user = userService.findUser(name);
        return ResponseEntity.status(HttpStatus.OK)
                .body(Response.success("200","success","successfully retrieved user",user));
    }
}