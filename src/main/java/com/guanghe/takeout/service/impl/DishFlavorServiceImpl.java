package com.guanghe.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanghe.takeout.entity.DishFlavor;
import com.guanghe.takeout.mapper.DishFlavorMapper;
import com.guanghe.takeout.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {
}
