package com.society.leagues.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
public class EmailService implements Runnable {

    ArrayBlockingQueue<EmailTaskRunner> queue = new ArrayBlockingQueue<>(5000);
    Logger logger = LoggerFactory.getLogger(EmailService.class);
    ScheduledThreadPoolExecutor threadPool = new ScheduledThreadPoolExecutor(10);

    @PostConstruct
    public void init() {
        logger.info("Email Scheduler Enabled");
        threadPool.scheduleAtFixedRate(this,1,1,TimeUnit.MINUTES);
    }

    @Override
    public void run() {
        if (queue.isEmpty()) {
            return;
        }
        EmailTaskRunner emailTaskRunner = queue.poll();
        logger.info("Sending " + emailTaskRunner.getTo());
        emailTaskRunner.run();
    }

    public void add(EmailTaskRunner taskRunner) {
        try {
            logger.info("Adding email to queue: " + taskRunner.getTo());
            queue.put(taskRunner);
        } catch (Throwable e) {
            logger.error(e.getLocalizedMessage(),e);
        }
    }
}
