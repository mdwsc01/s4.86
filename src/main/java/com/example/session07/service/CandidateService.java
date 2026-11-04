package com.example.session07.service;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.UUID;

@Service
public class CandidateService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    public String saveCv(MultipartFile cv) throws IOException {
        if(cv == null || cv.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"File CV là bắt buộc");
        }

        String originalName = cv.getOriginalFilename();

        if(originalName == null
            || !originalName.toLowerCase(Locale.ROOT).endsWith(".pdf")) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Chỉ chấp nhận file PDF");
        }

        if(!"application/pdf".equals(cv.getContentType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CV phải có Content-Type application/pdf");
        }

        if(cv.getSize() > 1024*1024){
            throw new ResponseStatusException(HttpStatus.valueOf(413),"File CV quá lớn(Tối đa 1MB)!");
        }

        byte[] signature = new byte[5];

        try(InputStream input = cv.getInputStream()) {
            if(input.readNBytes(signature,0,5) != 5
                || signature[0] != '%'
                    || signature[1] != 'P'
                    || signature[2] != 'D'
                    || signature[3] != 'F'
                    || signature[4] != '-') {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,"Nội dung file không phải PDF");
            }
        }

        String newFileName = UUID.randomUUID()+ ".pdf";
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(newFileName);

        try(InputStream input = cv.getInputStream()) {
            Files.copy(
                    input,
                    filePath,
                    StandardCopyOption.REPLACE_EXISTING
            );
        }

        return newFileName;
    }

}
