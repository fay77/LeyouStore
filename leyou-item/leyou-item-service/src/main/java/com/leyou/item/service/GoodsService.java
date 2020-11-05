package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Spu;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import tk.mybatis.mapper.entity.Example;

/**
 * Created by jing_tian on 2020/11/5.
 */
@Service
public class GoodsService {

    @Autowired
    private SpuMapper mSpuMapper;

    @Autowired
    private SpuDetailMapper mSpuDetailMapper;

    @Autowired
    private BrandMapper mBrandMapper;

    @Autowired
    private CategoryService mCategoryService;


    /**
     * 根据条件分页查询SPU
     * @param key
     * @param saleable
     * @param page
     * @param rows
     * @return
     */
    public PageResult<SpuBo> querySpuByPage(String key, Boolean saleable, Integer page, Integer rows) {

        Example example = new Example(Spu.class);
        //添加查询条件
        Example.Criteria criteria = example.createCriteria();
        if (StringUtils.isNotBlank(key)) {
            criteria.andLike("title", "%" + key + "%");
        }

        //添加saleable 上下架的过滤条件
        if (saleable != null) {
            criteria.andEqualTo("saleable", saleable);
        }

        //添加分页
        PageHelper.startPage(page, rows);

        //执行查询，获取SPU集合
        List<Spu> spus = mSpuMapper.selectByExample(example);

        PageInfo<Spu> pageInfo = new PageInfo<>(spus);

        //spu集合转换成spubo集合
        List<SpuBo> spuBos = spus.stream().map(spu -> {
            SpuBo spuBo = new SpuBo();
            BeanUtils.copyProperties(spu, spuBo);

            //查询品牌名称
            Brand brand = this.mBrandMapper.selectByPrimaryKey(spu.getBrandId());
            spuBo.setBname(brand.getName());
            //查询分类名称
            List<String> names = this.mCategoryService.queryNamesByIds(Arrays.asList(spu.getCid1(), spu.getCid2(), spu.getCid3()));
            spuBo.setCname(StringUtils.join(names, "-"));
            return spuBo;

        }).collect(Collectors.toList());

        //返回pageResult<SpuBo>


        return new PageResult<>(pageInfo.getTotal() ,spuBos);
    }
}
