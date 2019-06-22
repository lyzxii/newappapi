package com.caiyi.lottery.tradesystem.annotation;


import java.lang.annotation.*;

/**
 * 自定义注解——设置用户数据
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SetUserData {
    String sysCode();
}
