package cn.edu.xmu.freight.controller;

import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;


@Api(value = "运费服务", tags = "freight")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/privilege", produces = "application/json;charset=UTF-8")
public class FreightController {

    private  static  final Logger logger = LoggerFactory.getLogger(FreightController.class);

    /***
     * 新建运费模板
     */
    @ApiOperation(value = "新建运费模板")

}

