package com.zjl.service;

import com.zjl.error.BusinessException;
import com.zjl.service.model.UserMoldel;

public interface UserService {

    public UserMoldel getUserById(Integer id);

    public void register(UserMoldel userMoldel) throws BusinessException;

    public UserMoldel ValidateLogin(String telphone, String encrptPassword) throws BusinessException;
}
