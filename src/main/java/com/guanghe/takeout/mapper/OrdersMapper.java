package com.guanghe.takeout.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.guanghe.takeout.entity.Orders;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrdersMapper extends BaseMapper<Orders> {
}
