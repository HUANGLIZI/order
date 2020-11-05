package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.annotation.Audit;
import cn.edu.xmu.ooad.annotation.Depart;
import cn.edu.xmu.ooad.annotation.LoginUser;
import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.ResponseUtil;
import cn.edu.xmu.ooad.util.ReturnObject;
import cn.edu.xmu.privilege.model.vo.PrivilegeVo;
import cn.edu.xmu.privilege.model.vo.RoleVo;
import cn.edu.xmu.privilege.service.RoleService;
import cn.edu.xmu.privilege.service.UserService;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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

    /****auth 008*****/
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
        return Common.getRetObject(retObject);
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
        return Common.getNullRetObj(returnObject, httpServletResponse);
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
        return Common.getNullRetObj(retObject, httpServletResponse);
    }
}
