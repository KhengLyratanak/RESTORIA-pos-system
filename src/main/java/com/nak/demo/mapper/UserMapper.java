package com.nak.demo.mapper;

import com.nak.demo.common.enums.role;
import com.nak.demo.dto.auth.RegisterDto;
import com.nak.demo.entity.User;
import com.nak.demo.dto.user.UpdateUserDto;
import com.nak.demo.dto.user.UserDto;
import com.nak.demo.dto.user.UserResponseDto;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserMapper {
    public User toEntity(UserDto dto){
        User entity = new User();

        entity.setName(dto.getName().trim());
        entity.setPassword(dto.getPassword());
        entity.setRole(normaliseRole(dto.getRole()));
        entity.setAge(dto.getAge());
        entity.setAddress(dto.getAddress().trim());
        entity.setEmail(dto.getEmail().trim().toLowerCase());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    /** Self-registration: role is always USER, whatever the client sends. */
    public User toEntity(RegisterDto dto){
        User entity = new User();
        entity.setName(dto.getName().trim());
        entity.setEmail(dto.getEmail().trim().toLowerCase());
        entity.setPassword(dto.getPassword());
        entity.setAge(dto.getAge());
        entity.setAddress(dto.getAddress().trim());
        entity.setRole(role.USER.name());
        entity.setCreatedAt(LocalDateTime.now());
        return entity;
    }

    public static String normaliseRole(String value){
        return (value == null || value.isBlank()) ? role.USER.name() : value.trim().toUpperCase();
    }
    public void updateEntityFromDto(User entity, UpdateUserDto dto){
        if (entity == null || dto == null){
            return;
        }
        entity.setName(dto.getName());
        entity.setRole(dto.getRole());
        entity.setAddress(dto.getAddress());
    }
    public UserResponseDto toDto(User entity){
        UserResponseDto dto = new UserResponseDto();

        dto.setId(entity.getId());
        dto.setRole(entity.getRole());
        dto.setUserName(entity.getName());
        dto.setAge(entity.getAge() );
        dto.setEmail(entity.getEmail());
        dto.setAddress(entity.getAddress());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
            return dto;

    }
    public List<UserResponseDto> toDtoList(List<User> entities){
        if (entities  == null || entities.isEmpty()){
            return new ArrayList<>();
        }
        return entities.stream()
                .map(user -> this.toDto(user))
                .collect(Collectors.toList());
    }
    public void updateEntityChangePassword(User entity,String password){
        entity.setPassword(password);
    }
}
