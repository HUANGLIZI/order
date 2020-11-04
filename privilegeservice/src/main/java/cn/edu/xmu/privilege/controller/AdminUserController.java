package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.model.vo.UserEditVo;
import cn.edu.xmu.privilege.service.UserService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * AdminUser 控制器
 * @author Han Li
 **/
@Api(value = "权限服务", tags = "privilege")
@RestController /*Restful的Controller对象*/
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class AdminUserController {

    private static final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);

    @Autowired
    private UserService userService;

    /**
     * auth009: 修改任意用户信息
     * @param id: 用户 id
     * @return Object
     */
    @ApiOperation(value = "修改任意用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 732, message = "邮箱已被注册"),
            @ApiResponse(code = 733, message = "电话已被注册"),
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("adminusers/{id}")
    public Object modifyUserInfo(@PathVariable Long id, @RequestBody UserEditVo vo) {
        logger.debug("modifyUserInfo: id = "+ id +" vo" + vo);
        ReturnObject returnObject = userService.modifyUserInfo(id, vo);
        if (returnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
            return new ResponseEntity(
                    ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
    }

    /**
     * auth009: 删除任意用户
     * @param id: 用户 id
     * @return Object
     */
    @ApiOperation(value = "删除任意用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @DeleteMapping("adminusers/{id}")
    public Object deleteUser(@PathVariable Long id) {
        logger.debug("deleteUser: id = "+ id);
        ReturnObject returnObject = userService.deleteUser(id);
        if (returnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
            return new ResponseEntity(
                    ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
    }

    /**
     * auth009: 禁止用户登录
     * @param id: 用户 id
     * @return Object
     */
    @ApiOperation(value = "禁止用户登录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("adminusers/{id}/forbid")
    public Object forbidUser(@PathVariable Long id) {
        logger.debug("forbidUser: id = "+ id);
        ReturnObject returnObject = userService.forbidUser(id);
        if (returnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
            return new ResponseEntity(
                    ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
    }

    /**
     * auth009: 恢复用户
     * @param id: 用户 id
     * @return Object
     */
    @ApiOperation(value = "恢复用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="authorization", value="Token", required = true, dataType="String", paramType="header"),
            @ApiImplicitParam(name="id", required = true, dataType="Integer", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("adminusers/{id}/release")
    public Object releaseUser(@PathVariable Long id) {
        logger.debug("releaseUser: id = "+ id);
        ReturnObject returnObject = userService.releaseUser(id);
        if (returnObject.getCode() == ResponseCode.RESOURCE_ID_NOTEXIST) {
            return new ResponseEntity(
                    ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg()),
                    HttpStatus.NOT_FOUND);
        }
        return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
    }
}
