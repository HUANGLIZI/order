package cn.edu.xmu.freight.controller;

import cn.edu.xmu.freight.service.FreightService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@Api(value = "运费服务", tags = "freight")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/privilege", produces = "application/json;charset=UTF-8")
public class FreightController {

    private  static  final Logger logger = LoggerFactory.getLogger(FreightController.class);

    @Autowired
    FreightService freightService;
    

}

