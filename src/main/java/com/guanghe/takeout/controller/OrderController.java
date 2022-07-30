package com.guanghe.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanghe.takeout.common.BaseContext;
import com.guanghe.takeout.common.R;
import com.guanghe.takeout.dto.DishDto;
import com.guanghe.takeout.dto.OrdersDto;
import com.guanghe.takeout.entity.Category;
import com.guanghe.takeout.entity.Dish;
import com.guanghe.takeout.entity.OrderDetail;
import com.guanghe.takeout.entity.Orders;
import com.guanghe.takeout.service.OrderDetailService;
import com.guanghe.takeout.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单管理
 */
@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单提交：{}",orders);
        ordersService.submit(orders);

        return R.success("下单成功");
    }

    /**
     * 后台订单明细
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String number, String beginTime, String endTime){

        Page<Orders> pageInfo = new Page<>(page,pageSize);

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        if (number != null){
            queryWrapper.eq(Orders::getNumber,number);
        }
        if (beginTime != null && endTime != null){
            queryWrapper.ge(Orders::getOrderTime,beginTime);// WHERE (order_time >= ? AND order_time <= ?)
            queryWrapper.le(Orders::getOrderTime,endTime);
            queryWrapper.orderByDesc(Orders::getOrderTime);
        }

        ordersService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 修改订单状态
     * @param orders
     * @return
     */
    @PutMapping
    public R<String> updateStatus(@RequestBody Orders orders){
        log.info("修改订单状态");//订单状态 1待付款，2待派送，3已派送，4已完成，5已取消
        ordersService.updateById(orders);
        return R.success("更新订单状态成功");
    }

    /**
     * 移动端订单列表页
     * @param page
     * @param pageInfo
     * @return
     */
    @GetMapping("/userPage")
    public R<Page> userPage(Integer page, Integer pageSize){
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDto = new Page<>();

        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByDesc(Orders::getOrderTime);
        queryWrapper.eq(Orders::getUserId, BaseContext.getCurrentId());

        ordersService.page(pageInfo,queryWrapper);//获得orders数据

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,ordersDto,"records");//忽略records数组

        List<Orders> records = pageInfo.getRecords();
        List<OrdersDto> list = records.stream().map((item->{
            OrdersDto dto = new OrdersDto();
            BeanUtils.copyProperties(item,dto);//拷贝Orders数据

            Long orderId = item.getId();//订单id
            LambdaQueryWrapper<OrderDetail> orderDetailLambdaQueryWrapper = new LambdaQueryWrapper<>();
            orderDetailLambdaQueryWrapper.eq(OrderDetail::getOrderId,orderId);
            List<OrderDetail> orderDetail = orderDetailService.list(orderDetailLambdaQueryWrapper);
            dto.setOrderDetails(orderDetail);
            return dto;
        })).collect(Collectors.toList());

        ordersDto.setRecords(list);

        return R.success(ordersDto);

    }
}
