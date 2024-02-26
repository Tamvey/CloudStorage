package com.cstorage.service;

import com.cstorage.entity.User;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.*;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Service
public class ClientService {
    @Value("${spring.myminio.host}")
    private String host;
    @Value("${spring.myminio.port}")
    private String port;
    @Value("${spring.myminio.username}")
    private String username;
    @Value("${spring.myminio.password}")
    private String password;
    @Autowired
    UserService userService;
    @Getter
    private MinioClient minioClient;
    private User user;
    @Getter
    private String rootBucket;

    public void buildClient() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        user = userService.findByUsername(authentication.getName());
        minioClient = MinioClient.builder()
                .endpoint("http://" + host + ":" + port)
                .credentials(username, password)
                .build();
        setRootBucket();
    }

    private void setRootBucket() {
        try {
            rootBucket = "user-" + user.getUsername() + "-files";
            if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(rootBucket).build())) {
                minioClient.makeBucket(MakeBucketArgs
                                        .builder()
                                        .bucket(rootBucket)
                                        .build());
            }
        } catch (ErrorResponseException | InsufficientDataException | XmlParserException | ServerException |
                 NoSuchAlgorithmException | IOException | InvalidResponseException | InvalidKeyException |
                 InternalException e) {
            throw new RuntimeException(e);
        }
    }
}
