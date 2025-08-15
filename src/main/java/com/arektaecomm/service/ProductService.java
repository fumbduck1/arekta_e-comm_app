package com.arektaecomm.service;

import com.arektaecomm.dao.FirebaseProductDao;
import com.arektaecomm.model.Product;
import java.util.List;

public class ProductService {
    private final FirebaseProductDao dao;

    public ProductService(String databaseUrl) {
        this.dao = new FirebaseProductDao(databaseUrl);
    }

    public List<Product> getAllProducts() {
        try {
            return dao.fetchAll();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void addOrUpdate(Product p) {
        try {
            if (p.getId() == null) {
                dao.addProduct(p);
            } else {
                dao.updateProduct(p);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void remove(String id) {
        try {
            dao.deleteProduct(id);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}