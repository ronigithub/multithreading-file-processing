package com.roni.fileprocessusingmultithread.service;

import com.roni.fileprocessusingmultithread.model.ValidCustomer;
import com.roni.fileprocessusingmultithread.repository.ValidCustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * File Export Service
 *
 * @author Josim Uddin Roni
 * @since 2nd April 2023
 * @version 1.0
 */
@Service
@Transactional
@RequiredArgsConstructor
public class FileExportService {
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();
    private final ValidCustomerRepository validCustomerRepository;

    public void exportValidCustomers() {
        long started = System.currentTimeMillis();

        int batchSize = 100000;
        List<ValidCustomer> validCustomers = validCustomerRepository.findAll();
        List<List<ValidCustomer>> batches = new ArrayList<>();
        for (int i = 0; i < validCustomers.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, validCustomers.size());
            batches.add(validCustomers.subList(i, endIndex));
        }

        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        CountDownLatch latch = new CountDownLatch(batches.size());

        for (List<ValidCustomer> batch : batches) {
            executor.submit(() -> {
                try {
                    exportBatch(batch);
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        executor.shutdown();

        long ended = System.currentTimeMillis();
        System.out.println("Total Execution Time: " + ((ended - started)));
    }

    private void exportBatch(List<ValidCustomer> batch) {
        String fileName = "valid_customers_" + UUID.randomUUID() + ".txt";
        try (FileWriter writer = new FileWriter(fileName)) {
            for (ValidCustomer customer : batch) {
                String line = String.format("%s,%s,%s,%s,%s\n",
                        customer.getName(), customer.getAddressString(),
                        customer.getMobile(), customer.getEmail(), customer.getIp());
                writer.write(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
