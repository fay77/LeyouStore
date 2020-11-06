package com.leyou.item.service;

import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import com.leyou.item.pojo.SpecGroup;
import com.leyou.item.pojo.SpecParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import sun.security.ssl.Record;

/**
 * Created by jing_tian on 2020/10/19.
 */
@Service
public class SpecificationService {

    @Autowired
    private SpecGroupMapper mSpecGroupMapper;

    @Autowired
    private SpecParamMapper mSpecParamMapper;


    /**
     * 根据分类id查询参数组
     * @param cid
     * @return
     */
    public List<SpecGroup> queryGroupsByCid(long cid) {
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        return this.mSpecGroupMapper.select(record);
    }

    /**
     * 根据条件查询规格参数
     * @param gid
     * @param cid
     * @param generic
     * @param searching
     * @return
     */
    public List<SpecParam> queryParam(Long gid , Long cid , Boolean generic , Boolean searching) {
        SpecParam param = new SpecParam();
        param.setGroupId(gid);
        param.setCid(cid);
        param.setGeneric(generic);
        param.setSearching(searching);
        return mSpecParamMapper.select(param);
    }

    @Transactional
    public void saveParam(SpecParam specParam) {
        mSpecParamMapper.insert(specParam);
    }
}
