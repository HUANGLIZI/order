package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.model.bo.Role;
import cn.edu.xmu.privilege.model.vo.RoleVo;
import cn.edu.xmu.privilege.service.RoleService;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import cn.edu.xmu.privilege.model.vo.UserVo;
import cn.edu.xmu.privilege.service.UserService;
import com.mysql.cj.conf.PropertyKey;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    RoleService roleService;

    @Autowired
    private HttpServletResponse httpServletResponse;

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
    @Audit
    @PutMapping("privileges/{id}")
    public Object changePriv(@PathVariable Long id, @RequestBody PrivilegeVo vo, @LoginUser Long userId, @Depart Long departId){
        logger.debug("changePriv: id = "+ id +" vo" + vo);
        logger.debug("getAllPrivs: userId = " + userId +" departId = "+departId);
        ReturnObject returnObject =  userService.changePriv(id, vo);
        return ResponseUtil.fail(returnObject.getCode(), returnObject.getErrmsg());
    }
<<<<<<< HEAD
=======

    /**
     * 修改任意用户信息
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
    public Object modifyUserInfo(@PathVariable Long id, @RequestBody UserVo vo) {
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
     * 删除任意用户
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
     * 禁止用户登录
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
     * 恢复用户
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
>>>>>>> 5c49245112ed77dbf3cb31e33f39177359c87996
}
