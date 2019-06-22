package com.caiyi.lottery.tradesystem.annotation;

import java.lang.annotation.*;

/**
 * 自定义注解——验证用户是否登录
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface RealIP {
}
