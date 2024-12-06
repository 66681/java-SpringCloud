package com.example.consumer_rest_9001.controller;

import com.example.consumer_rest_9001.entity.GeoCode;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/webgis")
@Slf4j
public class WebGisController {
    @Value("${server.port}")
    String port;
    private static final String URL = "http://provider-service-rest/geocode";
    
    @Resource
    private RestTemplate restTemplate;
    
    @GetMapping("/list")
    public Object list() {
        log.info("调用地理编码服务");
            Object result = restTemplate.getForObject(URL, Object.class);
            log.info("调用成功"+port);
            return result;
    }

    @PostMapping("/save")
    public Object save(@RequestBody GeoCode geoCode){

        log.info("调用保存服务");
       return restTemplate.postForObject(URL,geoCode,Object.class);

    }
    @DeleteMapping("/del/{id}")
    public Object del(@PathVariable int id){
        log.info("调用删除服务");
        restTemplate.delete(URL+"/"+id);
        return true;

    }


}
