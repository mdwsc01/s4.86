package com.example.session07.controller;

import com.example.session07.dto.CandidateDTO;
import com.example.session07.service.CandidateService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/candidates")
public class CandidateController {
    private final CandidateService candidateService;

    public CandidateController(CandidateService candidateService) {
        this.candidateService = candidateService;
    }

    @PostMapping(
            value = "/apply",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> apply (
            @Valid @RequestPart("candidate")CandidateDTO candidate,
            @RequestPart("cv") MultipartFile cv
            ) throws IOException {
        String saveFileName =candidateService.saveCv(cv);
        Map<String,Object> response = new LinkedHashMap<>();
                response.put("message","Ứng tuyển thành công");
                response.put("fullName", candidate.getFullName());
                response.put("email", candidate.getEmail());
                response.put("skills",candidate.getSkill());
                response.put("cv",saveFileName);

                return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
