package com.dakartechwallet.walletbackend.mapper;

import com.dakartechwallet.walletbackend.dto.UserResponse;
import com.dakartechwallet.walletbackend.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public UserResponse toUserResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getTelephone(),
                user.getEmail(),
                user.isTelephoneVerifie(),
                user.isEmailVerifie(),
                user.getStatut().name()
        );
    }
}