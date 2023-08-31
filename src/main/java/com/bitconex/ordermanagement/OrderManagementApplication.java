package com.bitconex.ordermanagement;

import com.bitconex.ordermanagement.administration.product.Product;
import com.bitconex.ordermanagement.administration.product.ProductRepository;
import com.bitconex.ordermanagement.administration.product.ProductService;
import com.bitconex.ordermanagement.administration.user.*;
import com.bitconex.ordermanagement.orderingprocess.OrderRepository;
import com.bitconex.ordermanagement.orderingprocess.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.Scanner;



@SpringBootApplication
public class OrderManagementApplication implements CommandLineRunner {

	@Autowired
	public UserRepository userRepository;
	@Autowired
	public UserService userService;
	@Autowired
	public ProductService productService;
	@Autowired
	public ProductRepository productRepository;
	@Autowired
	public OrderService orderService;
	@Autowired
	public OrderRepository orderRepository;

	private static final Logger LOG = LoggerFactory.getLogger(OrderManagementApplication.class);

	public static void main(String[] args) {
		LOG.info("STARTING: Order Managemenet Application!");
		SpringApplication.run(OrderManagementApplication.class, args);
		LOG.info("STOPPED: Order Managemenet Application!");
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("EXECUTING: Order Managemenet Application!");
		User user = userLogIn();
		System.out.println("------------------------------------------------------------------------------------------");
		System.out.println("You have successfully logged in! Welcome " + user.getUserName());
		System.out.println("------------------------------------------------------------------------------------------");
		if(user.getRole().equals(UserRole.ADMIN)){
			// A D M I N I S T R A C I J A
			System.out.println("You are ADMIN...");
			System.out.println("STARTING: Administration!");
			while(true) {
				System.out.println("Choose an option: ");
				System.out.println("1. User-Administration");
				System.out.println("2. Product Catalog");
				System.out.println("3. List of all orders");
				System.out.println("0. Exit");

				Scanner s = new Scanner(System.in);
				if (s.hasNextInt()) {
					int choice = s.nextInt();
					switch (choice) {
						case 1 -> userAdministration();
						case 2 -> productCatalog();
						case 3 -> listOfAllOrders();
						case 0 -> System.out.println("Exiting...");  //fali return
						default -> System.out.println("Invalid selection. Please select again.");
					}
				} else {
					System.out.println("Invalid input. Please enter a valid integer.");
				}
			}

		}
		else {
			// N A R U DŽ B E N I  P R O C E S
			System.out.println("You are CUSTOMER...");
			System.out.println("STARTING: Ordering process!");
			while(true) {
				System.out.println("Choose an option: ");
				System.out.println("1. List of orders for customer ");
				System.out.println("2. Start new order ");
				System.out.println("0. Exit");

				Scanner s = new Scanner(System.in);
				if (s.hasNextInt()) {
					int choice = s.nextInt();
					switch (choice) {
						case 1:
							listOfAllOrdersForCustomer(user);
							break;
						case 2:
							startNewOrder(user);
							break;
						case 0:
							System.out.println("Exiting...");
							return;
						default:
							System.out.println("Invalid selection. Please select again.");
					}
				} else {
					System.out.println("Invalid input. Please enter a valid integer.");
				}
			}
		}
	}

	private void startNewOrder(User user) {
		orderService.addNewOrder(user);
	}

	private void listOfAllOrdersForCustomer(User user) {
		orderService.printAllOrdersForCustomer(user);
	}

	private void listOfAllOrders() {
		orderService.printOrdersInCSVFormat();
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
					case 1:
						addNewProduct();
						break;
					case 2:
						listOfAllProducts();
						break;
					case 3:
						deleteProduct();
						break;
					case 0:
						System.out.println("Exiting...");
						return;
					default:
						System.out.println("Invalid selection. Please select again.");
				}
			} else {
				System.out.println("Invalid input. Please enter a valid integer.");
			}
		}
	}

	private void deleteProduct() {
		while(true) {
			System.out.println("Choose an option: ");
			System.out.println("1. Delete product by name: ");
			System.out.println("2. Delete all products that are out of stock: ");
			System.out.println("3. Delete all products that are no longer available: ");
			System.out.println("0. Exit");

			Scanner s = new Scanner(System.in);
			if (s.hasNextInt()) {
				int choice = s.nextInt();
				switch (choice) {
					case 1:
						deleteProductByName();
						break;
					case 2:
						deleteProductsOutOfStock();
						break;
					case 3:
						deleteNoAvailableProducts();
						break;
					case 0:
						System.out.println("Exiting...");
						return;
					default:
						System.out.println("Invalid selection. Please select again.");
				}
			} else {
				System.out.println("Invalid input. Please enter a valid integer.");
			}
		}
	}
	private void deleteNoAvailableProducts() {

		productService.deleteProductsNoLongerAvailable();
		System.out.println("Deleted products that are no longer available!");
	}

	private void deleteProductsOutOfStock() {
		productService.deleteProductsOutOfStock();
		System.out.println("Deleted products that are out of stock!");
	}

	private void deleteProductByName() {
		System.out.println("Please enter name of product: ");
		String name = scanner();
		productService.deleteProductByName(name);
	}
	private void listOfAllProducts() {
		productService.printAllProducts();
	}

	private void addNewProduct() {
		Product product = new Product();
		System.out.println("Please enter name of product: ");
		String name = scanner();
		product.setName(name);
		Double price = null;
		Scanner s;
		do {
			System.out.println("Please enter price: ");
			s = new Scanner(System.in);
		} while(!s.hasNextDouble());
		price = s.nextDouble();
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
		productService.addNewProduct(product);
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
					case 1:
						addNewUser();
						break;
					case 2:
						deleteExistingUser();
						break;
					case 3:
						listOfAllUsers();
						break;
					case 0:
						System.out.println("Exiting...");
						return;
					default:
						System.out.println("Invalid selection. Please select again.");
				}
			} else {
				System.out.println("Invalid input. Please enter a valid integer.");
			}
		}
	}

	public User userLogIn() {
		System.out.println("Welcome to Order Management Application!");
		User user = new User();
		Optional<User> optionalUser;
		int i = 0;
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
			if(optionalUser.isPresent() && password.equals(optionalUser.get().getPassword())) i=i+1;
		} while (i==0);
		return optionalUser.get();
	}


	public void addNewUser(){
		System.out.println("ADDING NEW USER: Select 1 to add ADMIN_user or 2 to add CUSTOMER_user. If you want to exit select 0:");
		Scanner s = new Scanner(System.in);
		if (s.hasNextInt()) {
			int choice = s.nextInt();
			switch (choice) {
				case 1:
					User auser = addUser();
					addNewAdmin(auser);
					break;
				case 2:
					User cuser = addUser();
					addNewCustomer(cuser);
					break;
				case 0:
					System.out.println("Exiting...");
					return;
				default:
					System.out.println("Invalid selection. Please select again.");
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

	Date scanToDate(String input) {
		try (Scanner scanner = new Scanner(input)) {
			String dateString = scanner.next();
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			return formatter.parse(dateString);
		} catch (ParseException e) {
			throw new RuntimeException("Incorrect date format!");
		}
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
		System.out.println("DELETING USER: Please enter username: ");
		String userName = scanner();
		try {
			userService.deleteUser(userName);
			System.out.println("User successfully deleted!");
		} catch (IllegalStateException e) {
			System.out.println(e.getMessage());
		}

	}

	public void listOfAllUsers(){
		userService.printAllUsersInJsonFormat();
	}

	public String scanner() {
		Scanner s = new Scanner(System.in);
		return s.next();
	}

}
