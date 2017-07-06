package com.kishlaly.interviews.transactions.controllers;

import com.kishlaly.interviews.transactions.model.Transaction;
import com.kishlaly.interviews.transactions.service.TransactionsService;
import com.kishlaly.interviews.transactions.to.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

/**
 * @author Vladimir Kishlaly
 * @since 06.07.2017
 */
@RestController
public class TransactionsController {

    @Autowired
    private TransactionsService transactionsService;

    @RequestMapping("/statistics")
    public Statistics getStatistics() {
        return transactionsService.getStatistics();
    }

    @RequestMapping(value = "/transactions", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public void add(@RequestBody Transaction transaction) {
        transactionsService.addNew(transaction);
    }

}
