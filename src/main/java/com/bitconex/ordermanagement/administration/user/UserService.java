package com.bitconex.ordermanagement.administration.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


//@Transactional
@Service
public class UserService {

    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    private final PasswordEncoder passwordEncoder;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    public UserService(UserRepository userRepository, ObjectMapper objectMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.passwordEncoder = passwordEncoder;
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<Object> getUsers() {
        List<User> users = userRepository.findAllByActiveIsTrue();
        List<Object> usersDto = new ArrayList<>();

        for(User user : users) {
            if(UserRole.ADMIN.equals(user.getRole())) {
                AdminDTO adminDTO = convertToAdminDTO(user);
                usersDto.add(adminDTO);
            } else {
                CustomerDTO customerDTO = convertToCustomerDTO(user);
                usersDto.add(customerDTO);
            }
        }
        return usersDto;
    }


    public void addNewUser(User user) {
        Optional<User> appUserOptional = userRepository.findUserByUserName(user.getUsername());
        if (appUserOptional.isPresent()) {
            throw new IllegalStateException("Username taken!");
        }
        String encodedPw = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPw);
        user.setActive(true);
        userRepository.save(user);
    }


    public void deleteUser(String userName) {
        User user = userRepository.findUserByUserName(userName).orElse(null);
        if (user == null) {
            throw new IllegalStateException("There is no user with that name!");
        } else {
          user.setActive(false);
          userRepository.save(user);
        }
    }

    public void printAllUsersInJsonFormat() {
        List<Object> userDTOs = getUsers();
        try {
            String jsonUsers = objectMapper.writeValueAsString(userDTOs);
            System.out.println(jsonUsers);
        } catch (Exception e) {
           logger.error("Error converting to JSON format!", e);
        }
    }

    public AdminDTO convertToAdminDTO(User user) {
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(user.getId());
        adminDTO.setUsername(user.getUsername());
        adminDTO.setEmail(user.getEmail());
        adminDTO.setRole(UserRole.ADMIN);
        return adminDTO;
    }

    public CustomerDTO convertToCustomerDTO(User user) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(user.getId());
        customerDTO.setUsername(user.getUsername());
        customerDTO.setEmail(user.getEmail());
        customerDTO.setRole(UserRole.CUSTOMER);
        customerDTO.setName(user.getName());
        customerDTO.setSurname(user.getSurname());
        customerDTO.setDateOfBirth(user.getDateOfBirth());
        customerDTO.setAddress(convertToAddressDTO(user.getAddress()));
        return customerDTO;
    }

    public AddressDTO convertToAddressDTO(Address address) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setAddressId(address.getAddressId());
        addressDTO.setStreet(address.getStreet());
        addressDTO.setHouseNumber(address.getHouseNumber());
        addressDTO.setPlace(address.getPlace());
        addressDTO.setCountry(address.getCountry());
        return addressDTO;
    }

}

