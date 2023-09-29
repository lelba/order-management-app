package com.bitconex.ordermanagement;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.product.ProductRepository;
import com.bitconex.ordermanagement.administration.product.ProductService;
import com.bitconex.ordermanagement.administration.user.*;
import com.bitconex.ordermanagement.orderingprocess.order.Order;
import com.bitconex.ordermanagement.orderingprocess.order.OrderExportService;
import com.bitconex.ordermanagement.orderingprocess.order.OrderRepository;
import com.bitconex.ordermanagement.orderingprocess.order.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;


@SpringBootApplication
public class OrderManagementApplication implements CommandLineRunner {

    public UserRepository userRepository;
	public UserService userService;
	public ProductService productService;
	public OrderService orderService;
	public OrderExportService orderExportService;
	public UserAuthenticationService userAuthenticationService;
	public ProductRepository productRepository;
	public OrderRepository orderRepository;

	@Autowired
	public OrderManagementApplication(OrderRepository orderRepository, ProductRepository productRepository, UserRepository userRepository, UserService userService, ProductService productService, OrderService orderService, OrderExportService orderExportService, UserAuthenticationService userAuthenticationService) {
		this.orderRepository = orderRepository;
		this.productRepository = productRepository;
		this.userRepository = userRepository;
		this.userService = userService;
		this.productService = productService;
		this.orderService = orderService;
		this.orderExportService = orderExportService;
		this.userAuthenticationService = userAuthenticationService;
	}

