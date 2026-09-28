package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Model.User;
import com.nawaf.meetingpoint.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final EmailService emailService;

    public List<User> getUsers() {
        return userRepository.findAll();
    }

    // 0 = User created successfully
    // 1 = Email already exists
    public int createUser(User user) {
        User existEmail = userRepository.findUserByEmail(user.getEmail());

        if (existEmail != null) return 1;

        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getName());

        return 0;
    }

    // 0 = User updated successfully,
    // 1 = User not found,
    // 2 = Email already exists
    public int updateUser(Integer id, User user) {
        User foundUser = userRepository.findUserById(id);

        if (foundUser == null) return 1;

        User existEmail = userRepository.findUserByEmail(user.getEmail());

        if (existEmail != null && !existEmail.getId().equals(id)) return 2;

        foundUser.setName(user.getName());
        foundUser.setEmail(user.getEmail());

        userRepository.save(foundUser);
        return 0;
    }

    // 0 = User deleted successfully,
    // 1 = User not found
    public int deleteUser(Integer id) {
        User foundUser = userRepository.findUserById(id);

        if (foundUser == null) return 1;

        userRepository.delete(foundUser);
        return 0;
    }

    public User getUser(Integer id) {
        return userRepository.findUserById(id);
    }
}