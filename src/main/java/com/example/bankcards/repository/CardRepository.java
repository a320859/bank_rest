package com.example.bankcards.repository;

import com.example.bankcards.entity.Card;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Integer> {

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "INSERT INTO cards (number, owner_id, balance, validity_period, status) VALUES (:number, :ownerId, :balance, :validityPeriod, :status)")
    void saveCard(@Param("number") String number, @Param("ownerId") int ownerId, @Param("balance") int balance,
                  @Param("validityPeriod") LocalDate validityPeriod, @Param("status") String status);

    @Query(nativeQuery = true, value = "SELECT owner_id FROM cards WHERE number = :number")
    int getUserIdByCardNumber(@Param("number") String number);

    @Query(nativeQuery = true, value = "SELECT COUNT(*) FROM cards WHERE number = :number")
    int countOfCardsWithNumber(@Param("number") String number);

    @Query(nativeQuery = true, value = "SELECT * FROM cards WHERE owner_id = :userId")
    List<Card> getCards(@Param("userId") int userId);


    @Query(nativeQuery = true, value = "SELECT balance FROM cards WHERE number = :number")
    int getBalance(@Param("number") String number);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = "UPDATE cards SET balance = :newBalance WHERE number = :number")
    void editBalance(@Param("newBalance") int newBalance, @Param("number") String number);
}
