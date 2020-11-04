package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.model.bo.UserRole;
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
 * @author Ming Qiu
 **/
@Api(value = "权限服务", tags = "privilege")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/privilege", produces = "application/json;charset=UTF-8")
public class PrivilegeController {

    private  static  final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);

    @Autowired
    private UserService userService;

    /***
     * 取消用户权限
     * @param id 用户id
     * @return
     */
    @ApiOperation(value = "取消用户权限")
    @ApiImplicitParams({

            @ApiImplicitParam(name="userid", value="用户id", required = true, dataType="Integer", paramType="path")

    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @DeleteMapping("/adminusersrole/{id}")
    public Object revokeRole(@PathVariable Long id){
        return Common.getRetObject(userService.revokeRole(id));
    }

    /***
     * 赋予用户权限
     * @param userid 用户id
     * @param roleid 角色id
     * @return
     */
    @ApiOperation(value = "赋予用户权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name="userid", value="用户id", required = true, dataType="Integer", paramType="path"),
            @ApiImplicitParam(name="roleid", value="角色id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 504, message = "操作id不存在")
    })
    @PostMapping("/adminusers/{userid}/roles/{roleid}")
    public Object assignRole(@RequestParam(name = "createid") Long createid, @PathVariable Long userid, @PathVariable Long roleid){

        ReturnObject<VoObject> returnObject =  userService.assignRole(createid, userid, roleid);
        return Common.getRetObject(returnObject);
    }

    /***
     * 获得自己角色信息
     * @author Xianwei Wang
     * @return
     */
    @ApiOperation(value = "获得自己角色信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", value="用户", required = true, dataType="String", paramType="param")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),

    })
    @GetMapping("/adminusers/self/roles")
    public Object getUserSelfRole(@RequestParam(name = "id") Long id){

        ReturnObject<List> returnObject =  userService.getSelfUserRoles(id);
        return Common.getListRetObject(returnObject);
    }

    /***
     * 获得所有人角色信息
     * @param id 用户id
     * @return
     */
    @ApiOperation(value = "获得所有人角色信息")
    @ApiImplicitParams({

            @ApiImplicitParam(name="id", value="用户id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/adminusers/{id}/roles")
    public Object getSelfRole(@PathVariable Long id){
        ReturnObject<List> returnObject =  userService.getSelfUserRoles(id);
        return Common.getListRetObject(returnObject);
    }


    /**
     * 获得所有权限
     * @return Object
     * createdBy Ming Qiu 2020/11/03 23:57
     */
    @ApiOperation(value = "获得所有权限")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("privileges")
    public Object getAllPrivs(){
        ReturnObject<List> returnObject =  userService.findAllPrivs();
        return Common.getListRetObject(returnObject);
    }

    /**
     * 修改权限
     * @param id: 权限id
     * @return Object
     * createdBy Ming Qiu 2020/11/03 23:57
     */
    @ApiOperation(value = "修改权限信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="String", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("privileges/{id}")
    public Object changePriv(@PathVariable Long id, @RequestBody PrivilegeVo vo){
        logger.debug("changePriv: id = "+ id +" vo" + vo);
        ReturnObject returnObject =  userService.changePriv(id, vo);
        return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
    }

}

