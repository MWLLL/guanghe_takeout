package com.guanghe.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanghe.takeout.common.R;
import com.guanghe.takeout.dto.DishDto;
import com.guanghe.takeout.entity.Category;
import com.guanghe.takeout.entity.Dish;
import com.guanghe.takeout.entity.DishFlavor;
import com.guanghe.takeout.service.CategoryService;
import com.guanghe.takeout.service.DishFlavorService;
import com.guanghe.takeout.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 菜品管理
 */
@Slf4j
@RestController
@RequestMapping("/dish")
public class DishController {

    @Autowired
    private DishService dishService;
    @Autowired
    private DishFlavorService dishFlavorService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增菜品
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody DishDto dishDto){
        log.info("新增菜品...");

        dishService.saveWithFlavor(dishDto);

        //清理菜品的Redis缓存
        //Set keys = redisTemplate.keys("dish_*");//清理所有
        String key = "dish_"+dishDto.getCategoryId()+"_1";//清理指定分类菜品缓存
        redisTemplate.delete(key);

        return R.success("新增菜品成功");
    }

    /**
     * 菜品信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){

        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();

        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name!=null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);

        dishService.page(pageInfo,queryWrapper);

        //对象拷贝
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");//忽略records数组

        List<Dish> records = pageInfo.getRecords();
        List<DishDto> list = records.stream().map((item->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            return dishDto;
        })).collect(Collectors.toList());

        dishDtoPage.setRecords(list);


        return R.success(dishDtoPage);
    }

    /**
     * 根据id查询菜品信息和对应口味信息用于表单回显数据
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> get(@PathVariable Long id){

        DishDto dishDto = dishService.getByIdWithFlavor(id);

        return R.success(dishDto);
    }

    /**
     * 修改菜品
     * @param dishDto
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody DishDto dishDto){
        log.info("修改菜品...");

        dishService.updateWithFlavor(dishDto);

        //清理菜品的Redis缓存
        //Set keys = redisTemplate.keys("dish_*");//清理所有
        String key = "dish_"+dishDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("修改菜品成功");
    }

    /**
     * 删除菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){

        log.info("删除菜品，id为：{}",ids);
        int flag = dishService.removeWithFlavor(ids);
        if (flag==1){
            return R.success("删除菜品成功");
        }
        return R.error("启售中的菜品不能删除");
    }

    /**
     * 单个或批量修改菜品状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,@RequestParam(value = "ids",required=false) List<Long> ids){

        log.info("修改菜品状态...");
        dishService.updateStatus(status,ids);
        //清理菜品的Redis缓存
        Set keys = redisTemplate.keys("dish_*");//清理所有
        redisTemplate.delete(keys);

        return R.success("更新菜品状态成功");
    }

    /**
     * 根据条件查询对应菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> list(Dish dish){
//        //构造查询条件
//        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId,dish.getCategoryId());
//        //查询状态为1的(起售状态的菜品)
//        queryWrapper.eq(Dish::getStatus,1);
//        //添加排序条件
//        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);
//
//        List<Dish> list = dishService.list(queryWrapper);
//
//        return R.success(list);
//    }
    @GetMapping("/list")
    public R<List<DishDto>> list(Dish dish){

        List<DishDto> dishDtoList = null;
        //动态构造key
        String key = "dish_"+dish.getCategoryId()+"_1";//dish_2131231231_1
        //从Redis中获取缓存数据
        dishDtoList = (List<DishDto>) redisTemplate.opsForValue().get(key);
        if (dishDtoList!=null){
            //如果存在，直接返回，无需查询数据库
            return R.success(dishDtoList);
        }
        //如果不存在，需要查询数据库，将查到的数据缓存到Redis中
        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null, Dish::getCategoryId,dish.getCategoryId());
        //查询状态为1的(起售状态的菜品)
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort).orderByDesc(Dish::getUpdateTime);

        List<Dish> list = dishService.list(queryWrapper);

        dishDtoList = list.stream().map((item->{
            DishDto dishDto = new DishDto();
            BeanUtils.copyProperties(item,dishDto);

            Long categoryId = item.getCategoryId();//分类id
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();
                dishDto.setCategoryName(categoryName);
            }
            Long dishId = item.getId();//菜品id
            LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(DishFlavor::getDishId,dishId);
            List<DishFlavor> dishFlavors = dishFlavorService.list(lambdaQueryWrapper);
            dishDto.setFlavors(dishFlavors);


            return dishDto;
        })).collect(Collectors.toList());

        //数据缓存到Redis中
        redisTemplate.opsForValue().set(key,dishDtoList,60l, TimeUnit.MINUTES);

        return R.success(dishDtoList);
    }
}
