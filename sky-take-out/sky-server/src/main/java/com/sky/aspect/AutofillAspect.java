package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;  // ✅ 正确！这是 Java 反射的 Method

import java.lang.reflect.InvocationTargetException;
import java.time.LocalDateTime;

/**
 * 自定义切面，实现公共字段自动填充
 */
@Aspect
@Component
@Slf4j
public class AutofillAspect {

    /**
     * 切入点
     */
    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){
    }

    /**
     * 前置通知
     */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint){
        log.info("开始进行公共字段填充");
        //获取当前方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        //获取当前方法参数
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = autoFill.value();
        //获取当前方法参数对象
        Object[] args = joinPoint.getArgs();
        //判断参数对象
        if(args == null || args.length == 0) {
            return;
        }
        Object entity = args[0];
        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();
        if(value == OperationType.INSERT) {
            //为插入操作的字段赋值
            //setCreateTime
            //setUpdateTime
            //setCreateUser
            //setUpdateUser
            try {
                //获取方法
                Method setCreateTime = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
                Method setUpdateTime = entity.getClass().getMethod( AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setCreateUser = entity.getClass().getMethod( AutoFillConstant.SET_CREATE_USER, Long.class);
                Method setUpdateUser = entity.getClass().getMethod( AutoFillConstant.SET_UPDATE_USER, Long.class);
                //调用方法
                setCreateTime.invoke(entity, now);
                setUpdateTime.invoke(entity, now);
                setCreateUser.invoke(entity, currentId);
                setUpdateUser.invoke(entity, currentId);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (value == OperationType.UPDATE){
            //为更新操作的字段赋值
            //setUpdateTime
            //setUpdateUser

            try {
                //获取方法
                Method setUpdateTime = entity.getClass().getMethod( AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
                Method setUpdateUser = entity.getClass().getMethod( AutoFillConstant.SET_UPDATE_USER, Long.class);

                //调用方法
                        setUpdateTime.invoke(entity, now);
                        setUpdateUser.invoke(entity, currentId);
            } catch (Exception e) {
                throw new RuntimeException(e);
        }
        }


    }
}
