package com.guanghe.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.guanghe.takeout.common.BaseContext;
import com.guanghe.takeout.common.R;
import com.guanghe.takeout.entity.ShoppingCart;
import com.guanghe.takeout.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 购物车
 */
@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 新增购物车
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("添加购物车...");
        //设置用户id，指定当前是哪个用户的购物车数据
        Long currentId = BaseContext.getCurrentId();
        shoppingCart.setUserId(currentId);

        //查询当前添加的菜品或套餐有没有在数据库
        Long dishId = shoppingCart.getDishId();

        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        if (dishId != null){
            //添加到购物车的是菜品
            queryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            //添加到购物车的是套餐
            queryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart shoppingCartOne = shoppingCartService.getOne(queryWrapper);
        if (shoppingCartOne != null){
            //如果有则数据库number字段+1
            Integer number = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(number + 1);
            shoppingCartService.updateById(shoppingCartOne);
        }else {
            //如果不存在则添加到购物车，数量默认是1
            shoppingCart.setNumber(1);
            shoppingCartService.save(shoppingCart);
            shoppingCartOne = shoppingCart;
        }

        return R.success(shoppingCartOne);
    }
}
