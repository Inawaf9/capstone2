package com.nawaf.meetingpoint.Service;

import com.nawaf.meetingpoint.Api.ApiException;
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
    public void createUser(User user) {
        User existEmail = userRepository.findUserByEmail(user.getEmail());

        if (existEmail != null) throw new ApiException("Email already exists");

        userRepository.save(user);

        emailService.sendWelcomeEmail(user.getEmail(), user.getName());
    }

    // 0 = User updated successfully,
    // 1 = User not found,
    // 2 = Email already exists
    public void updateUser(Integer id, User user) {
        User foundUser = userRepository.findUserById(id);

        if (foundUser == null) throw new ApiException("User not found");

        User existEmail = userRepository.findUserByEmail(user.getEmail());

        if (existEmail != null && !existEmail.getId().equals(id)) throw new ApiException("Email already exists");

        foundUser.setName(user.getName());
        foundUser.setEmail(user.getEmail());

        userRepository.save(foundUser);
    }

    // 0 = User deleted successfully,
    // 1 = User not found
    public void deleteUser(Integer id) {
        User foundUser = userRepository.findUserById(id);

        if (foundUser == null) throw new ApiException("User not found");

        userRepository.delete(foundUser);
    }

    public User getUser(Integer id) {
        User foundUser = userRepository.findUserById(id);

        if (foundUser == null) throw new ApiException("User not found");

        return foundUser;
    }
}