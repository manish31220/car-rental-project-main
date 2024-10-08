package com.example.carrentalproject.service;

import com.example.carrentalproject.domain.AccessKey;
import com.example.carrentalproject.domain.CarPackage;
import com.example.carrentalproject.domain.CreditCard;
import com.example.carrentalproject.domain.PlacedOrder;
import com.example.carrentalproject.domain.User;
import com.example.carrentalproject.dto.AccessKeyDto;
import com.example.carrentalproject.exception.InsufficientFundsException;
import com.example.carrentalproject.exception.NoCreditCardException;
import com.example.carrentalproject.repository.AccessKeyRepository;
import com.example.carrentalproject.repository.CarPackageRepository;
import com.example.carrentalproject.repository.OrderRepository;
import com.example.carrentalproject.security.LoggedInUser;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

        @Mock
        CarPackageRepository carPackageRepository;

        @Mock
        AccessKeyRepository accessKeyRepository;

        @Mock
        LoggedInUser loggedInUser;

        @Mock
        OrderRepository orderRepository;

        @InjectMocks
        OrderService orderService;

        @Test
        void itShouldReturnAllOrders() {
                PlacedOrder order = PlacedOrder.builder()
                        .userId(1L)
                        .carId(5L)
                        .brand("Ford")
                        .model("Mustang")
                        .build();

                PlacedOrder order2 = PlacedOrder.builder()
                        .userId(37L)
                        .carId(15L)
                        .brand("Fiat")
                        .model("Brava")
                        .build();

                PlacedOrder order3 = PlacedOrder.builder()
                        .userId(9L)
                        .carId(7L)
                        .brand("Daewoo")
                        .model("Matiz")
                        .build();

                PlacedOrder order4 = PlacedOrder.builder()
                        .userId(4L)
                        .carId(9L)
                        .brand("Porsche")
                        .model("Cayman")
                        .build();

                List<PlacedOrder> orders = new ArrayList<>();
                orders.add(order);
                orders.add(order2);
                orders.add(order3);
                orders.add(order4);


                when(orderRepository.findAll()).thenReturn(orders);


                Assertions.assertThat(orderService.getOrders()).isEqualTo(orders);
        }

        @Test
        void itShouldReturnAccessKeyDto() {
                CreditCard card = CreditCard.builder()
                        .cardNumber(7755443334559900L)
                        .month(4)
                        .year(2023)
                        .CVV(278)
                        .accountBalance(1200L)
                        .build();

                User user = User.builder()
                        .creditCard(card)
                        .build();

                CarPackage luxury = CarPackage.builder()
                        .packageName("Luxury")
                        .pricePerHour(500)
                        .build();

                AccessKey accessKey = AccessKey.builder()
                        .carPackage("Luxury")
                        .hours(2)
                        .build();

                AccessKeyDto accessKeyDto = AccessKeyDto.builder()
                        .carPackage("Luxury")
                        .hours(2)
                        .build();


                when(loggedInUser.getUser()).thenReturn(user);
                when(carPackageRepository.findByPackageName("Luxury")).thenReturn(Optional.of(luxury));
                when(accessKeyRepository.save(accessKey)).thenReturn(accessKey);


                assertThat(orderService.submitOrder("Luxury", 2)).isEqualTo(accessKeyDto);
                assertThat(user.getCreditCard().getAccountBalance()).isEqualTo(200L);
        }

        @Test
        void itShouldThrowEntityNotFoundException() {
                CreditCard card = CreditCard.builder()
                        .accountBalance(0L)
                        .build();

                User user = User.builder()
                        .username("Radoslaw")
                        .creditCard(card)
                        .build();


                when(loggedInUser.getUser()).thenReturn(user);
                when(carPackageRepository.findByPackageName(anyString())).thenThrow(EntityNotFoundException.class);


                assertThrows(EntityNotFoundException.class, () -> orderService.submitOrder("BigCar", 3));
        }

        @Test
        void itShouldThrowNoCreditCardException() {
                User user = User.builder()
                        .username("Tomasz")
                        .creditCard(null)
                        .build();


                when(loggedInUser.getUser()).thenReturn(user);


                assertThrows(NoCreditCardException.class, () -> orderService.submitOrder("Ordinary", 2));
        }

        @Test
        void itShouldThrowInsufficientFundsException() {
                CreditCard card = CreditCard.builder()
                        .accountBalance(600L)
                        .build();

                User user = User.builder()
                        .username("Radoslaw")
                        .creditCard(card)
                        .build();

                CarPackage luxury = CarPackage.builder()
                        .packageName("Luxury")
                        .pricePerHour(500)
                        .build();


                when(loggedInUser.getUser()).thenReturn(user);
                when(carPackageRepository.findByPackageName("Luxury")).thenReturn(Optional.of(luxury));


                assertThrows(InsufficientFundsException.class, () -> orderService.submitOrder("Luxury", 2));
        }

}


//Setup:
//        Mocks: Utilizes Mockito to create mocks for CarPackageRepository, AccessKeyRepository, LoggedInUser, and OrderRepository.
//        Inject Mocks: OrderService is instantiated with the mocked dependencies.
//        Test Methods:
//        itShouldReturnAllOrders()
//
//        Purpose: Verifies that getOrders() returns a list of all orders.
//        Setup: Mocked orderRepository.findAll() to return a predefined list of orders.
//        Assertion: Compares the result of orderService.getOrders() with the predefined list.
//        itShouldReturnAccessKeyDto()
//
//        Purpose: Checks if submitOrder() correctly processes an order and updates the user's credit card balance.
//        Setup: Mocked loggedInUser.getUser() to return a user with a credit card, carPackageRepository.findByPackageName() to return a valid car package, and accessKeyRepository.save() to return a saved access key.
//        Assertions:
//        Verifies that orderService.submitOrder("Luxury", 2) returns the expected AccessKeyDto.
//        Ensures the user's credit card balance is updated correctly.
//        itShouldThrowEntityNotFoundException()
//
//        Purpose: Tests that submitOrder() throws an EntityNotFoundException when the car package is not found.
//        Setup: Mocked carPackageRepository.findByPackageName() to throw an EntityNotFoundException.
//        Assertion: Asserts that orderService.submitOrder("BigCar", 3) throws EntityNotFoundException.
//        itShouldThrowNoCreditCardException()
//
//        Purpose: Ensures that submitOrder() throws NoCreditCardException if the user does not have a credit card.
//        Setup: Mocked loggedInUser.getUser() to return a user without a credit card.
//        Assertion: Verifies that orderService.submitOrder("Ordinary", 2) throws NoCreditCardException.
//        itShouldThrowInsufficientFundsException()
//
//        Purpose: Verifies that submitOrder() throws InsufficientFundsException when the user's credit card balance is insufficient.
//        Setup: Mocked loggedInUser.getUser() to return a user with insufficient funds and carPackageRepository.findByPackageName() to return a valid car package.
//        Assertion: Asserts that orderService.submitOrder("Luxury", 2) throws InsufficientFundsException.