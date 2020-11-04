package cn.edu.xmu.privilege.controller;

import cn.edu.xmu.ooad.model.VoObject;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.ooad.util.ReturnObject;
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
@RequestMapping(value = "/", produces = "application/json;charset=UTF-8")
public class RoleController {
    private  static  final Logger logger = LoggerFactory.getLogger(PrivilegeController.class);

    @Autowired
    private UserService userService;

    @Autowired
    RoleService roleService;

    @Autowired
    private HttpServletResponse httpServletResponse;

    /**
     * @param token
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
    public Object selectAllRoles(@RequestHeader(value = "authorization") String token, @RequestParam Integer page, @RequestParam Integer pageSize) {
        JwtHelper jwtHelper = new JwtHelper();
        Long userId = jwtHelper.verifyTokenAndGetClaims(token).getUserId();
        ReturnObject<List> returnObject = roleService.selectAllRoles(page, pageSize);
        return Common.getListRetObject(returnObject);
    }

    /**
     * @param token
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
    public Object insertRole(@RequestHeader(value = "authorization") String token, @Validated @RequestBody RoleVo vo, BindingResult bindingResult) {
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        JwtHelper jwtHelper = new JwtHelper();
        Long userId = jwtHelper.verifyTokenAndGetClaims(token).getUserId();
        ReturnObject<VoObject> retObject = roleService.insertRole(userId, vo);
        httpServletResponse.setStatus(HttpStatus.CREATED.value());
        return Common.getRetObject(retObject);
    }

    /**
     * @param token
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
    public Object deleteRole(@RequestHeader(value = "authorization") String token, @PathVariable("id") Long id) {
        ReturnObject<Object> returnObject = roleService.deleteRole(id);
        return Common.getNullRetObj(returnObject, httpServletResponse);
    }

    /**
     * @param token
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
    public Object updateRole(@RequestHeader(value = "authorization") String token, @PathVariable("id") Long id, @Validated @RequestBody RoleVo vo, BindingResult bindingResult) {
        Object returnObject = Common.processFieldErrors(bindingResult, httpServletResponse);
        if (null != returnObject) {
            return returnObject;
        }
        JwtHelper jwtHelper = new JwtHelper();
        Long userId = jwtHelper.verifyTokenAndGetClaims(token).getUserId();
        ReturnObject<Object> retObject = roleService.updateRole(userId, id, vo);
        return Common.getNullRetObj(retObject, httpServletResponse);
    }
}
