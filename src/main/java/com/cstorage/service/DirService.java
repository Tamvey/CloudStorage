package com.cstorage.service;

import com.cstorage.repository.DirRepository;
import com.cstorage.utils.DirUtil;
import com.cstorage.utils.FileUtil;
import com.cstorage.utils.Pair;
import io.minio.*;
import io.minio.errors.*;
import io.minio.messages.Item;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DirService implements DirRepository {
    @Autowired
    ClientService clientService;
    @Override
    public List<String> listObjects(String path) {
        var objects = new ArrayList<String>();
        for (Result<Item> it : clientService.getMinioClient().listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(clientService.getRootBucket())
                        .prefix(path)
                        .build())) {
            try {
                if (!it.get().isDir())
                    objects.add(it.get().objectName().replace(path, ""));
            } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                     IOException | InvalidResponseException | InvalidKeyException | InternalException |
                     InsufficientDataException e) {
                throw new RuntimeException(e);
            }
        }
        return objects;
    }

    @Override
    public List<String> listFolders(String path) {
        var folders = new ArrayList<String>();
        for (Result<Item> it : clientService.getMinioClient().listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(clientService.getRootBucket())
                        .prefix(path)
                        .build())) {
            try {
                if (it.get().isDir())
                    folders.add(it.get().objectName().replace(path, ""));
            } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                     IOException | InvalidResponseException | InvalidKeyException | InternalException |
                     InsufficientDataException e) {
                throw new RuntimeException(e);
            }
        }
        return folders;
    }

    @Override
    public List<String> listRootObjects() {
        var objects = new ArrayList<String>();
        for (Result<Item> it : clientService.getMinioClient().listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(clientService.getRootBucket())
                        .build())) {
            try {
                if (!it.get().isDir())
                    objects.add(it.get().objectName());
            } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                     IOException | InvalidResponseException | InvalidKeyException | InternalException |
                     InsufficientDataException e) {
                throw new RuntimeException(e);
            }
        }
        return objects;
    }

    @Override
    public List<String> listRootFolders() {
        var folders = new ArrayList<String>();
        for (Result<Item> it : clientService.getMinioClient().listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(clientService.getRootBucket())
                        .build())) {
            try {
                if (it.get().isDir())
                    folders.add(it.get().objectName());
            } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                     IOException | InvalidResponseException | InvalidKeyException | InternalException |
                     InsufficientDataException e) {
                throw new RuntimeException(e);
            }
        }
        return folders;
    }

    @Override
    public void addFolder(String path) {
        try {
            String newPath = path + "/empty.txt";
            if (!DirUtil.validate(newPath))
                return;
            clientService.getMinioClient().putObject(
                    PutObjectArgs.builder().bucket(clientService.getRootBucket())
                            .object(newPath)
                            .stream(
                                    new ByteArrayInputStream(new byte[]{}), 0, -1).build());
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void deleteFolder(String path) {
        if (!DirUtil.validate(path))
            return;
        for (Result<Item> it : clientService.getMinioClient().listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(clientService.getRootBucket())
                        .recursive(true)
                        .prefix(path)
                        .build())){
            try {
                clientService.getMinioClient().removeObject(
                        RemoveObjectArgs.builder()
                                .bucket(clientService.getRootBucket())
                                .object(it.get().objectName())
                                .build());
            } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                     IOException | InvalidResponseException | InvalidKeyException | InternalException |
                     InsufficientDataException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    public void renameFolder(String before, String after) {
        if (!DirUtil.validate(before) || !DirUtil.validate(after) || !folderExists(before) || before.equals(after))
            return;
        try {
            for (Result<Item> it : clientService.getMinioClient().listObjects(
                    ListObjectsArgs
                            .builder()
                            .bucket(clientService.getRootBucket())
                            .recursive(true)
                            .prefix(before)
                            .build())) {
                clientService.getMinioClient().copyObject(
                        CopyObjectArgs.builder()
                                .bucket(clientService.getRootBucket())
                                .object(it.get().objectName().replace(before, after + "/"))
                                .source(
                                        CopySource.builder()
                                                .bucket(clientService.getRootBucket())
                                                .object(it.get().objectName())
                                                .build())
                                .build());
            }
            deleteFolder(before);
        } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                 IOException | InvalidResponseException | InvalidKeyException | InternalException |
                 InsufficientDataException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean folderExists(String before) {
        if (!DirUtil.validate(before))
            return false;
        for (Result<Item> it : clientService.getMinioClient().listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(clientService.getRootBucket())
                        .recursive(true)
                        .prefix(before)
                        .build())) {

            try {
                if (it.get().objectName().startsWith(before))
                    return true;
            } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                     IOException | InvalidResponseException | InvalidKeyException | InternalException |
                     InsufficientDataException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
    public List<String> allDirEntries(String entry) {
        var all = new ArrayList<String>();
        for (Result<Item> it : clientService.getMinioClient().listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(clientService.getRootBucket())
                        .recursive(true)
                        .build())) {

            try {
                var filePath = it.get().objectName();
                if (filePath.contains(entry) && DirUtil.getPath(filePath).contains(entry)) {
                    all.add(DirUtil.getPath(filePath));
                }
            } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                     IOException | InvalidResponseException | InvalidKeyException | InternalException |
                     InsufficientDataException e) {
                throw new RuntimeException(e);
            }
        }
        return all;
    }

    public List<Pair> allFileEntries(String entry) {
        var all = new ArrayList<Pair>();
        for (Result<Item> it : clientService.getMinioClient().listObjects(
                ListObjectsArgs
                        .builder()
                        .bucket(clientService.getRootBucket())
                        .recursive(true)
                        .build())) {

            try {
                var filePath = it.get().objectName();
                if (filePath.contains(entry) && FileUtil.getObjectName(filePath).contains(entry)) {
                    all.add(new Pair(filePath, DirUtil.getPath(filePath)));
                }
            } catch (ErrorResponseException | XmlParserException | ServerException | NoSuchAlgorithmException |
                     IOException | InvalidResponseException | InvalidKeyException | InternalException |
                     InsufficientDataException e) {
                throw new RuntimeException(e);
            }
        }
        return all;
    }
}
