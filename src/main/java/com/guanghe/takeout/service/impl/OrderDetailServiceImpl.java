package com.guanghe.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanghe.takeout.entity.OrderDetail;
import com.guanghe.takeout.mapper.OrderDetailMapper;
import com.guanghe.takeout.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
