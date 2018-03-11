package com.chriniko.db.populator.example.worker;

import com.chriniko.db.populator.example.service.PopulatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

@Component
@Scope("prototype")
public class PopulatorWorker implements Runnable {

    private final PopulatorService populatorService;

    private int trafficTargetPerWorker;
    private int duration;
    private CountDownLatch countDownLatch;

    @Autowired
    public PopulatorWorker(PopulatorService populatorService) {
        this.populatorService = populatorService;
    }

    @Override
    public void run() {

        System.out.println("PopulatorWorker#run --- params: {trafficTargetPerWorker = "
                + trafficTargetPerWorker
                + ", duration = "
                + duration + "}");

        long startTimeInNanos = System.nanoTime();
        boolean finished = false;

        while (!finished) {

            // Note: action to be performed from worker.
            populatorService.populateDB(trafficTargetPerWorker);

            // Note: find out if we should stop.
            long timePassedInNanos = System.nanoTime() - startTimeInNanos;
            long timePassedInSecs = TimeUnit.SECONDS.convert(timePassedInNanos, TimeUnit.NANOSECONDS);
            if (timePassedInSecs >= duration) {
                finished = true;
            }

        }
        countDownLatch.countDown();

    }

    public void setTrafficTargetPerWorker(int trafficTargetPerWorker) {
        this.trafficTargetPerWorker = trafficTargetPerWorker;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setCountDownLatch(CountDownLatch countDownLatch) {
        this.countDownLatch = countDownLatch;
    }
}
