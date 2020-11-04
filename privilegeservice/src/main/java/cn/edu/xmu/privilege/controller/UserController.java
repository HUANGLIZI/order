package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.service.UserService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import cn.edu.xmu.ooad.util.*;
import cn.edu.xmu.privilege.model.bo.User;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * UserController
 * @author XQChen
 **/

@Api(value = "权限服务", tags = "privilege")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/adminusers", produces = "application/json;charset=UTF-8")
public class UserController {

    private  static  final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    private final JwtHelper jwtHelper = new JwtHelper();

    @ApiOperation(value = "auth003:查看自己信息",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value ="用户token", required = true)
    })
    @ApiResponses({
    })
    @GetMapping("")
    public Object getUserSelf(@RequestHeader String token) {
        Object returnObject = null;

        //不考虑token
        //登陆做完后取消该注释
        //JwtHelper.UserAndDepart userAndDepart = jwtHelper.verifyTokenAndGetClaims(token);
        //Long id = userAndDepart.getUserId();

        //测试临时指定id
        Long id = 1L;

        if(id == null) {
            returnObject = Common.getNullRetObj(new ReturnObject<>(ResponseCode.AUTH_NEED_LOGIN), httpServletResponse);
        } else
        {
            ReturnObject<VoObject> user =  userService.findUserById(id);
            logger.info("findUserSelf: user = " + user.getData() + " code = " + user.getCode());
            if(!user.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
                returnObject = Common.getRetObject(user);
            } else {
                returnObject = Common.getNullRetObj(new ReturnObject<>(user.getCode(), user.getErrmsg()), httpServletResponse);
            }
        }

        return returnObject;
    }

    @ApiOperation(value = "auth003: 查看任意用户信息",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "path",   dataType = "Integer", name = "id",            value ="用户id",    required = true)
    })
    @ApiResponses({
    })
    @GetMapping("{id}")
    public Object getUserById(@PathVariable("id") Long id, @RequestHeader String token) {
        Object returnObject = null;

        //不考虑token
        //登陆做完后取消该注释
        //JwtHelper.UserAndDepart userAndDepart = jwtHelper.verifyTokenAndGetClaims(token);
        //Long id = userAndDepart.getUserId();

        if(id == null) {
            returnObject = Common.getNullRetObj(new ReturnObject<>(ResponseCode.AUTH_NEED_LOGIN), httpServletResponse);
        } else {
            ReturnObject<VoObject> user = userService.findUserById(id);
            logger.info("findUserById: user = " + user.getData() + " code = " + user.getCode());
            if (!user.getCode().equals(ResponseCode.RESOURCE_ID_NOTEXIST)) {
                returnObject = Common.getRetObject(user);
            } else {
                returnObject = Common.getNullRetObj(new ReturnObject<>(user.getCode(), user.getErrmsg()), httpServletResponse);
            }
        }

        return returnObject;
    }

    @ApiOperation(value = "auth003: 查询用户信息",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String",  name = "authorization", value ="用户token", required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "userName",      value ="用户名",    required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "String",  name = "mobile",        value ="电话号码",  required = false),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "page",          value ="页码",      required = true),
            @ApiImplicitParam(paramType = "query",  dataType = "Integer", name = "pagesize",      value ="每页数目",  required = true)
    })
    @ApiResponses({
    })
    @GetMapping("/all")
    public Object findAllUser(
            @RequestHeader String  token,
            @RequestParam  String  userName,
            @RequestParam String  mobile,
            @RequestParam  Integer page,
            @RequestParam  Integer pagesize) {

        Object object = null;

        //不考虑token
        //登陆做完后取消该注释
        //JwtHelper.UserAndDepart userAndDepart = jwtHelper.verifyTokenAndGetClaims(token);
        //Lon = userAndDepart.getUserId();

        //测试临时指定id
        Long id = 1L;

        if(id == null) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.AUTH_NEED_LOGIN), httpServletResponse);
        } else if(page <= 0 || pagesize <= 0) {
            object = Common.getNullRetObj(new ReturnObject<>(ResponseCode.FIELD_NOTVALID), httpServletResponse);
        } else {
            ReturnObject<List> returnObject = userService.findAllUsers(userName, mobile, page, pagesize);
            logger.info("findUserById: getUsers = " + returnObject);
            object = Common.getListRetObject(returnObject);
        }

        return object;
    }


}
