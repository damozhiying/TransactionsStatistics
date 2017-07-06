package com.kishlaly.interviews.transactions.repository;

import com.kishlaly.interviews.transactions.model.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * @author Vladimir Kishlaly
 * @since 06.07.2017
 */
public interface TransactionsRepository extends CrudRepository<Transaction, Long> {

    @Query("SELECT t FROM Transaction t WHERE t.timestamp >= :timestamp")
    List<Transaction> findStartingFrom(@Param("timestamp") long timestamp);

}
