package com.guanghe.takeout.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.guanghe.takeout.common.R;
import com.guanghe.takeout.dto.SetmealDishDto;
import com.guanghe.takeout.dto.SetmealDto;
import com.guanghe.takeout.entity.Category;
import com.guanghe.takeout.entity.Dish;
import com.guanghe.takeout.entity.Setmeal;
import com.guanghe.takeout.entity.SetmealDish;
import com.guanghe.takeout.service.CategoryService;
import com.guanghe.takeout.service.DishService;
import com.guanghe.takeout.service.SetmealDishService;
import com.guanghe.takeout.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private DishService dishService;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 新增套餐
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto){
        log.info("新增套餐...");

        setmealService.saveWithDish(setmealDto);

        String key = "setmeal_"+setmealDto.getCategoryId()+"_1";
        redisTemplate.delete(key);

        return R.success("新增套餐成功");
    }

    /**
     * 套餐分页查询
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(Integer page, Integer pageSize, String name){
        Page<Setmeal> pageInfo = new Page(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();

        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件,根据name进行like模糊查询
        queryWrapper.like(name != null,Setmeal::getName,name);
        //添加排序条件，根据更新时间进行降序排列
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        //查询套餐信息，但数据里没有分类名称属性
        setmealService.page(pageInfo,queryWrapper);

        //添加分类名称属性
        //对象拷贝,将pageInfo里的数据拷贝到dtoPage中,不拷贝records数据
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        //接收pageInfo中的records数据
        List<Setmeal> records = pageInfo.getRecords();
        //将records数据拷贝到声明的setmealDto中，然后设置setmealDto中的分类名称属性值，再用list数组接收
        List<SetmealDto> list = records.stream().map((item->{
            SetmealDto setmealDto = new SetmealDto();
            //拷贝其他数据
            BeanUtils.copyProperties(item,setmealDto);
            //获取分类id
            Long categoryId = item.getCategoryId();
            //根据分类id查询分类信息
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                String categoryName = category.getName();//分类名称
                setmealDto.setCategoryName(categoryName);//设置分类名称
            }
            return setmealDto;
        })).collect(Collectors.toList());
        //设置dtoPage中的records值，此时records中有分类名称数据
        dtoPage.setRecords(list);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> delete(@RequestParam List<Long> ids){
        log.info("删除套餐：ids = {}",ids);
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 根据id查询套餐信息和对应菜品信息用于表单回显数据
     */
    @GetMapping("/{id}")
    public R<SetmealDto> get(@PathVariable Long id){
        SetmealDto setmealDto = setmealService.getByIdWithDish(id);
        return R.success(setmealDto);
    }

    /**
     * 套餐更新
     * @return
     */
    @PutMapping
    public R<String> update(@RequestBody SetmealDto setmealDto){
        log.info("修改套餐...");
        setmealService.updateByIdWithDish(setmealDto);
        //清理Redis缓存
        String key = "setmeal_"+setmealDto.getCategoryId()+"_1";
        redisTemplate.delete(key);
        return R.success("更新套餐成功");
    }

    /**
     * 单个或批量修改菜品状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> updateStatus(@PathVariable Integer status,@RequestParam List<Long> ids){

        log.info("修改套餐售卖状态...");
        setmealService.updateStatus(status,ids);

        Set keys = redisTemplate.keys("setmeal_*");//清理所有
        redisTemplate.delete(keys);

        return R.success("更新套餐状态成功");
    }

    /**
     * 移动端获取套餐列表信息
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> list(Setmeal setmeal){

        List<Setmeal> setmeals = null;
        String key = "setmeal_"+setmeal.getCategoryId()+"_"+setmeal.getStatus();
        //判断Redis缓存中是否存在数据
        setmeals = (List<Setmeal>)redisTemplate.opsForValue().get(key);
        if (setmeals != null){//如果存在，直接返回，无需查询数据库
            return R.success(setmeals);
        }
        //如果不存在，需要查询数据库，将查到的数据缓存到Redis中
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(Setmeal::getStatus,setmeal.getStatus());
        setmeals = setmealService.list(queryWrapper);

        //数据缓存到Redis中
        redisTemplate.opsForValue().set(key,setmeals,60l, TimeUnit.MINUTES);

        return R.success(setmeals);
    }

    /**
     * 移动端套餐详情页
     * @param id
     * @return
     */
    @GetMapping("/dish/{id}")
    public R<List<SetmealDishDto>> getDish(@PathVariable Long id){
        LambdaQueryWrapper<SetmealDish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> dishes = setmealDishService.list(queryWrapper);

        List<SetmealDishDto> dishDtoList = dishes.stream().map((item->{
            SetmealDishDto dishDto = new SetmealDishDto();
            //拷贝数据
            BeanUtils.copyProperties(item,dishDto);
            //获取菜品id
            Long dishId = item.getDishId();
            //根据菜品id查询菜品信息
            Dish dish = dishService.getById(dishId);
            if (dish != null){
                String dishImgName = dish.getImage();//照片名称
                dishDto.setImage(dishImgName);//设置分类名称
            }
            return dishDto;
        })).collect(Collectors.toList());



        return R.success(dishDtoList);
    }
}
