package com.coder.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.coder.entity.GeoCode;
import com.coder.mapper.GeoCodeMapper;
import com.coder.service.GeoCodeService;
import jakarta.annotation.Resource;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

@Service
public class GeoCodeServiceImpl extends ServiceImpl<GeoCodeMapper, GeoCode> implements GeoCodeService {
    @Resource  // 或者 @Autowired
    private GeoCodeMapper geoCodeMapper;

    @Override
    public void getById(int id) {
        GeoCode geoCode = geoCodeMapper.selectById(id);
        System.out.println("查询结果：" + geoCode);
    }


}
