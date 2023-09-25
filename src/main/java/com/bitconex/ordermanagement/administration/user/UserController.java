package com.bitconex.ordermanagement.administration.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping()
    public ResponseEntity<List<Object>> getUsers() {
        return ResponseEntity.ok(userService.getUsers());
    }

    @PostMapping("/add")
    public void addNewUser(@RequestBody User user) {
        userService.addNewUser(user);
    }

    @DeleteMapping("/{userName}")
    public void deleteUser(@PathVariable String userName) {
        userService.deleteUser(userName);
    }

}