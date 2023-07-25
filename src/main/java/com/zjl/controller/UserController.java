package com.zjl.controller;

import com.alibaba.druid.util.StringUtils;
import com.zjl.controller.viewobject.UserVO;
import com.zjl.error.BusinessException;
import com.zjl.error.EmBusinessError;
import com.zjl.response.CommonReturnType;
import com.zjl.service.UserService;
import com.zjl.service.model.UserMoldel;
import com.zjl.util.MD5Utils;
import org.apache.tomcat.util.security.MD5Encoder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import sun.misc.BASE64Encoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Controller("user")
@RequestMapping("/user")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @RequestMapping("/get")
    @ResponseBody
    public CommonReturnType getUser(@RequestParam(name = "id")Integer id) throws BusinessException {
        UserMoldel userModel = userService.getUserById(id);
        if(userModel == null) {
            throw new BusinessException(EmBusinessError.USER_NOT_EXIST);
        }
        UserVO userVO = convertFromModel(userModel);
        return CommonReturnType.create(userVO);
    }

    /**
     * 用户获取otp短信接口
     * @param telphone
     * @return
     */
    @RequestMapping(value = "/getotp", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType getOtp(@RequestParam(name = "telphone") String telphone) {
        //需要按照一定的规则生成OTP验证码
        Random random = new Random();
        int randomInt = random.nextInt(99999);
        randomInt += 10000;
        String otpCode = String.valueOf(randomInt);
        //将OTP验证码同对应用户的手机号关联,使用httpsession的方式绑定他的手机号与OTPCODE
        HttpSession session = httpServletRequest.getSession();
        session.setAttribute(telphone, otpCode);
        //将OTP验证码通过短信通道发送给用户，省略
        System.out.println("telphone = " + telphone + " & otpCode = " + otpCode);

        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/register", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType register(@RequestParam(name = "telphone")String telphone,
                                     @RequestParam(name = "otpCode")String otpCode,
                                     @RequestParam(name = "name")String name,
                                     @RequestParam(name = "gender")Integer gender,
                                     @RequestParam(name = "age")Integer age,
                                     @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        String inSessionOtpCode = (String) httpServletRequest.getSession().getAttribute(telphone);
        if(!StringUtils.equals(otpCode, inSessionOtpCode)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "短信验证码错误");
        }
        UserMoldel userMoldel = new UserMoldel();
        userMoldel.setTelphone(telphone);
        userMoldel.setName(name);
        userMoldel.setGender(new Byte(String.valueOf(gender)));
        userMoldel.setAge(age);
        userMoldel.setRegisterModel("byphone");
        userMoldel.setEncrptPassword(MD5Utils.encodeByMd5(password));

        userService.register(userMoldel);
        return CommonReturnType.create(null);
    }

    @RequestMapping(value = "/login", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    @ResponseBody
    public CommonReturnType login(@RequestParam(name = "telphone")String telphone,
                                     @RequestParam(name = "password")String password) throws BusinessException, UnsupportedEncodingException, NoSuchAlgorithmException {
        if(org.apache.commons.lang3.StringUtils.isEmpty(telphone) || StringUtils.isEmpty(password)) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }

        UserMoldel userMoldel = userService.ValidateLogin(telphone, MD5Utils.encodeByMd5(password));

        httpServletRequest.getSession().setAttribute("IS_LOGIN", true);
        httpServletRequest.getSession().setAttribute("LOGIN_USER", userMoldel);
        return CommonReturnType.create(null);
    }

    private UserVO convertFromModel(UserMoldel userMoldel) {
        if(userMoldel == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(userMoldel, userVO);
        return userVO;
    }


}
