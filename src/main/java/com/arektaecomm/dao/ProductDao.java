package com.arektaecomm.dao;

import com.arektaecomm.model.Product;
import java.util.List;

public interface ProductDao {
    void addProduct(Product p);

    void updateProduct(Product p);

    void deleteProduct(String productId);

    List<Product> fetchAll();
}