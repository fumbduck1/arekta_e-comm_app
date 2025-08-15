package com.arektaecomm.service;

import com.arektaecomm.dao.FirebaseOrderDao;
import com.arektaecomm.model.Order;
import com.arektaecomm.util.PDFUtil;
import java.io.File;

public class OrderService {
    private final FirebaseOrderDao dao;

    public OrderService(String databaseUrl) {
        this.dao = new FirebaseOrderDao(databaseUrl);
    }

    public void placeOrder(Order o, String userEmail) throws Exception {
        if (o.getId() == null) {
            dao.createOrder(o);
        } else {
            dao.updateOrder(o);
        }
        File invoice = PDFUtil.generateInvoice(o);
        File invoicesDir = new File("invoices");
        if (!invoicesDir.exists()) {
            invoicesDir.mkdirs();
        }
        File savedInvoice = new File(invoicesDir, invoice.getName());
        java.nio.file.Files.copy(invoice.toPath(), savedInvoice.toPath(),
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
    }

    public java.util.List<Order> fetchAllOrders() {
        return dao.fetchAll();
    }

    public java.util.List<Order> fetchOrdersByUser(String userId) {
        return dao.fetchByUser(userId);
    }
}