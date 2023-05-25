package com.niit.vay.services;

import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

@Service
public class StorageServiceImpl implements StorageService{

    private final Path root = Paths.get("src/main/webapp/admin/assets/img");

//    @Override
//    public void init() {
//        try {
//            if (!Files.exists(root))
//                Files.createDirectory(root);
//        } catch (IOException e) {
//            throw new RuntimeException("Could not create directory! Error: " + e.getMessage());
//        }
//    }

    @Override
    public void save(MultipartFile file) {
        try {
            Files.deleteIfExists(this.root.resolve(Objects.requireNonNull(file.getOriginalFilename())));
            Files.copy(file.getInputStream(), this.root.resolve(file.getOriginalFilename()));
        } catch (IOException e) {
            throw new RuntimeException("Could not store file! Error: " + e.getMessage());
        }
    }

    @Override
    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
             Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file.");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.root, 1).filter(path -> !path.equals(this.root)).map(this.root::relativize);
        } catch (IOException e) {
            throw new RuntimeException("Could not load the files!. Error: " +  e.getMessage());
        }
    }

}
