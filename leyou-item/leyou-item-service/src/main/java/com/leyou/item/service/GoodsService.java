package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.bo.SpuBo;
import com.leyou.item.mapper.BrandMapper;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import com.leyou.item.mapper.StockMapper;
import com.leyou.item.pojo.Brand;
import com.leyou.item.pojo.Sku;
import com.leyou.item.pojo.Spu;
import com.leyou.item.pojo.SpuDetail;
import com.leyou.item.pojo.Stock;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Date;
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

    @Autowired
    private SkuMapper mSkuMapper;

    @Autowired
    private StockMapper mStockMapper;


    /**
     * 根据条件分页查询SPU
     *
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


        return new PageResult<>(pageInfo.getTotal(), spuBos);
    }


    /**
     * 新增商品
     *
     * @param spuBo spu
     */
    @Transactional
    public void saveGoods(SpuBo spuBo) {

        //新增spu
        spuBo.setId(null);
        spuBo.setSaleable(true);
        spuBo.setValid(true);
        spuBo.setCreateTime(new Date());
        spuBo.setLastUpdateTime(spuBo.getCreateTime());
        this.mSpuMapper.insertSelective(spuBo);

        //新增spuDetail
        SpuDetail spuDetail = spuBo.getSpuDetail();
        spuDetail.setSpuId(spuBo.getId());
        this.mSpuDetailMapper.insertSelective(spuDetail);


        saveSkuAndStock(spuBo);


    }

    private void saveSkuAndStock(SpuBo spuBo) {
        spuBo.getSkus().forEach(sku -> {
            //新增Sku
            sku.setId(null);
            sku.setSpuId(spuBo.getId());
            sku.setCreateTime(new Date());
            sku.setLastUpdateTime(sku.getCreateTime());
            this.mSkuMapper.insertSelective(sku);

            //新增Stock
            Stock stock = new Stock();
            stock.setStock(sku.getStock());
            stock.setSkuId(sku.getId());
            this.mStockMapper.insertSelective(stock);
        });
    }


    /**
     * 根据spuId 查询spuDetail
     * @param spuId
     * @return
     */
    public SpuDetail querySpuDetailBySpuId(Long spuId) {
        return this.mSpuDetailMapper.selectByPrimaryKey(spuId);
    }


    /**
     * 根据spuid 查询sku集合
     * @param spuId
     * @return
     */
    public List<Sku> querySkusBySpuId(Long spuId) {
        Sku record = new Sku();
        record.setSpuId(spuId);
        List<Sku> skus = this.mSkuMapper.select(record);
       skus.forEach(sku -> {
           Stock stock = this.mStockMapper.selectByPrimaryKey(sku.getId());
           sku.setStock(stock.getStock());
       });
        return skus;
    }

    /**
     * 更新商品信息
     * @param spuBo
     */
    @Transactional
    public void updateGoods(SpuBo spuBo) {
        //根据spuId 查询要删除的sku
        Sku record = new Sku();
        record.setSpuId(spuBo.getId());
        List<Sku> skus = mSkuMapper.select(record);
        skus.forEach(sku -> {
            //删除stock
            mStockMapper.deleteByPrimaryKey(sku.getId());

        });
        //删除sku
        Sku sku = new Sku();
        sku.setSpuId(spuBo.getId());
        mSkuMapper.delete(sku);

        //新增sku stock
        saveSkuAndStock(spuBo);

        //更新spu和spuDetail
        spuBo.setCreateTime(null);
        spuBo.setValid(null);
        spuBo.setSaleable(null);
        spuBo.setLastUpdateTime(new Date());
        mSpuMapper.updateByPrimaryKeySelective(spuBo);
        mSpuDetailMapper.updateByPrimaryKeySelective(spuBo.getSpuDetail());

    }
}
