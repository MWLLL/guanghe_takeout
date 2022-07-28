package com.guanghe.takeout.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanghe.takeout.common.R;
import com.guanghe.takeout.dto.DishDto;
import com.guanghe.takeout.entity.Dish;
import com.guanghe.takeout.entity.DishFlavor;
import com.guanghe.takeout.mapper.DishMapper;
import com.guanghe.takeout.service.DishFlavorService;
import com.guanghe.takeout.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    //文件路径
    @Value("${guanghe.path}")
    private String basePath;

    @Autowired
    private DishFlavorService dishFlavorService;

    /**
     * 新增菜品同时保存对应的口味数据
     * @param dishDto
     */
    @Override
    @Transactional
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到dish表
        this.save(dishDto);

        Long dishId = dishDto.getId();//菜品id

        //菜品口味
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
           item.setDishId(dishId);
           return item;
        }).collect(Collectors.toList());

        //保存菜品口味数据到dish_flavor
        dishFlavorService.saveBatch(flavors);

    }

    /**
     * 根据id查询菜品信息和对应口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);

        //拷贝
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //查询菜品对应口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);

        dishDto.setFlavors(flavors);

        return dishDto;
    }

    /**
     * 更新菜品信息同时更新口味信息
     * @param dishDto
     */
    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表基本信息
        this.updateById(dishDto);

        //清理当前菜品对应口味信息dish_flavor表的delete操作
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);

        //添加当前提交过来的口味数据dish_flavor表的insert操作
        List<DishFlavor> flavors = dishDto.getFlavors();

        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());

        dishFlavorService.saveBatch(flavors);
    }

    /**
     * 删除菜品信息时同时删除对应口味信息
     * @param ids
     */
    @Override
    @Transactional
    public Integer removeWithFlavor(List<Long> ids) {

        //删除对应菜品图片
        for (int i=0 ; i<ids.size(); i++){
            Long id = ids.get(i);
            Dish dish = this.getById(id);//查询菜品基本信息
            if (dish.getStatus()==1){//判断当前菜品是否是启售状态，如果是启售则不能删除
                return 0;
            }
            File file = new File(basePath+dish.getImage());
            if (file.exists()){
                file.delete();
            }
        }
        //删除菜品
        this.removeByIds(ids);

        Map<String,Object> columnMap = new HashMap<>();
        //循环遍历删除口味信息
        for (Long id : ids){
            columnMap.put("dish_id",id);
            dishFlavorService.removeByMap(columnMap);
        }
        return 1;
    }

    /**
     * 更新菜品状态
     * @param status
     * @param ids
     */
    @Override
    public void updateStatus(Integer status, List<Long> ids) {

        List<Dish> dishList = new ArrayList<>();

        for (int i=0 ; i<ids.size(); i++){
            dishList.add(new Dish());
            dishList.get(i).setId(ids.get(i));
            dishList.get(i).setStatus(status);
        }
        this.updateBatchById(dishList);
    }
}
