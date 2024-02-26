package com.cstorage.repository;

import com.cstorage.dto.file.FileDTO;
import org.springframework.stereotype.Repository;

import java.io.File;

@Repository
public interface FileRepository {
    void uploadFile(FileDTO object, String path);

    void deleteFile(String path);

    File downloadFile(String path);

    void renameFile(String path, String after);
}
