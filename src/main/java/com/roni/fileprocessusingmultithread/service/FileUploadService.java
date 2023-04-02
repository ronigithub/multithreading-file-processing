package com.roni.fileprocessusingmultithread.service;

import com.roni.fileprocessusingmultithread.model.InvalidCustomer;
import com.roni.fileprocessusingmultithread.model.ValidCustomer;
import com.roni.fileprocessusingmultithread.repository.InvalidCustomerRepository;
import com.roni.fileprocessusingmultithread.repository.ValidCustomerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * File Upload Service
 *
 * @author Josim Uddin Roni
 * @version 1.0
 * @since 2nd April 2023
 */
@Service
@RequiredArgsConstructor
public class FileUploadService {
    private final ValidCustomerRepository validCustomerRepository;
    private final InvalidCustomerRepository invalidCustomerRepository;
    private static final int NUM_THREADS = Runtime.getRuntime().availableProcessors();

    @Value("${spring.jpa.properties.hibernate.jdbc.batch_size}")
    private int batchSize;

    @Transactional
    public void upload(MultipartFile file) {
        long started = System.currentTimeMillis();

        List<ValidCustomer> validLines = new ArrayList<>();
        List<InvalidCustomer> invalidLines = new ArrayList<>();
        ExecutorService executor = Executors.newFixedThreadPool(NUM_THREADS);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                executor.submit(new LineProcessor(line, validLines, invalidLines));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        while (!executor.isTerminated()) {
            // Wait for all threads to finish
        }

        // save data in database
        // valid data
        List<List<ValidCustomer>> batches = new ArrayList<>();
        for (int i = 0; i < validLines.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, validLines.size());
            batches.add(validLines.subList(i, endIndex));
        }

        for (List<ValidCustomer> batch : batches) {
            validCustomerRepository.saveAll(batch);
        }

        // invalid data
        List<List<InvalidCustomer>> otherBatches = new ArrayList<>();
        for (int i = 0; i < invalidLines.size(); i += batchSize) {
            int endIndex = Math.min(i + batchSize, invalidLines.size());
            otherBatches.add(invalidLines.subList(i, endIndex));
        }
        for (List<InvalidCustomer> batch : otherBatches) {
                invalidCustomerRepository.saveAll(batch);
        }

        long ended = System.currentTimeMillis();
        System.out.println("Total Execution time: " + ((ended - started)));
        System.out.println("Valid Total: " + validLines.size() + " Invalid total: " + invalidLines.size());

    }

    private record LineProcessor(String line, List<ValidCustomer> validLines,
                                 List<InvalidCustomer> invalidLines) implements Runnable {

        @Override
        public void run() {
            final String[] parts = line.split(",");
            String name = parts[0];
            String street = parts[1];
            String city = parts[2];
            String state = parts[3];
            String zip = parts[4];
            String mobile = parts[5];
            String email = parts[6];
            String ip = parts[7];
            String addressString = street + city + state + zip;

            // mobile validation
            Pattern mobilePattern = Pattern.compile("^(1\\s?)?(\\(\\d{3}\\)|\\d{3})[\\s-]?\\d{3}[\\s-]?\\d{4}$");
            Matcher mobileMatcher = mobilePattern.matcher(mobile);

            // email validation
            Pattern emailPattern = Pattern.compile("^\\w+([\\.-]?\\w+)*@\\w+([\\.-]?\\w+)*(\\.\\w{2,3})+$");
            Matcher emailMatcher = emailPattern.matcher(email);

            if (mobileMatcher.matches() && emailMatcher.matches()) {
                synchronized (validLines) {
                    final ValidCustomer valid = new ValidCustomer();
                    valid.setName(name);
                    valid.setAddressString(addressString);
                    valid.setMobile(mobile);
                    valid.setEmail(email);
                    valid.setIp(ip);
                    validLines.add(valid);
                }
            } else {
                synchronized (invalidLines) {
                    final InvalidCustomer invalid = new InvalidCustomer();
                    invalid.setName(name);
                    invalid.setAddressString(addressString);
                    invalid.setMobile(mobile);
                    invalid.setEmail(email);
                    invalid.setIp(ip);
                    invalidLines.add(invalid);
                }
            }
        }
    }

}
