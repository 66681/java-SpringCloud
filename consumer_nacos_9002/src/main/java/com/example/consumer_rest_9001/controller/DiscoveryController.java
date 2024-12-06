package com.example.consumer_rest_9001.controller;

import jakarta.annotation.Resource;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.function.Consumer;
@RestController
@RequestMapping("/discovery")
public class DiscoveryController {
    @Resource
    private DiscoveryClient discoveryClient;
    @GetMapping
    public void showDiscovertInfo(){
        //nacos注册中心的所有服务名称
        List<String> services = discoveryClient.getServices();
        services.forEach(servicename->{
            List<ServiceInstance> instances = discoveryClient.getInstances(servicename);
            instances.forEach(instance->{
                System.out.println("serviceId: "+instance.getServiceId());
                System.out.println(instance.getInstanceId());
                System.out.println(instance.getHost()+""+instance.getPort());
                System.out.println("uri:"+instance.getUri());

            });
        });


    }




}
