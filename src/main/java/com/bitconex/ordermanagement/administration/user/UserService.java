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
    private final AddressRepository addressRepository;

    @Autowired
    public UserService(UserRepository userRepository, ObjectMapper objectMapper, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
        this.addressRepository = addressRepository;
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<Object> getUsers() {
        List<User> users = userRepository.findAll();
        List<Object> userDtos = new ArrayList<>();

        for(User user : users) {
            if(UserRole.ADMIN.equals(user.getRole())) {
                AdminDTO adminDTO = convertToAdminDTO(user);
                userDtos.add(adminDTO);
            } else {
                CustomerDTO customerDTO = convertToCustomerDTO(user);
                userDtos.add(customerDTO);
            }
        }
        return userDtos;
    }


    public void addNewUser(User user) {
        Optional<User> appUserOptional = userRepository.findUserByUserName(user.getUserName());
        if (appUserOptional.isPresent()) {
            throw new IllegalStateException("Username taken!");
        }
        if(user.getRole().equals(UserRole.CUSTOMER)){
            Address address = new Address();
            addressRepository.save(address);
        } //provjeriti ?????
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
        List<Object> userDTOs = getUsers();
        try {
            String jsonUsers = objectMapper.writeValueAsString(userDTOs);
            System.out.println(jsonUsers);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserName(user.getUserName());
        userDTO.setId(user.getId());
        userDTO.setEmail(user.getEmail());
        return userDTO;
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

