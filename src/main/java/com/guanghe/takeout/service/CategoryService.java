package com.guanghe.takeout.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.guanghe.takeout.entity.Category;

public interface CategoryService extends IService<Category> {
    public void remove(Long id);
}
