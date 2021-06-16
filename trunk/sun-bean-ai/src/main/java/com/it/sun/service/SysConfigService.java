package com.it.sun.service;

import com.alibaba.fastjson.JSONObject;
import com.it.common.util.BaseUtil;
import com.it.common.util.task.QuartzManager;
import com.it.sun.entity.SysConfig;
import com.it.sun.entity.example.SysConfigExample;
import com.it.sun.mapper.SysConfigMapper;
import org.apache.commons.lang3.StringUtils;
import org.apdplat.word.WordSegmenter;
import org.apdplat.word.segmentation.Word;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

//获取系统配置的服务
@Service
public class SysConfigService {
    @Autowired
    private SysConfigMapper sysConfigMapper;

    /**
     * 查询 sys_config 表，返回单个
     * @param
     * @return
     */
    public String getParamValue(String paramKey){
        SysConfigExample example = new SysConfigExample();
        SysConfigExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(paramKey)){
            criteria.andParamKeyEqualTo(paramKey);
        }

        List<SysConfig> list = sysConfigMapper.selectByExample(example);
        if(list!=null && list.size()>0 && list.get(0)!=null){
            return list.get(0).getParamValue();
        }else {
            return null;
        }
    }

    /**
     * 查询 sys_config 表，返回列表
     * @param paramKey
     * @return
     */
    public List<SysConfig> getParamValues(String paramKey){
        SysConfigExample example = new SysConfigExample();
        SysConfigExample.Criteria criteria = example.createCriteria();
        if(StringUtils.isNotEmpty(paramKey)){
            criteria.andParamKeyEqualTo(paramKey);
        }
        List<SysConfig> list = sysConfigMapper.selectByExample(example);
        return list;
    }

    /**
     * 新建系统配置
     * @param sc
     * @return
     */
    public int addSysConfig(SysConfig sc){
        return sysConfigMapper.insert(sc);
    }


    /**
     * 修改系统配置
     * @param sc
     * @return
     */
    public int updSysConfig(SysConfig sc){
        return sysConfigMapper.updateByPrimaryKeySelective(sc);
    }


    /**
     * 根据 param_key 删除对应的配置
     * @param param_key
     * @return
     */
    public int delSysConfig(String param_key){
        SysConfigExample example=new SysConfigExample();
        SysConfigExample.Criteria criteria=example.createCriteria();
        criteria.andParamKeyEqualTo(param_key);
        return sysConfigMapper.deleteByExample(example);
    }

    public List<String> task(String str){
        Long startTime=System.currentTimeMillis();

        List<String> ws=new ArrayList<>();//结果

        List<Word> wordsList = WordSegmenter.seg(str);
        if(wordsList!=null && wordsList.size()>0){
            for (Word w:wordsList){
                ws.add(w.getText());
            }
        }

        long stopTime = System.currentTimeMillis();
        System.out.println((stopTime-startTime)*1d/1000d);
        return ws;
    }

}
