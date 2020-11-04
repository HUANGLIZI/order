package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import cn.edu.xmu.privilege.service.UserService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限控制器
 * @author yue hao
 **/
@Api(value = "用户服务", tags = "user")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/user", produces = "application/json;charset=UTF-8")
public class UserController {

    private  static  final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    /**
     * 查询某一用户权限
     * @return Object
     * createdBy yuehao 2020/11/04
     */
    /**
     * 查询某一用户权限
     * @return Object
     * createdBy yuehao 2020/11/04
     */
    @ApiOperation(value = "获得某一用户的权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/adminusers/{id}/privileges")
    public Object getPrivsByUserId(@PathVariable Long id){
        ReturnObject<List> returnObject =  userService.findPrivsByUserId(id);
        return Common.getListRetObject(returnObject);
    }

}
