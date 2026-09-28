package com.nawaf.meetingpoint.Controller;

import com.nawaf.meetingpoint.Api.ApiResponse;
import com.nawaf.meetingpoint.Model.User;
import com.nawaf.meetingpoint.Service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/get-all")
    public ResponseEntity<?> getUsers() {
        return ResponseEntity.status(200).body(userService.getUsers());
    }

    @PostMapping("/add")
    public ResponseEntity<?> createUser(@Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) return ResponseEntity.status(400).body(new ApiResponse(errors.getFieldError().getDefaultMessage()));

        int createCase = userService.createUser(user);

        return switch (createCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("Email already exists"));
            default -> ResponseEntity.status(201).body(new ApiResponse("Created new user successfully"));
        };
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @Valid @RequestBody User user, Errors errors) {
        if (errors.hasErrors()) return ResponseEntity.status(400).body(new ApiResponse(errors.getFieldError().getDefaultMessage()));

        int updateCase = userService.updateUser(id, user);

        return switch (updateCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("User not found"));
            case 2 -> ResponseEntity.status(400).body(new ApiResponse("Email already exists"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Updated user successfully"));
        };
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        int deleteCase = userService.deleteUser(id);

        return switch (deleteCase) {
            case 1 -> ResponseEntity.status(400).body(new ApiResponse("User not found"));
            default -> ResponseEntity.status(200).body(new ApiResponse("Deleted user successfully"));
        };
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUser(@PathVariable Integer id) {
        User user = userService.getUser(id);

        if (user == null) return ResponseEntity.status(400).body(new ApiResponse("User not found"));

        return ResponseEntity.status(200).body(user);
    }
}