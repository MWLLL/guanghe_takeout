package com.guanghe.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanghe.takeout.entity.Orders;

public interface OrdersService extends IService<Orders> {
    //用户下单
    public void submit(Orders orders);
}
