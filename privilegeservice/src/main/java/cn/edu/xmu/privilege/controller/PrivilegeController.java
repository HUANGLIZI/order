package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.annotation.*;
import cn.edu.xmu.privilege.model.bo.Privilege;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.dao.PrivilegeDao;
import cn.edu.xmu.privilege.model.bo.UserRole;
import cn.edu.xmu.privilege.model.vo.LoginVo;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;

import cn.edu.xmu.ooad.util.ResponseCode;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import cn.edu.xmu.privilege.model.vo.RoleVo;
import cn.edu.xmu.privilege.model.vo.UserVo;
import cn.edu.xmu.privilege.service.RoleService;
import cn.edu.xmu.privilege.service.UserService;
import cn.edu.xmu.privilege.util.IpUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 权限控制器
 * @author Ming Qiu
 * Modified at 2020/11/5 13:21
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
        return Common.decorateReturnObject(userService.revokeRole(id));
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
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }

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

            @ApiImplicitParam(name="id", value="用户id", required = true, dataType="int", paramType="path")
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("/adminusers/{id}/roles")
    public Object getSelfRole(@PathVariable Long id){
        ReturnObject<List> returnObject =  userService.getSelfUserRoles(id);
        if (returnObject.getCode() == ResponseCode.OK) {
            return Common.getListRetObject(returnObject);
        } else {
            return Common.decorateReturnObject(returnObject);
        }

    }


