package com.nak.demo.service;

import com.nak.demo.entity.User;
import com.nak.demo.dto.user.UpdateUserDto;
import com.nak.demo.exception.model.ResourceNotFoundException;
import com.nak.demo.repository.UserRepository;

import com.nak.demo.dto.user.UserResponseDto;
import com.nak.demo.mapper.UserMapper;
import com.nak.demo.exception.model.DuplicateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserMapper mapper;


    public List<UserResponseDto> listUser() {
        List<User> users = userRepository.findAll();
        return mapper.toDtoList(users);
    }

    public UserResponseDto getUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("user not found with id :" + userId));

        return mapper.toDto(user);

    }

    public void updateUser(UpdateUserDto payload, Long userId) {
        User existing = userRepository.findById(userId)
//IF USER NOT FOUND then response 404
                .orElseThrow(() ->
                        new ResourceNotFoundException("user not found with id :" + userId));

        String newName = payload.getName().trim();
        if (!newName.equalsIgnoreCase(existing.getName()) && userRepository.existsByNameIgnoreCase(newName)) {
            throw new DuplicateException("username already exists");
        }
        mapper.updateEntityFromDto(existing, payload);

        userRepository.save(existing);

    }

    public void deleteUser(Long userId) {

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("user not found with id :" + userId);
        }

        // user found , then delete
        userRepository.deleteById(userId);


    }

    public List<UserResponseDto> findUser(String name) {
        String formattedName = name != null ?
                name.toLowerCase()
                : name;
        List<User> user = userRepository.findUserWithFilters(formattedName);
        return mapper.toDtoList(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // log in with either username or email (email is case-insensitive)
        return userRepository.findByName(username)
                .or(() -> userRepository.findByEmailIgnoreCase(username))
                .orElseThrow(() -> new UsernameNotFoundException("user not found: " + username));
    }
}