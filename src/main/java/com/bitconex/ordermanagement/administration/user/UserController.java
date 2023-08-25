package com.bitconex.ordermanagement.administration.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public List<User> getUsers() {
        return userService.getUsers();
    }

    @PostMapping
    public void addNewUser(User user) {
        userService.addNewUser(user);
    }

    @DeleteMapping
    public void deleteUser(String userName) {
        userService.deleteUser(userName);
    }

}