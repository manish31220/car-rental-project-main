package com.example.carrentalproject.repository;

import com.example.carrentalproject.domain.User;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.Optional;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UserRepositoryTest {

        @Autowired
        private UserRepository userRepository;

        @BeforeEach
        void setUp() {
                User user = new User(null, "Lawrence", "Okolie", "lawrence123", "apples678", "lpf56@gmail.com", 888777621, null, null, new ArrayList<>());
                User user2 = new User(null, "Anthony", "Joshua", "joshua456", "eddiehearn678", "gbjosh@onet.pl", 465999222, null, null, new ArrayList<>());
                User user3 = new User(null, "Tyson", "Fury", "gypsyKing", "deeoonthai", "gypsyKing655@gmail.com", 765555444, null, null, new ArrayList<>());
                User user4 = new User(null, "Saul", "Alvarez", "canelo", "prettyBoy", "Goose@gmail.com", 908764220, null, null, new ArrayList<>());
                userRepository.save(user);
                userRepository.save(user2);
                userRepository.save(user3);
                userRepository.save(user4);
        }

        @AfterEach
        void tearDown() {
                userRepository.deleteAll();
        }

        @Test
        void itShouldReturnTrueIfUsernameExists() {
                AssertionsForClassTypes.assertThat(userRepository.findByUsername("lawrence123")).isInstanceOf(Optional.class).isPresent();
                AssertionsForClassTypes.assertThat(userRepository.findByUsername("Gregory56")).isInstanceOf(Optional.class).isEmpty();
                AssertionsForClassTypes.assertThat(userRepository.findByUsername("gypsyKing")).isInstanceOf(Optional.class).isPresent();
                AssertionsForClassTypes.assertThat(userRepository.findByUsername("joshua456")).isInstanceOf(Optional.class).isPresent();
                AssertionsForClassTypes.assertThat(userRepository.findByUsername("programmer777")).isInstanceOf(Optional.class).isEmpty();
                AssertionsForClassTypes.assertThat(userRepository.findByUsername("canelo")).isInstanceOf(Optional.class).isPresent();

        }

}

//    Setup and Teardown:
//
                //@BeforeEach: This method runs before each test, setting up the test environment. It creates and saves several User objects in the repository.
                //@AfterEach: This method runs after each test, cleaning up the test environment by deleting all users from the repository.
                //        Test Method:
//
//@Test - itShouldReturnTrueIfUsernameExists(): This test checks whether the findByUsername method of the userRepository correctly identifies if a username exists in the repository:
//        It verifies that existing usernames return a present Optional.
//        It verifies that non-existing usernames return an empty Optional.
//        Assertions:
//
//        Uses AssertionsForClassTypes to check the presence and absence of Optional results for different usernames.
