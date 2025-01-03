package com.example.blog.service.user;

import com.example.blog.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserEntity register(String username, String rawpassword) {
        var encodePassword = passwordEncoder.encode(rawpassword);
        var newUser = new UserEntity(null, username, encodePassword, true);
        userRepository.insert(newUser);
        return newUser;
    }

    @Transactional
    public void delete(String username) {
        userRepository.deleteByUsername(username);
    }

    @Transactional(readOnly = true)
    public boolean existsUsername(String username) {
        return userRepository.selectByUsername(username).isPresent();
    }
}
