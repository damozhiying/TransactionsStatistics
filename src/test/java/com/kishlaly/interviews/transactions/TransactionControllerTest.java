package com.kishlaly.interviews.transactions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kishlaly.interviews.transactions.controllers.TransactionsController;
import com.kishlaly.interviews.transactions.exceptions.IllegalTransactionRequestException;
import com.kishlaly.interviews.transactions.exceptions.OutdatedTransactionException;
import com.kishlaly.interviews.transactions.model.Transaction;
import com.kishlaly.interviews.transactions.repository.TransactionsRepository;
import com.kishlaly.interviews.transactions.service.TransactionsService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @author Vladimir Kishlaly
 * @since 06.07.2017
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@TestPropertySource("classpath:application_test.properties")
public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Value("${valid.period.seconds:60}")
    private long period;

    @Mock
    private TransactionsService transactionsService;
    @Mock
    private TransactionsRepository repository;

    @InjectMocks
    private TransactionsController controller;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    public void test_getStatisticsBeforeAnyTransactionsMade() throws Exception {
        mockMvc.perform(get("/statistics"))
                .andExpect(status().isOk());
        verify(transactionsService, times(1)).getStatistics();
    }

    @Test
    public void test_successfullyAddingNewValidTransaction() throws Exception {
        Transaction transaction = new Transaction(1, 10, System.currentTimeMillis());
        mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJSON(transaction)))
                .andExpect(status().is(201));
        verify(transactionsService, times(1)).addNew(transaction);
    }

    @Test
    public void test_tryToAddTransactionFromFuture() throws Exception {
        Transaction transaction = new Transaction(1, 10, System.currentTimeMillis() + 10000);
        doThrow(new IllegalTransactionRequestException()).when(transactionsService).addNew(transaction);
        mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJSON(transaction)))
                .andExpect(status().is(400));
        verify(repository, never()).save(transaction);
    }

    @Test
    public void test_tryToAddExpiredTransaction() throws Exception {
        Transaction transaction = new Transaction(1, 10, System.currentTimeMillis() - (period * 1000));
        doThrow(new OutdatedTransactionException()).when(transactionsService).addNew(transaction);
        mockMvc.perform(
                post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJSON(transaction)))
                .andExpect(status().is(204));
        verify(repository, never()).save(transaction);
    }

    public String toJSON(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
