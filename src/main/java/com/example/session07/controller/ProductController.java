package com.example.session07.controller;

import com.example.session07.model.Product;
import com.example.session07.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {
    private final ProductService productService;

    @Value("${file.upload-dir}")
    private String uploadDir;

    public ProductController(ProductService productService){
        this.productService = productService;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createProduct(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "price", required = false) Double price,
            @RequestParam(value = "image", required = false) MultipartFile image
    ) throws IOException {
        if(name == null || name.isBlank()){
            return ResponseEntity.badRequest().body("Tên sản phẩm không được để trống");
        }

        if(price == null || !Double.isFinite(price) || price <= 0){
            return ResponseEntity.badRequest().body("Giá sản phẩm phải lớn hơn 0");
        }

        if(image == null || image.isEmpty()){
            return ResponseEntity.badRequest().body("Ảnh sản phẩm là bắt buộc");
        }

        String originalName = image.getOriginalFilename();

        if(originalName == null || originalName.isBlank()){
            return ResponseEntity.badRequest().body("Tên file không hợp lệ");
        }

        String lowerName = originalName.toLowerCase(Locale.ROOT);
        String extension;

        if(lowerName.endsWith(".jpg")){
            extension = ".jpg";
        } else if(lowerName.endsWith(".png")){
            extension = ".png";
        } else{
            return ResponseEntity.badRequest().body("Chỉ chấp nhận ảnh (.png, .jpg)");
        }

        String contentType = image.getContentType();
        boolean validMime = (extension.equals(".jpg")
                            && "image/jpeg".equals(contentType))
                || (extension.equals(".png") && "image/png".equals(contentType));

        if(!validMime){
            return ResponseEntity.badRequest().body("Định dạng ảnh không hợp lệ");
        }

        if(image.getSize() > 1024*1024){
            return ResponseEntity.status(413).body("File quá lớn(Tối đa 1MB)!");
        }

        String fileName = UUID.randomUUID() + extension;

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();

        Files.createDirectories(uploadPath);

        Path filePath = uploadPath.resolve(fileName);

        try(InputStream inputStream = image.getInputStream()) {
            Files.copy(inputStream,filePath);
        }

        Product product;
        try {
            product = productService.createProduct(name.trim(),price,fileName);
        }   catch (RuntimeException ex){
            Files.deleteIfExists(filePath);
            throw ex;
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(product);
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok(productService.getAllProducts());
    }


}
