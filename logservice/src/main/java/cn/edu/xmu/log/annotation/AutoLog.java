package cn.edu.xmu.log.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自动日志注解
 *
 * @author Wang Weice
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLog {
    /** 模块 */
    String title() default "";

    /** 功能 */
    String action() default "";
}
