package com.guanghe.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanghe.takeout.dto.SetmealDto;
import com.guanghe.takeout.entity.Setmeal;
import com.guanghe.takeout.entity.SetmealDish;
import com.guanghe.takeout.mapper.SetmealMapper;
import com.guanghe.takeout.service.SetmealDishService;
import com.guanghe.takeout.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{

    @Autowired
    private SetmealDishService setmealDishService;


    /**
     * 保存套餐和关联菜品信息
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐基本信息，操作setmeal表
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item->{
            item.setSetmealId(setmealDto.getId());
            return item;
        })).collect(Collectors.toList());

        //保存套餐和菜品关联信息，操作setmeal_dish
        setmealDishService.saveBatch(setmealDishes);
    }
}
