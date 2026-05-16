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

import java.lang.reflect.Method;

import java.time.LocalDateTime;
import java.util.Collection;

@Aspect
@Component
@Slf4j
public class AutofillAspect {

    @Pointcut("execution(* com.sky.mapper.*.*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut() {
    }

    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) {
        log.info("开始进行公共字段填充");
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        AutoFill autoFill = signature.getMethod().getAnnotation(AutoFill.class);
        OperationType value = autoFill.value();
        Object[] args = joinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }

        Object entity = args[0];

        LocalDateTime now = LocalDateTime.now();
        Long currentId = BaseContext.getCurrentId();

        if (value == OperationType.INSERT) {
            if (entity instanceof Collection) {
                Collection<?> collection = (Collection<?>) entity;
                for (Object item : collection) {
                    if (item != null) {
                        setInsertFields(item, now, currentId);
                    }
                }
            } else {
                setInsertFields(entity, now, currentId);
            }
        } else if (value == OperationType.UPDATE) {
            if (entity instanceof Collection) {
                Collection<?> collection = (Collection<?>) entity;
                for (Object item : collection) {
                    if (item != null) {
                        setUpdateFields(item, now, currentId);
                    }
                }
            } else {
                setUpdateFields(entity, now, currentId);
            }
        }
    }

    private void setInsertFields(Object entity, LocalDateTime now, Long currentId) {
        try {
            Method setCreateTime = null;
            Method setUpdateTime = null;
            Method setCreateUser = null;
            Method setUpdateUser = null;

            try {
                setCreateTime = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_TIME, LocalDateTime.class);
            } catch (NoSuchMethodException e) {
                log.debug("实体 {} 没有 setCreateTime 方法", entity.getClass().getSimpleName());
            }

            try {
                setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            } catch (NoSuchMethodException e) {
                log.debug("实体 {} 没有 setUpdateTime 方法", entity.getClass().getSimpleName());
            }

            try {
                setCreateUser = entity.getClass().getMethod(AutoFillConstant.SET_CREATE_USER, Long.class);
            } catch (NoSuchMethodException e) {
                log.debug("实体 {} 没有 setCreateUser 方法", entity.getClass().getSimpleName());
            }

            try {
                setUpdateUser = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            } catch (NoSuchMethodException e) {
                log.debug("实体 {} 没有 setUpdateUser 方法", entity.getClass().getSimpleName());
            }

            if (setCreateTime != null) {
                setCreateTime.invoke(entity, now);
            }
            if (setUpdateTime != null) {
                setUpdateTime.invoke(entity, now);
            }
            if (setCreateUser != null) {
                setCreateUser.invoke(entity, currentId);
            }
            if (setUpdateUser != null) {
                setUpdateUser.invoke(entity, currentId);
            }
        } catch (Exception e) {
            log.error("自动填充字段失败", e);
        }
    }

    private void setUpdateFields(Object entity, LocalDateTime now, Long currentId) {
        try {
            Method setUpdateTime = null;
            Method setUpdateUser = null;

            try {
                setUpdateTime = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_TIME, LocalDateTime.class);
            } catch (NoSuchMethodException e) {
                log.debug("实体 {} 没有 setUpdateTime 方法", entity.getClass().getSimpleName());
            }

            try {
                setUpdateUser = entity.getClass().getMethod(AutoFillConstant.SET_UPDATE_USER, Long.class);
            } catch (NoSuchMethodException e) {
                log.debug("实体 {} 没有 setUpdateUser 方法", entity.getClass().getSimpleName());
            }

            if (setUpdateTime != null) {
                setUpdateTime.invoke(entity, now);
            }
            if (setUpdateUser != null) {
                setUpdateUser.invoke(entity, currentId);
            }
        } catch (Exception e) {
            log.error("自动填充字段失败", e);
        }
    }
}