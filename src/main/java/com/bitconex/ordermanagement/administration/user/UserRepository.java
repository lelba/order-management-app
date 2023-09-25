package com.bitconex.ordermanagement.administration.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT a FROM User a WHERE a.userName = ?1")
    Optional<User> findUserByUserName(String username);

    List<User> findAllByActiveIsTrue();



}
