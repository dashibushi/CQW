package com.sky.controller.user;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.json.JacksonObjectMapper;
import com.sky.properties.JwtProperties;
import com.sky.result.Result;
import com.sky.service.UserServer;
import com.sky.utils.JwtUtil;
import com.sky.vo.UserLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user/user")
@Api(tags = "C端用户接口")
@Slf4j
public class UserController {
    @Autowired
    private UserServer userServer;

    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 微信用户端登录接口
     * @param userLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("微信登录")
    public Result Userlogin(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("微信用户登录：code:{}", userLoginDTO.getCode());
        //1.调用微信接口服务，获取微信用户信息
        User user = userServer.WXlogin(userLoginDTO);

        Map<String, Object> claims = new HashMap();
        claims.put(JwtClaimsConstant.USER_ID, user.getId());
        //生成登录令牌
        String token = JwtUtil.createJWT(jwtProperties.getAdminSecretKey(), jwtProperties.getUserTtl(), claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(user.getId())
                .openid(user.getOpenid())
                .token(token)
                .build();
        return Result.success(userLoginVO);
    }


}