//    @Autowired
    private JwtHelper jwtHelper = new JwtHelper();

    @Autowired
    private RoleService roleService;

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
    @Audit
    @GetMapping("privileges")
    public Object getAllPrivs(@RequestParam(required = false) Integer page, @RequestParam(required = false) Integer pageSize){

        logger.debug("getAllPrivs: page = "+ page +"  pageSize ="+pageSize);

        page = (page == null)?1:page;
        pageSize = (pageSize == null)?10:pageSize;

        ReturnObject<PageInfo<VoObject>> returnObject =  userService.findAllPrivs(page, pageSize);
        return Common.getPageRetObject(returnObject);
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



    /* auth008 start*/
    //region
    /**
     * 分页查询所有角色
     * @param page
     * @param pageSize
     * @return Object
     */
    @ApiOperation(value = "auth008: 查询角色", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "page", value = "页码", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "int", name = "pageSize", value = "每页数目", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @GetMapping("roles")
    public Object selectAllRoles(@RequestParam Integer page, @RequestParam Integer pageSize) {
        logger.info("select all roles");
        ReturnObject<List> returnObject = roleService.selectAllRoles(page, pageSize);
        return Common.getListRetObject(returnObject);
    }

    /**
     * 新增一个角色
     * @param vo
     * @param bindingResult
     * @return Object
     */
    @ApiOperation(value = "auth008: 新增角色", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "RoleVo", name = "vo", value = "可修改的用户信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PostMapping("roles")
    public Object insertRole(@Validated @RequestBody RoleVo vo, BindingResult bindingResult) {
        logger.info("insert role");
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            logger.info("validate fail");
            return returnObject;
        }
        //由AOP解析token获取userId
        Long userId = 1L;
        ReturnObject<VoObject> retObject = roleService.insertRole(userId, vo);
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.decorateReturnObject(retObject);
    }

    /**
     * 删除角色，同时级联删除用户角色表与角色权限表
     * @param id
     * @return Object
     */
    @ApiOperation(value = "auth008： 删除角色", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "角色id", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @DeleteMapping("roles/{id}")
    public Object deleteRole(@PathVariable("id") Long id) {
        logger.info("delete role");
        ReturnObject<Object> returnObject = roleService.deleteRole(id);
        return Common.decorateReturnObject(returnObject);
    }

    /**
     * 修改角色信息
     * @param id
     * @param vo
     * @param bindingResult
     * @return Object
     */
    @ApiOperation(value = "auth008:修改角色信息", produces = "application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "header", dataType = "String", name = "authorization", value = "Token", required = true),
            @ApiImplicitParam(paramType = "path", dataType = "int", name = "id", value = "角色id", required = true),
            @ApiImplicitParam(paramType = "body", dataType = "RoleVo", name = "vo", value = "可修改的用户信息", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
    })
    @PutMapping("roles/{id}")
    public Object updateRole(@PathVariable("id") Long id, @Validated @RequestBody RoleVo vo, BindingResult bindingResult) {
        logger.info("update role");
        //校验前端数据
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        //由AOP解析token获取userId
        Long userId = 1L;
        ReturnObject<Object> retObject = roleService.updateRole(userId, id, vo);
        return Common.decorateReturnObject(retObject);
    }
    //endregion
    /* auth008 end*/

    /* auth009 */

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
    public Object modifyUserInfo(@PathVariable Long id, @RequestBody UserVo vo) {
        if (logger.isDebugEnabled()) {
            logger.debug("modifyUserInfo: id = "+ id +" vo = " + vo);
        }
        ReturnObject returnObject = userService.modifyUserInfo(id, vo);
        return Common.decorateReturnObject(returnObject);
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
        if (logger.isDebugEnabled()) {
            logger.debug("deleteUser: id = "+ id);
        }
        ReturnObject returnObject = userService.deleteUser(id);
        return Common.decorateReturnObject(returnObject);
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
        if (logger.isDebugEnabled()) {
            logger.debug("forbidUser: id = "+ id);
        }
        ReturnObject returnObject = userService.forbidUser(id);
        return Common.decorateReturnObject(returnObject);
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
        if (logger.isDebugEnabled()) {
            logger.debug("releaseUser: id = "+ id);
        }
        ReturnObject returnObject = userService.releaseUser(id);
        return Common.decorateReturnObject(returnObject);
    }
    /* auth009 结束 */

    /**
     * 用户登录
     * @param loginVo
     * @param bindingResult
     * @param httpServletResponse
     * @param httpServletRequest
     * @return
     * @author 24320182203266
     */
    @ApiOperation(value = "登录")
    @PostMapping("privileges/login")
    public Object login(@Validated @RequestBody LoginVo loginVo, BindingResult bindingResult
            , HttpServletResponse httpServletResponse,HttpServletRequest httpServletRequest){
        /* 处理参数校验错误 */
        Object o = Common.processFieldErrors(bindingResult, httpServletResponse);
        if(o != null){
            return o;
        }

        String ip = IpUtil.getIpAddr(httpServletRequest);
        ReturnObject<String> jwt = userService.login(loginVo.getUserName(), loginVo.getPassword(), ip);

        if(jwt.getData() == null){
            return ResponseUtil.fail(jwt.getCode(), jwt.getErrmsg());
        }else{
            return ResponseUtil.ok(jwt.getData());
        }
    }

    /**
     * 用户注销
     * @param userId
     * @return
     * @author 24320182203266
     */
    @ApiOperation(value = "注销")
    @Audit
    @GetMapping("privileges/logout")
    public Object logout(@LoginUser Long userId){

        logger.debug("logout: userId = "+userId);
        ReturnObject<Boolean> success = userService.Logout(userId);
        if (success.getData() == null)  {
            return ResponseUtil.fail(success.getCode(), success.getErrmsg());
        }else {
            return ResponseUtil.ok();
        }
    }

    /**
     * @author 24320182203218
     **/
    @ApiOperation(value = "用户上传图片",  produces="application/json")
    @ApiImplicitParams({
            @ApiImplicitParam(paramType = "path", dataType = "Integer", name = "id", value ="用户id" ,required = true),
            @ApiImplicitParam(paramType = "formData", dataType = "file", name = "img", value ="文件", required = true)
    })
    @ApiResponses({
            @ApiResponse(code = 0, message = "成功"),
            @ApiResponse(code = 506, message = "该目录文件夹没有写入的权限"),
    })
    @PostMapping("/adminusers/{id}/uploadImg")
    public Object uploadImg(@PathVariable("id") Integer id, @RequestParam("img") MultipartFile multipartFile){
        logger.debug("uploadImg: id = "+ id +" img" + multipartFile.getOriginalFilename());
        ReturnObject returnObject = userService.uploadImg(id,multipartFile);
        return Common.getNullRetObj(returnObject, httpServletResponse);
    }
}

