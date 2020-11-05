package com.leyou.item.mapper;

import com.leyou.item.pojo.Brand;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

import tk.mybatis.mapper.common.Mapper;

/**
 * Created by jing_tian on 2020/9/29.
 */
public interface BrandMapper extends Mapper<Brand> {

    @Insert("insert into tb_category_brand (category_id,brand_id) values (#{cid} , #{bid})")
    void insertCategoryAndBrand(@Param("cid") Long cid,@Param("bid") Long id);

    @Select("SELECT * from tb_brand brand , tb_category_brand category WHERE brand.id = category.brand_id and category.category_id = #{cid}")
    List<Brand> selectBrandsByCid(Long cid);
}
