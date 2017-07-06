package com.kishlaly.interviews.transactions.service.impl;

import com.kishlaly.interviews.transactions.exceptions.IllegalTransactionRequestException;
import com.kishlaly.interviews.transactions.exceptions.OutdatedTransactionException;
import com.kishlaly.interviews.transactions.model.Transaction;
import com.kishlaly.interviews.transactions.repository.TransactionsRepository;
import com.kishlaly.interviews.transactions.service.TransactionsService;
import com.kishlaly.interviews.transactions.to.Statistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @author Vladimir Kishlaly
 * @since 06.07.2017
 */
@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
public class TimedTransactionsService implements TransactionsService {

    private ConcurrentLinkedDeque<Transaction> cache = new ConcurrentLinkedDeque<>();
    private AtomicReference<Statistics> statistics = new AtomicReference<>(new Statistics(0, 0, 0, 0, 0));

    @Autowired
    private TransactionsRepository repository;

    @Value("${valid.period.seconds:60}")
    private long period;

    private TimeUnit timeUnit = TimeUnit.SECONDS;
    private volatile boolean warmedUp;

    @Override
    public synchronized void addNew(Transaction transaction) {
        if (transaction == null || transaction.getAmount() == 0 || transaction.getTimestamp() == 0 || transaction.getTimestamp() > Instant.now().toEpochMilli()) {
            throw new IllegalTransactionRequestException("Wrong transaction attributes");
        }
        if (isOutdated(transaction)) {
            throw new OutdatedTransactionException("Timestamp is older than " + period + " " + timeUnit.toString().toLowerCase());
        }
        cache.addLast(transaction);
        repository.save(transaction);
    }

    @Override
    public Statistics getStatistics() {
        return statistics.get();
    }

    @Scheduled(fixedDelay = 50)
    public void update() {
        if (!cache.isEmpty()) {
            while (!cache.isEmpty() && isOutdated(cache.getFirst())) {
                cache.pollFirst();
            }
            calculateStatistics();
        } else {
            if (!warmedUp) {
                List<Transaction> existed = repository.findStartingFrom(getDifference());
                if (existed != null) {
                    cache.addAll(existed);
                }
                calculateStatistics();
                warmedUp = true;
            } else {
                // otherwise we'll get -Infinity and +Infinity in DoubleSummaryStatistics
                statistics.getAndSet(new Statistics(0, 0, 0, 0, 0));
            }
        }
    }

    @Override
    public void clearCache() {
        cache.clear();
    }

    private boolean isOutdated(Transaction transaction) {
        return transaction.getTimestamp() < getDifference();
    }

    private long getDifference() {
        return Instant.now().toEpochMilli() - (timeUnit.toMillis(period));
    }

    private void calculateStatistics() {
        DoubleSummaryStatistics stat = cache.stream().collect(Collectors.summarizingDouble(Transaction::getAmount));
        statistics.getAndSet(new Statistics(stat.getSum(), stat.getAverage(), stat.getMax(), stat.getMin(), stat.getCount()));
    }

}
