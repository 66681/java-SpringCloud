package com.coder.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
@TableName("geocode")
@Data
public class GeoCode {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String name;
    private String road;
    private String address;
    private String note;
}
