package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.reggie.common.R;
import com.example.reggie.entity.Category;
import com.example.reggie.entity.Employee;
import com.example.reggie.service.CategoryService;
import com.example.reggie.service.impl.CategoryServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author HHB
 * @Date: 2022/7/6 15:09
 * @Description: 分类管理
 * @Version 1.0
 */
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryService categoryService;

    /***
     * 添加菜品
     * @param category
     * @return
     */
    @PostMapping
    public R<String> saveCategory(@RequestBody Category category){

        categoryService.save(category);
        return R.success("添加菜品成功");
    }

    /***
     * 显示所有菜品
     * @param page
     * @param pageSize
     * @return
     */
    @RequestMapping(value = "/page", method = RequestMethod.GET)
    public R<Page> page( int page, int pageSize){

        //构造分页器
        Page pageInfo = new Page(page, pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();

        //添加排序条件(根据创建时间)
        queryWrapper.orderByDesc(Category::getUpdateTime);
        //执行查询
        categoryService.page(pageInfo, queryWrapper);
        return R.success(pageInfo);

    }

    /***
     * 修改菜品分类->判断重复
     */
    @RequestMapping(method = RequestMethod.PUT)
    public R<String> updateCategory(@RequestBody Category category){

        categoryService.updateById(category);
        if (category != null){
            return R.success("修改菜品成功");
        }
        return R.error("获取员工信息失败");
    }

    /***
     * 删除菜品分类
     * @param ids
     * @return
     */
    @RequestMapping(method = RequestMethod.DELETE)
    public R<String> deleteCategory(Long ids){

//        categoryService.removeById(ids);
        categoryService.removeCategory(ids);

        return R.success("删除菜品分类成功");
    }

    /***
     * 获取添加菜品的菜品分类列表
     */
    @RequestMapping(value = "/list",method = RequestMethod.GET)
    public R<List<Category>> getList(Category category){

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper();
        //构造条件
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        queryWrapper.orderByDesc(Category::getUpdateTime);

        List<Category> list = categoryService.list(queryWrapper);
        return R.success(list);
    }
}
