package com.bitconex.ordermanagement.administration.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserService(UserRepository userRepository, ObjectMapper objectMapper) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<User> getUsers() {
        return userRepository.findAll();
    }


    public void addNewUser(User user) {
        Optional<User> appUserOptional = userRepository.findUserByUserName(user.getUserName());
        if (appUserOptional.isPresent()) {
            throw new IllegalStateException("Username taken!");
        }
        userRepository.save(user);
    }

    public void deleteUser(String userName) {

        User user = userRepository.findUserByUserName(userName)
                .orElse(null);

        if (user == null) {
            throw new IllegalStateException("There is no user with that name!");
        } else {
            userRepository.delete(user);
        }
    }

    public void printAllUsersInJsonFormat() {
        List<User> users = userRepository.findAll();

        try {
            String jsonUsers = objectMapper.writeValueAsString(users);
            System.out.println(jsonUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

