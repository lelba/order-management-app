package com.bitconex.ordermanagement.administration.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Transactional
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
        List<Object> userDTOs = new ArrayList<>();

        for (User user : users) {
            if (user.getRole() == UserRole.CUSTOMER) {
                CustomerDTO customerDTO = convertToCustomerDTO(user);
                userDTOs.add(customerDTO);
            } else if (user.getRole() == UserRole.ADMIN) {
                AdminDTO adminDTO = convertToAdminDTO(user);
                userDTOs.add(adminDTO);
            }
        }

        try {
            String jsonUsers = objectMapper.writeValueAsString(userDTOs);
            System.out.println(jsonUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AdminDTO convertToAdminDTO(User user) {
        AdminDTO adminDTO = new AdminDTO();
        adminDTO.setId(user.getId());
        adminDTO.setUserName(user.getUserName());
        adminDTO.setEmail(user.getEmail());
        return adminDTO;
    }

    public CustomerDTO convertToCustomerDTO(User user) {
        CustomerDTO customerDTO = new CustomerDTO();
        customerDTO.setId(user.getId());
        customerDTO.setUserName(user.getUserName());
        customerDTO.setEmail(user.getEmail());
        customerDTO.setName(user.getName());
        customerDTO.setSurname(user.getSurname());
        customerDTO.setDateOfBirth(user.getDateOfBirth());
        customerDTO.setAddressDTO(convertToAddressDTO(user.getAddress()));
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

