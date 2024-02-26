package com.cstorage.service;

import com.cstorage.dto.file.FileDTO;
import com.cstorage.repository.FileRepository;
import com.cstorage.utils.DirUtil;
import com.cstorage.utils.FileUtil;
import io.minio.*;
import io.minio.errors.*;
import jakarta.annotation.PostConstruct;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class FileService implements FileRepository {

    @Autowired
    ClientService clientService;

    private String userDownload = System.getProperty("user.home") + File.separator +
                                                     ".myminio" + File.separator +
                                                     "download";
    private String tempUploadFiles = System.getProperty("user.home") + File.separator +
                                                        ".myminio" + File.separator +
                                                        "upload";

    @Override
    public void uploadFile(FileDTO object, String path) {
        try {
            for (var i : object.getFiles()) {
                File some = fromMulitpartToFile(i);
                clientService.getMinioClient().uploadObject(
                        UploadObjectArgs.builder()
                                .bucket(clientService.getRootBucket())
                                .object(path + i.getOriginalFilename())
                                .filename(some.getPath()).build());
            }
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFile(String path) {
        if (!FileUtil.validate(path) || !fileExists(path))
            return;
        try {
            clientService.getMinioClient().removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(clientService.getRootBucket())
                            .object(path)
                            .build());
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public File downloadFile(String path) {
        clearBaseFolders();
        if (fileExists(path)) {
            try {
                clientService.getMinioClient().downloadObject(
                        DownloadObjectArgs.builder()
                                .bucket(clientService.getRootBucket())
                                .object(path)
                                .filename(userDownload + File.separator + FileUtil.getObjectName(path))
                                .build());
            } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                     IOException | InvalidResponseException | InvalidKeyException | InternalException |
                     InsufficientDataException e) {
                throw new RuntimeException(e);
            }
            return new File(userDownload + File.separator + FileUtil.getObjectName(path));
        }
        return null;
    }

    @Override
    public void renameFile(String path, String after) {
        try {
            if (!FileUtil.validate(path) ||
                    !FileUtil.validate(path + after) ||
                    !DirUtil.validate(path))
                return;
            if (!fileExists(path))
                return;
            clientService.getMinioClient().copyObject(
                    CopyObjectArgs.builder()
                            .bucket(clientService.getRootBucket())
                            .object(DirUtil.getPath(path) + after)
                            .source(
                                    CopySource.builder()
                                            .bucket(clientService.getRootBucket())
                                            .object(path)
                                            .build())
                            .build());
            deleteFile(path);
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

    @PostConstruct
    private void setBaseFolders() {
        File theDir = new File(userDownload);
        File theDir1 = new File(tempUploadFiles);
        if (!theDir.exists())
            theDir.mkdirs();
        if (!theDir1.exists())
            theDir1.mkdirs();
    }
    private void clearBaseFolders() {
        File theDir = new File(userDownload);
        File theDir1 = new File(tempUploadFiles);
        if (theDir.exists()) {
            try {
                FileUtils.cleanDirectory(theDir);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        if (theDir1.exists()) {
            try {
                FileUtils.cleanDirectory(theDir1);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private File fromMulitpartToFile(MultipartFile multipartFile) {
        File file = new File(tempUploadFiles + File.separator + multipartFile.getName());
        try {
            multipartFile.transferTo(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private boolean fileExists(String path) {
        try {
            StatObjectResponse objectStat = clientService.getMinioClient().statObject(
                    StatObjectArgs.builder().bucket(clientService.getRootBucket()).object(path).build());
            if (objectStat == null)
                return false;
            return true;
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }
}
