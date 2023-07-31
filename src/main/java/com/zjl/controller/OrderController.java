package com.zjl.controller;

import com.zjl.error.BusinessException;
import com.zjl.error.EmBusinessError;
import com.zjl.response.CommonReturnType;
import com.zjl.service.OrderService;
import com.zjl.service.model.OrderModel;
import com.zjl.service.model.UserMoldel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

import static com.zjl.controller.BaseController.CONTENT_TYPE_FORMED;

/**
 * @author zhangjiling
 * @date 2023/7/28 1:22
 */
@RestController("order")
@RequestMapping("/order")
@CrossOrigin(allowCredentials = "true",allowedHeaders = "*")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private HttpServletRequest httpServletRequest;


    @RequestMapping(value = "/create", method = {RequestMethod.POST}, consumes = {CONTENT_TYPE_FORMED})
    public CommonReturnType createOrder(@RequestParam(name = "itemId")Integer itemId,
                                        @RequestParam(name = "amount") Integer amount,
                                        @RequestParam(name = "promoId", required = false) Integer promoId) throws BusinessException {
        //获取用户的登录信息
        Boolean isLogin = (Boolean) httpServletRequest.getSession().getAttribute("IS_LOGIN");
        if(isLogin == null || !isLogin.booleanValue()) {
            throw new BusinessException(EmBusinessError.USER_NOT_LOGIN, "用户还未登录, 不能下单");
        }

        UserMoldel loginUser = (UserMoldel) httpServletRequest.getSession().getAttribute("LOGIN_USER");

        orderService.create(loginUser.getId(), itemId, promoId, amount);

        return CommonReturnType.create(null);
    }
}
