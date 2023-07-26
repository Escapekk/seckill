package com.zjl.service;

import com.zjl.error.BusinessException;
import com.zjl.service.model.UserMoldel;

public interface UserService {

    UserMoldel getUserById(Integer id);

    void register(UserMoldel userMoldel) throws BusinessException;

    UserMoldel ValidateLogin(String telphone, String encrptPassword) throws BusinessException;
}
