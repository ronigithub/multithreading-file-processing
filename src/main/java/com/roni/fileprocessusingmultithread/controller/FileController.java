package com.roni.fileprocessusingmultithread.controller;

import com.roni.fileprocessusingmultithread.service.FileExportService;
import com.roni.fileprocessusingmultithread.service.FileUploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * File Rest Controller
 *
 * @author Josim Uddin Roni
 * @since 2nd April 2023
 * @version 1.0
 */
@RestController
@RequestMapping("/file")
@CrossOrigin
@RequiredArgsConstructor
public class FileController {
    private final FileUploadService fileUploadService;
    private final FileExportService fileExportService;

    /**
     * Customers file upload
     * @param file file
     */
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/upload")
    public void upload(@RequestParam MultipartFile file) {
        fileUploadService.upload(file);
    }

    /**
     * Export valid customers
     */
    @GetMapping("/export")
    public void export() {
        fileExportService.exportValidCustomers();
    }
}

