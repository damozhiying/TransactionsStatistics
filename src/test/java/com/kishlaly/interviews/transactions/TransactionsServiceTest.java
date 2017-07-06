package com.kishlaly.interviews.transactions;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.stream.LongStream;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;

/**
 * @author Vladimir Kishlaly
 * @since 06.07.2017
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = Application.class)
@TestPropertySource("classpath:application_test.properties")
public class TransactionsServiceTest {

    @Value("${valid.period.seconds:60}")
    private long period;

    @Autowired
    @InjectMocks
    private TransactionsService transactionsService;

    @Mock
    private TransactionsRepository repository;

    @Before
    public void beforeEachTest() {
        MockitoAnnotations.initMocks(this);
        transactionsService.clearCache();
    }

    @Test(expected = IllegalTransactionRequestException.class)
    public void test_addNonExistentTransaction() {
        transactionsService.addNew(null);
    }

    @Test(expected = OutdatedTransactionException.class)
    public void test_outdatedTransaction() {
        transactionsService.addNew(new Transaction(10, 123));
    }

    @Test(expected = OutdatedTransactionException.class)
    public void test_transactionFromFuture() {
        transactionsService.addNew(new Transaction(10, 123));
    }

    @Test
    public void test_countOfTransactionWithValidTimestamp() {
        long limit = 1000;
        LongStream.rangeClosed(1, limit).forEach(i -> {
            fill(i, System.currentTimeMillis());
        });
        transactionsService.update();
        assertThat(transactionsService.getStatistics().getCount(), is(limit));
    }

    @Test
    public void test_countOfParticularlyExpiredTransactions() {
        long limit = 1000;
        LongStream.rangeClosed(1, limit).forEach(i -> {
            fill(i, System.currentTimeMillis() - period * 1000 + 100);
        });
        transactionsService.update();
        long finalCount = transactionsService.getStatistics().getCount();
        assertTrue(finalCount < limit);
    }

    @Test
    public void test_countOfFullyExpiredTransactions() {
        long limit = 1000;
        LongStream.rangeClosed(1, limit).forEach(i -> {
            fill(i, System.currentTimeMillis() - period * 1000 + 100);
        });
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
        }
        transactionsService.update();
        long finalCount = transactionsService.getStatistics().getCount();
        assertTrue(finalCount == 0);
    }

    private void fill(long id, long timestamp) {
        Transaction transaction = new Transaction(id, 1, timestamp);
        given(repository.save(transaction)).willReturn(transaction);
        transactionsService.addNew(transaction);
    }


}
