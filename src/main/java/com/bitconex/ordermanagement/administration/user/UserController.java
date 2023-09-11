package com.bitconex.ordermanagement.administration.user;

import org.springframework.beans.factory.annotation.Autowired;
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

    @GetMapping("/getAllUsers")
    public List<Object> getUsers() {
        return userService.getUsers();
    }

    @PostMapping("/addNewUser")
    public void addNewUser(@RequestBody User user) {
        userService.addNewUser(user);
    }

    @DeleteMapping("/deleteUser/{userName}")  //brisanje korisika koji nema narudzbu
    public void deleteUser(@PathVariable String userName) {
        userService.deleteUser(userName);
    }

}