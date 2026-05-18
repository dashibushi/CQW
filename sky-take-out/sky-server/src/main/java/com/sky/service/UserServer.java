package com.sky.service;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;

public interface UserServer {
    /**
     * 微信登录
     * @param userLoginDTO
     * @return
     */
    User WXlogin(UserLoginDTO userLoginDTO);
}
