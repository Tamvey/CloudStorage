package com.cstorage.repository;

import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DirRepository {
    List<String> listObjects(String path);
    List<String> listFolders(String path);
    List<String> listRootObjects();
    List<String> listRootFolders();
    void addFolder(String path);
    void deleteFolder(String path);
    void renameFolder(String before, String after);
}
