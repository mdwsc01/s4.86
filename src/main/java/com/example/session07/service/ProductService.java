package com.example.session07.service;

import com.example.session07.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ProductService {
    private final List<Product> products = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public synchronized Product createProduct(String name,Double price, String image) {
        Long id = idGenerator.getAndIncrement();

        Product product = new Product(id, name, price, image);

        products.add(product);
        return product;
    }

    public synchronized List<Product> getAllProducts(){
        return new ArrayList<>(products);
    }
}
