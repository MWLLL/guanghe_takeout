package com.guanghe.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanghe.takeout.common.CustomException;
import com.guanghe.takeout.entity.Category;
import com.guanghe.takeout.entity.Dish;
import com.guanghe.takeout.entity.Setmeal;
import com.guanghe.takeout.mapper.CategoryMapper;
import com.guanghe.takeout.service.CategoryService;
import com.guanghe.takeout.service.DishService;
import com.guanghe.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    DishService dishService;
    @Autowired
    SetmealService setmealService;

    /**
     * 根据id删除分类，在删除之前判断分类是否关联菜品或者套餐
     * @param id
     */
    @Override
    public void remove(Long id) {

        //查询分类是否关联了菜品，如果关联抛出业务异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId,id);
        int count1 = dishService.count(dishLambdaQueryWrapper);
        //查询分类是否关联了菜品，如果关联抛出业务异常
        if (count1 > 0){
            //已经关联菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }

        //查询分类是否关联了套餐，如果关联抛出业务异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId,id);
        int count2 = setmealService.count(setmealLambdaQueryWrapper);
        if (count2 > 0){
            //已经关联套餐，抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除分类
        super.removeById(id);

    }
}
