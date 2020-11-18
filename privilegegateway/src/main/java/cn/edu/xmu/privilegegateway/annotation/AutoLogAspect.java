package cn.edu.xmu.privilegegateway.annotation;

import cn.edu.xmu.ooad.util.JwtHelper;
import cn.edu.xmu.privilegegateway.model.Log;
import cn.edu.xmu.ooad.util.Common;
import cn.edu.xmu.ooad.util.IpUtil;
import cn.edu.xmu.ooad.util.JacksonUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 自动日志注解
 *
 * @author Wang Weice
 */
@Aspect
@Component
@Order(0) // 切面的顺序，越小越优先，对于多个切面Spring是使用责任链的模式 为了一开始将日志相关的参数初始化好，这里设置为最优先执行
@Slf4j
public class AutoLogAspect {

//    @Autowired
//    private SysLogMapper sysLogMapper;

    /**
     * 此处的切点是注解的方式
     * 只要出现 @LogAnnotation注解都会进入
     */
    @Pointcut("@annotation(cn.edu.xmu.oomall.annotation.AutoLog)")
    public void logPointCut(){
    }

    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        //执行方法
        Object result = point.proceed();
        //执行时长(毫秒)
        long time = System.currentTimeMillis() - beginTime;

        //保存日志
        try {
            saveSysLog(point, time);
        } catch (Exception e) {
            log.error("e={}",e);
        }

        return result;
    }

    private void saveSysLog(ProceedingJoinPoint joinPoint, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        Log sysLog = new Log();
        AutoLog autoLog = method.getAnnotation(AutoLog.class);
        if(autoLog != null){
            //注解上的描述
            sysLog.setDescr(autoLog.title()+"-"+autoLog.action());
        }

        //请求的方法名
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = signature.getName();
        sysLog.setMethod(className + "." + methodName + "()");
        log.info("请求{}.{}耗时{}毫秒",className,methodName,time);
        try {
            //请求的参数
            Object[] args = joinPoint.getArgs();
            String params=null;
            if(args.length!=0){
                params= JacksonUtil.toJson(args);
            }

            sysLog.setParams(params);
        } catch (Exception e) {

        }
        //获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //设置IP地址
        sysLog.setIp(IpUtil.getIpAddr(request));
        log.info("Ip{}，接口地址{}，请求方式{}，入参：{}",sysLog.getIp(),request.getRequestURL(),request.getMethod(),sysLog.getParams());
        //用户名
        String token = request.getHeader("Token");
        JwtHelper.UserAndDepart userAndDepart = new JwtHelper().verifyTokenAndGetClaims(token);
        Long userId = userAndDepart.getUserId();
        sysLog.setUserId(userId);
        sysLog.setGmtCreate(LocalDateTime.now());

        log.info(sysLog.toString());
//        sysLogMapper.insertSelective(sysLog);

    }
}
