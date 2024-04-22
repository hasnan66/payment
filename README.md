# payment

# Application build and run guide

- java 11 is used for the development and h2 is used for data storage.
- To run the application docker files are under the project directory /payment.
- To run the application, open cmd/shell window under project path(/payment) and execute given docker command
  ``` docker-compose up --build ``` (docker should be installed and running on host machine)
- You can access H2 database using below url.
  ```http://localhost:8080/h2-console``` and data source url is ```jdbc:h2:mem:payment``` and username is ```sa``` and password is empty(nothing need to provide).

# API Documentation

- **API for the Payment Processing**
  > POST : http://localhost:8080/payment/process
- **Request body for Denied payment status**
  > {
  "cardNumber": "5011054488597827",
  "expiryDate": "12/25",
  "cvv": "123",
  "amount": 100.52,
  "currency": "USD",
  "merchantId": "MERCHANT123"
  }
- **Request body for Denied Approved status**
  > {
    "cardNumber": "6271701225979642",
    "expiryDate": "12/25",
    "cvv": "123",
    "amount": 100.52,
    "currency": "USD",
    "merchantId": "MERCHANT123"
    }
- **Encryption Details**
  > We can use ssh key as encryption and decryption. This will be the best approach to secure data communication.
  > Or we cna use ECC encryption.