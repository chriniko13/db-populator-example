package com.chriniko.db.populator.example.runner;

import com.chriniko.db.populator.example.worker.PopulatorWorker;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

@Component
public class PopulatorRunner {

    @Autowired
    private ObjectFactory<PopulatorWorker> populatorWorkerObjectFactory;

    private ExecutorService executorService;

    @PostConstruct
    void init() {
        initialize();
    }

    private void initialize() {
        executorService = Executors.newCachedThreadPool();

        Runtime.getRuntime()
                .addShutdownHook(
                        new Thread(() -> {
                            System.out.println("\nShutting down...");
                            executorService.shutdownNow();
                        })
                );
    }

    public void run(int concurrency, int trafficTarget, int duration, boolean equalDistribution) {

        if (concurrency > trafficTarget) {
            throw new IllegalStateException("concurrencyAsInt > trafficTargetAsInt");
        }

        final CountDownLatch finishLatch = new CountDownLatch(concurrency);

        double loadPerWorker = (double) trafficTarget / (double) concurrency;
        boolean balancedWork = loadPerWorker % 1 == 0;

        if (balancedWork) {
            balancedWorkProcessing(finishLatch, (int) loadPerWorker, concurrency, duration);
        } else {
            unbalancedWorkProcessing(finishLatch, loadPerWorker, concurrency, duration, equalDistribution);
        }

        awaitTermination(finishLatch);
    }

    private void balancedWorkProcessing(CountDownLatch finishLatch,
                                        int loadPerWorker,
                                        int concurrency,
                                        int duration) {

        IntStream.range(0, concurrency)
                .forEach(idx -> {

                    PopulatorWorker populatorWorker = populatorWorkerObjectFactory.getObject();

                    populatorWorker.setDuration(duration);
                    populatorWorker.setTrafficTargetPerWorker(loadPerWorker);
                    populatorWorker.setCountDownLatch(finishLatch);

                    executorService.submit(populatorWorker);

                });
    }

    private void unbalancedWorkProcessing(CountDownLatch finishLatch,
                                          double loadPerWorker,
                                          int concurrency,
                                          int duration,
                                          boolean equalDistribution) {

        double missedWork = loadPerWorker % 1;
        double totalMissedWork = 0;
        for (int i = 0; i < concurrency; i++) {
            totalMissedWork += missedWork;
        }

        // --- Note: use them for investigation ---
        System.out.println("\ntotal missed work: " + totalMissedWork);
        System.out.println("total missed work(ceil): " + Math.ceil(totalMissedWork));
        System.out.println("total missed work(floor): " + Math.floor(totalMissedWork));
        System.out.println();

        if (!equalDistribution) {

            for (int i = 0; i < concurrency; i++) {

                boolean isLast = (i == concurrency - 1);

                PopulatorWorker populatorWorker = populatorWorkerObjectFactory.getObject();
                populatorWorker.setDuration(duration);
                populatorWorker.setCountDownLatch(finishLatch);

                if (isLast) { // Note: add the total missed work to the last worker.
                    populatorWorker.setTrafficTargetPerWorker(((int) loadPerWorker) + ((int) totalMissedWork));
                } else {
                    populatorWorker.setTrafficTargetPerWorker((int) loadPerWorker);
                }

                executorService.submit(populatorWorker);

            }

        } else {

            final Integer[] loadPerWorkers = new Integer[concurrency];
            for (int i = 0; i < concurrency; i++) {
                loadPerWorkers[i] = (int) loadPerWorker;
            }

            // Note: now steal from missed work and equal distribute it among all workers...
            int loadPerWorkerWalker = 0;
            int totalMissedWorkAsInt = (int) totalMissedWork;

            while (totalMissedWorkAsInt != 0) {

                loadPerWorkers[loadPerWorkerWalker] = loadPerWorkers[loadPerWorkerWalker] + 1;

                totalMissedWorkAsInt--;
                loadPerWorkerWalker++;
            }

            System.out.println("\nloadPerWorkers = " + Arrays.toString(loadPerWorkers) + "\n");

            for (Integer load : loadPerWorkers) {

                PopulatorWorker populatorWorker = populatorWorkerObjectFactory.getObject();
                populatorWorker.setDuration(duration);
                populatorWorker.setCountDownLatch(finishLatch);
                populatorWorker.setTrafficTargetPerWorker(load);

                executorService.submit(populatorWorker);
            }
        }

    }

    private void awaitTermination(CountDownLatch finishLatch) {
        try {
            finishLatch.await();
        } catch (InterruptedException e) {
            System.err.println("PopulatorRunner#run --- error occurred: " + e);
        }
        System.exit(0);
    }

}
