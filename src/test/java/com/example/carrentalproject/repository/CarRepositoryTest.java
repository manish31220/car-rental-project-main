package com.example.carrentalproject.repository;

import com.example.carrentalproject.constant.FuelType;
import com.example.carrentalproject.constant.GearBoxType;
import com.example.carrentalproject.domain.Car;
import com.example.carrentalproject.domain.CarPackage;
import com.example.carrentalproject.domain.CarParameters;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class CarRepositoryTest {

        @Autowired
        private CarRepository carRepository;

        @Autowired
        private CarPackageRepository carPackageRepository;

        @Autowired
        private CarParametersRepository carParametersRepository;

        @BeforeEach
        void setUp() {
                CarPackage sporty = new CarPackage(null, "Sporty", 300, new ArrayList<>());
                carPackageRepository.save(sporty);
                Car car = new Car(null, "RSA45362", "Audi", "S6", true, sporty, null);
                CarParameters carParameters = new CarParameters(null, FuelType.PETROL, GearBoxType.AUTOMATIC, 5, 5, true, car);
                carRepository.save(car);
                sporty.getCars().add(car);
        }

        @AfterEach
        void tearDown() {
                carRepository.deleteAll();
        }

        @Test
        void itShouldReturnAvailableCars() {

                List<Car> cars = carRepository.findCars(PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "id")));

                assertThat(carRepository.findAvailableCars(PageRequest.of(1, 20, Sort.by(Sort.Direction.ASC, "id"))))
                        .isEqualTo(cars.stream()
                                .filter(car -> car.getIsAvailable().equals(true))
                                .collect(Collectors.toList()));

        }

}

//        Test Case Purpose: Verify that the findAvailableCars() method correctly retrieves cars that are marked as available.
//
//        Annotations:
                //@DataJpaTest: Sets up a test focused on JPA repositories.
                //@AutoConfigureTestDatabase(replace = NONE): Uses the actual database instead of an in-memory one.
                //@Autowired: Injects the required repositories.
                //@BeforeEach: Initializes test data, including a Car and its related entities.
                //@AfterEach: Cleans up the data after each test.
                //@Test: Defines the test method that checks if the repository correctly returns available cars.





