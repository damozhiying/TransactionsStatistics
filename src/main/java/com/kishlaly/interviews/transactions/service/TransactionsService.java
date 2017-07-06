package com.kishlaly.interviews.transactions.service;

import com.kishlaly.interviews.transactions.model.Transaction;
import com.kishlaly.interviews.transactions.to.Statistics;

/**
 * Transaction service provides handling, validation and saving incoming entities.
 *
 * @author Vladimir Kishlaly
 * @since 06.07.2017
 */
public interface TransactionsService {

    /**
     * Adds new transaction to DB
     *
     * @param transaction
     */
    void addNew(Transaction transaction);

    /**
     * Returns the statistics of existing transactions for the las period.
     * <p>Period in seconds specified in <i>application.properties</i></p>
     *
     * @return
     */
    Statistics getStatistics();

    /**
     * This method is being by inner scheduler. There is no need to call it directly, though it safe. Used in tests.
     */
    public void update();

    /**
     * Clears the inner cache. Use with caution, as it might corrupt the correct list of transactions for the last period of time.
     */
    void clearCache();

}
