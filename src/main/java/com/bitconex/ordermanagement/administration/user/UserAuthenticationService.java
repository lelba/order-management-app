package com.bitconex.ordermanagement.administration.user;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Scanner;

@Service
public class UserAuthenticationService {

    private final UserRepository userRepository;

    public UserAuthenticationService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User logInUser() {

        User user = new User();
        Optional<User> optionalUser;
        int i = 0;
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        do {
            System.out.println("Enter username: ");
            String userName;
            Scanner s = new Scanner(System.in);
            userName = s.next();

            System.out.println("Enter password: ");
            String password;
            Scanner t = new Scanner(System.in);
            password = t.next();

            user.setUserName(userName);
            user.setPassword(password);

            optionalUser = userRepository.findUserByUserName(userName);

            if (optionalUser.isPresent() && passwordEncoder.matches(password, optionalUser.get().getPassword())) {
                i = 1;
            }
        } while (i == 0);

        return optionalUser.get();
    }
}
