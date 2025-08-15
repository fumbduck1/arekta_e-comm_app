package com.arektaecomm.dao;

import com.arektaecomm.model.Order;
import java.util.List;

public interface OrderDao {
    void createOrder(Order o);

    void updateOrder(Order o);

    List<Order> fetchByUser(String userId);

    List<Order> fetchAll();
}