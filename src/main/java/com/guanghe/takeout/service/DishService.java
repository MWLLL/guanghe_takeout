package com.guanghe.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanghe.takeout.dto.DishDto;
import com.guanghe.takeout.entity.Dish;

import java.util.List;

public interface DishService extends IService<Dish> {

    //新增菜品同时插入菜品对应口味数据，需要操作两张表，dish和dish_flavor
    public void saveWithFlavor(DishDto dishDto);
    //根据id查询菜品信息和对应口味信息
    public DishDto getByIdWithFlavor(Long id);
    //更新菜品信息同时更新口味信息
    public void updateWithFlavor(DishDto dishDto);
    //删除菜品信息时同时删除对应口味信息
    public void removeWithFlavor(List<Long> ids);
    //更新菜品状态
    public void updateStatus(Integer status, List<Long> ids);
}
