package com.coder.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.coder.entity.GeoCode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface GeoCodeMapper extends BaseMapper<GeoCode> {
    @Select("select * from geocode where id = #{id}")
    GeoCode getById(int id);
}
