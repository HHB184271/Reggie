package com.example.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.reggie.entity.Category;

/**
 * @Author HHB
 * @Date: 2022/7/6 9:38
 * @Description: TODO
 * @Version 1.0
 */

public interface CategoryService extends IService<Category> {

    void removeCategory(Long ids);

}
