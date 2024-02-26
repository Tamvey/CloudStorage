package com.cstorage.dto.file;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
public class FileDTO {
    private List<MultipartFile> files;
}
