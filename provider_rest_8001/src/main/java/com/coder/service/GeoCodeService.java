package com.coder.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.coder.entity.GeoCode;
import org.apache.ibatis.annotations.Select;

public interface GeoCodeService extends IService<GeoCode> {
    public void getById(int id);

}
