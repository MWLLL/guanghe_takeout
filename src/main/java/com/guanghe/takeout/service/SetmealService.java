package com.guanghe.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanghe.takeout.dto.SetmealDto;
import com.guanghe.takeout.entity.Setmeal;
import com.guanghe.takeout.mapper.SetmealMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface SetmealService extends IService<Setmeal> {

    //保存套餐和关联菜品信息
    public void saveWithDish(SetmealDto setmealDto);
    //删除套餐和关联的菜品信息
    public void removeWithDish(List<Long> ids);
    //根据id查询套餐和关联菜品信息
    public SetmealDto getByIdWithDish(Long id);
    //根据id修改套餐和对应菜品信息
    void updateByIdWithDish(SetmealDto setmealDto);
    //单个或批量修改套餐状态
    void updateStatus(Integer status, List<Long> ids);
}
