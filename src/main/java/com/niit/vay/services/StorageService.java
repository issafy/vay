package com.niit.vay.services;

import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource;

import java.nio.file.Path;
import java.util.stream.Stream;

public interface StorageService {

//    public void init();

    public void save(MultipartFile file);

    public Resource load(String filename);

    public void deleteAll();

    public Stream<Path> loadAll();

}