	private static final Logger LOG = LoggerFactory.getLogger(OrderManagementApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OrderManagementApplication.class, args);
	}

	@Override
	public void run(String... args) {
		LOG.info("STARTING: Order Management Application!");

		System.out.println("Welcome to Order Management Application!");
		User user = userAuthenticationService.logInUser();
		LOG.info("User successfully logged in: {}", user.getUsername());

		if(user.getRole().equals(UserRole.ADMIN)){
			adminMenu();
		}
		else {
			customerMenu(user);
		}
	}

	private void adminMenu() {
		LOG.info("Admin user logged in.");
		while (true) {
			System.out.println("Choose an option: ");
			System.out.println("1. User Administration");
			System.out.println("2. Product Catalog");
			System.out.println("3. List of all orders");
			System.out.println("0. Exit");

			Scanner scanner = new Scanner(System.in);
			if (scanner.hasNextInt()) {
				int choice = scanner.nextInt();
				switch (choice) {
					case 1 -> userAdministration();
					case 2 -> productCatalog();
					case 3 -> listOfAllOrders();
					case 0 -> {
						LOG.info("Exiting...");
						return;
					}
					default -> System.out.println("Invalid selection. Please select again.");
				}
			} else {
				System.out.println("Invalid input. Please enter a valid integer.");
			}
		}
	}

	private void customerMenu(User user) {
		LOG.info("Customer user logged in.");
		while (true) {
			System.out.println("Choose an option: ");
			System.out.println("1. List of orders for customer");
			System.out.println("2. Start new order");
			System.out.println("0. Exit");

			Scanner scanner = new Scanner(System.in);
			if (scanner.hasNextInt()) {
				int choice = scanner.nextInt();
				switch (choice) {
					case 1 -> listOfAllOrdersForCustomer(user);
					case 2 -> startNewOrder(user);
					case 0 -> {
						LOG.info("Exiting...");
						return;
					}
					default -> System.out.println("Invalid selection. Please select again.");
				}
			} else {
				System.out.println("Invalid input. Please enter a valid integer.");
			}
		}
	}

	private void startNewOrder(User user) {
		try {
			List<Product> availableProducts = productRepository.findAllByActiveIsTrueAndValidToIsAfter(new Date());
			List<Product> orderedProducts = new ArrayList<>();
			Scanner scanner = new Scanner(System.in);
			for (Product product : availableProducts) {
				System.out.printf("Add product '%s' to the order? (y/n): ", product.getName());
				System.out.printf("Price: '%s': ", product.getPrice());
				String choice = scanner.next();
				if ("y".equalsIgnoreCase(choice)) {
					orderedProducts.add(product);
				}
			}
			Order order = orderService.createOrder(user, orderedProducts);
			System.out.println("Do you want to confirm the order? (y/n): ");
			String choice = scanner();
			if(choice.equals("y")) {
				orderRepository.save(order);
				System.out.println("Order confirmed and saved!");
				orderService.printNewOrder(order);
			}
			else {
				System.out.println("Order cancelled!");
			}
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	private void listOfAllOrdersForCustomer(User user) {
		try {
			orderService.printAllOrdersForCustomer(user);
		} catch(Exception e) {
			LOG.error("Exception: "+ e.getMessage(), e);
		}
	}

	private void listOfAllOrders() {
		System.out.println("Please enter directory path: ");
		String directoryPath = scanner();
		System.out.println("Please enter file name: ");
		String fileName = scanner();
		try {
			orderExportService.exportOrdersToCsv(directoryPath, fileName);
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	private void productCatalog() {
		while(true) {
			System.out.println("Choose an option: ");
			System.out.println("1. Add new product");
			System.out.println("2. List of all products");
			System.out.println("3. Delete product");
			System.out.println("0. Exit");

			Scanner s = new Scanner(System.in);
			if (s.hasNextInt()) {
				int choice = s.nextInt();
				switch (choice) {
					case 1 -> addNewProduct();
					case 2 -> listOfAllProducts();
					case 3 -> deleteProduct();
					case 0 -> {
						System.out.println("Exiting...");
						return;
					}
					default -> System.out.println("Invalid selection. Please select again.");
				}
			} else {
				System.out.println("Invalid input. Please enter a valid integer.");
			}
		}
	}

	private void deleteProduct() {
		System.out.println("Please enter name of product: ");
		String name = scanner();
		try {
			productService.deleteProductByName_setNotActive(name);
			LOG.info("Product successfully deleted.");
		} catch (IllegalStateException e) {
			System.out.println("There is no product with that name!");
		}

	}
	private void listOfAllProducts() {
		productService.printAllProducts();
	}

	private void addNewProduct() {
		Product product = new Product();
		System.out.println("Please enter name of product: ");
		String name = scanner();
		product.setName(name);
		Scanner s;
		do {
			System.out.println("Please enter price: ");
			s = new Scanner(System.in);
		} while(!s.hasNextDouble());
		double price = s.nextDouble();
		product.setPrice(price);
		do {
			System.out.println("Please enter valid from (yyyy-MM-dd): ");
			String dateString = scanner();
			try {
				Date date = scanToDate(dateString);
				product.setValidFrom(date);
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
		} while(product.getValidFrom()==null);
		do {
			System.out.println("Please enter valid to (yyyy-MM-dd): ");
			String dateString = scanner();
			try {
				Date date = scanToDate(dateString);
				product.setValidTo(date);
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
		} while(product.getValidTo()==null);
		int quantity = 0;
		do {
			System.out.println("Please enter quantity: ");
			Scanner scan = new Scanner(System.in);
			if (scan.hasNextInt()) {
				quantity = scan.nextInt();
			}
		} while(quantity == 0);
		product.setQuantity(quantity);
		try {
			productService.addNewProduct(product);
		} catch(Exception e) {
			LOG.error(e.getMessage());
		}

	}

	private void userAdministration() {
		while(true) {
			System.out.println("Choose an option: ");
			System.out.println("1. Add new user");
			System.out.println("2. Delete existing user");
			System.out.println("3. List of all users");
			System.out.println("0. Exit");

			Scanner s = new Scanner(System.in);
			if (s.hasNextInt()) {
				int choice = s.nextInt();
				switch (choice) {
					case 1 -> addNewUser();
					case 2 -> deleteExistingUser();
					case 3 -> listOfAllUsers();
					case 0 -> {
						System.out.println("Exiting...");
						return;
					}
					default -> System.out.println("Invalid selection. Please select again.");
				}
			} else {
				System.out.println("Invalid input. Please enter a valid integer.");
			}
		}
	}

	public void addNewUser(){
		System.out.println("Choose an option: ");
		System.out.println("1. Add Admin user");
		System.out.println("2. Add Customer user");
		System.out.println("0. Exit");
		Scanner s = new Scanner(System.in);
		if (s.hasNextInt()) {
			int choice = s.nextInt();
			switch (choice) {
				case 1 -> {
					User auser = addUser();
					addNewAdmin(auser);
				}
				case 2 -> {
					User cuser = addUser();
					addNewCustomer(cuser);
				}
				case 0 -> System.out.println("Exiting...");
				default -> System.out.println("Invalid selection. Please select again.");
			}
		} else {
			System.out.println("Invalid input. Please enter a valid integer.");
		}
	}

	private User addUser() {
		User user = new User();
		System.out.println("Please enter username: ");
		String userName = scanner();
		user.setUserName(userName);
		System.out.println("Please enter password: ");
		String password = scanner();
		user.setPassword(password);
		System.out.println("Please enter email: ");
		String email = scanner();
		user.setEmail(email);
		return user;
	}

	private void addNewCustomer(User user) {
		user.setRole(UserRole.CUSTOMER);
		System.out.println("Please enter name: ");
		String name = scanner();
		user.setName(name);
		System.out.println("Please enter surname: ");
		String surname = scanner();
		user.setSurname(surname);
		do {
			System.out.println("Please enter date of birth (yyyy-MM-dd): ");
			String dateString = scanner();
			try {
				Date date = scanToDate(dateString);
				user.setDateOfBirth(date);
			} catch (RuntimeException e) {
				System.out.println(e.getMessage());
			}
		} while(user.getDateOfBirth()==null);
		System.out.println("Please enter address: ");
		Address address = addAddress();
		user.setAddress(address);
		try {
			userService.addNewUser(user);
			System.out.println("User successfully added!");
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
	}

	private Address addAddress() {
		Address address = new Address();
		System.out.println("Please enter street: ");
		String street = scanner();
		address.setStreet(street);
		System.out.println("Please enter house number: ");
		Scanner s = new Scanner(System.in);
		Long number = s.nextLong();
		address.setHouseNumber(number);
		System.out.println("Please enter place: ");
		String place = scanner();
		address.setPlace(place);
		System.out.println("Please enter country: ");
		String country = scanner();
		address.setCountry(country);

		return address;
	}


	private void addNewAdmin(User user) {
		user.setRole(UserRole.ADMIN);
		try {
			userService.addNewUser(user);
			System.out.println("User successfully added!");
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}
	}


	public void deleteExistingUser() {
		System.out.println("DELETING USER... Please enter username: ");
		String userName = scanner();
		try {
			userService.deleteUser(userName);
			System.out.println("User successfully deleted!");
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}

	}

	public void listOfAllUsers(){
		try {
			userService.printAllUsersInJsonFormat();
		} catch (Exception e) {
			LOG.error(e.getMessage());
		}
	}

	public String scanner() {
		Scanner s = new Scanner(System.in);
		return s.nextLine();
	}
	Date scanToDate(String input) {
		try (Scanner scanner = new Scanner(input)) {
			String dateString = scanner.next();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Incorrect date format!");
		}
	}
}
