package com.leyou.item.mapper;

import com.leyou.item.pojo.Category;

import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;
import tk.mybatis.mapper.common.Mapper;

/**
 * Created by jing_tian on 2020/9/28.
 */
public interface CategoryMapper extends Mapper<Category> , SelectByIdListMapper<Category , Long> {
}
