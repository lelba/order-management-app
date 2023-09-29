REST Api
When using the REST Api, the user must be authenticated: localhost:8080/api/authenticate

If the user has ADMIN roles, the user can access the following endpoints:
1. localhost:8080/api/users  - get All Active User
2. localhost:8080/api/users/add - add New User
3. localhost:8080/api/users/{username} - delete User
4. localhost:8080/api/products/add - add new Product
5. localhost:8080/api/products - get All available Products
6. localhost:8080/api/products/{product_name} - delete Product

If the user has the CUSTOMER role, the user can access the following endpoints:
1. localhost:8080/api/orders - get All Orders
2. localhost:8080/api/orders/add - add New Order