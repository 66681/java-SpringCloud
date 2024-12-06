package com.coder.controller;

import com.coder.entity.GeoCode;
import com.coder.service.impl.GeoCodeServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/geocode")
public class GeoCodeController {
    @Resource
    private GeoCodeServiceImpl geoCodeService;
//    4
    @GetMapping
    public Object list(){

        return geoCodeService.list();

    }
   /* @GetMapping("{id}")
    public void getById(@PathVariable int id){

//        return geoCodeService.list();
         geoCodeService.getById(id);
    }
*/

//    1
    @PostMapping
    public Object save(@RequestBody GeoCode geoCode){
        return geoCodeService.save(geoCode);
    }
//    3
    @PutMapping
    public Object update(@RequestBody GeoCode geoCode){
        return geoCodeService.save(geoCode);
    }
//    2
    @DeleteMapping("{id}")
    public Object delete(@PathVariable Integer id){
        return geoCodeService.removeById(id);
    }
    @GetMapping("{id}")
    public Object getById(@PathVariable Integer id){
        return geoCodeService.getById(id);
    }

    @GetMapping("/task-list")
    public String getTaskList() {
        return """
            [
                {
                    "taskId": "001",
                    "taskType": "入库",
                    "startPoint": "A1",
                    "endPoint": "B2",
                    "status": "进行中",
                    "priority": "高",
                    "startTime": "2024-01-01 10:00",
                    "endTime": "2024-01-01 11:00",
                    "executor": "系统",
                    "note": "紧急任务",
                    "operation1": "暂停",
                    "operation2": "取消"
                }
            ]
            """;
    }





}
