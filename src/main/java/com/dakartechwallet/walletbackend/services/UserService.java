package com.dakartechwallet.walletbackend.services;

import com.dakartechwallet.walletbackend.dto.UserResponse;
import com.dakartechwallet.walletbackend.exception.ResourceNotFoundException;
import com.dakartechwallet.walletbackend.mapper.UserMapper;
import com.dakartechwallet.walletbackend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserResponse getProfil(String telephone) {
        return userRepository.findByTelephone(telephone)
                .map(userMapper::toUserResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur introuvable"));
    }
}