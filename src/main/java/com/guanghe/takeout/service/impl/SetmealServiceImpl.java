package com.guanghe.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanghe.takeout.entity.Setmeal;
import com.guanghe.takeout.mapper.SetmealMapper;
import com.guanghe.takeout.service.SetmealService;
import org.springframework.stereotype.Service;

@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService{
}
