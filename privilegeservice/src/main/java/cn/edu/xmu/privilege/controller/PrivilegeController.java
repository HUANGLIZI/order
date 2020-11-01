package cn.edu.xmu.privilege.controller;

import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 权限控制器
 * @author Ming Qiu
 **/
@Api(value = "权限服务", tags = "privilege")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class PrivilegeController {
}
