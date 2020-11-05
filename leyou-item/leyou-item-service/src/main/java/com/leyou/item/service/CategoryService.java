package com.leyou.item.service;

import com.leyou.item.mapper.CategoryMapper;
import com.leyou.item.pojo.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by jing_tian on 2020/9/28.
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryMapper mCategoryMapper;

    /**
     * 根据父节点 查询子节点
     * @param pid
     * @return
     */
    public List<Category> queryCategoriesByPid(Long pid) {
        Category category = new Category();
        category.setParentId(pid);
        return mCategoryMapper.select(category);
    }


    public List<String> queryNamesByIds(List<Long> ids) {
        List<Category> categories = this.mCategoryMapper.selectByIdList(ids);
        return categories.stream().map(category -> category.getName()).collect(Collectors.toList());
    }
}
