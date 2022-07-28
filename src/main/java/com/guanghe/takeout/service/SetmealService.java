package com.guanghe.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanghe.takeout.dto.SetmealDto;
import com.guanghe.takeout.entity.Setmeal;
import com.guanghe.takeout.mapper.SetmealMapper;
import org.springframework.stereotype.Service;

@Service
public interface SetmealService extends IService<Setmeal> {

    //保存套餐和关联菜品信息
    public void saveWithDish(SetmealDto setmealDto);
}
