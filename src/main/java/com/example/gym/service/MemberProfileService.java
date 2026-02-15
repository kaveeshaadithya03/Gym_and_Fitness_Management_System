package com.example.gym.service;

import com.example.gym.model.MemberProfile;
import com.example.gym.model.User;
import com.example.gym.repository.MemberProfileRepository;
import com.example.gym.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberProfileService {

    private final MemberProfileRepository memberProfileRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public MemberProfile getMemberByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return memberProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Member profile not found"));
    }
}