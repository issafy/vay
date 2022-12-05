package com.niit.vay.controllers;

import com.niit.vay.services.StorageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;

@RestController
@RequestMapping("/files")
@CrossOrigin("http://localhost:8100")
public class FileController {

    private final StorageService storageService;

    public FileController(StorageService storageService) {
        this.storageService = storageService;
    }

    @RolesAllowed("ADMIN")
    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<String> uploadFile(MultipartFile file) {
        String message = "";
        try {
            storageService.save(file);;
            message = "Uploaded the file successfully: " + file.getOriginalFilename();
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "Could not upload the file: " + file.getOriginalFilename() + "!";
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }



}
