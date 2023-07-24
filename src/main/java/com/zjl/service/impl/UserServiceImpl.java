package com.zjl.service.impl;

import com.zjl.dao.UserDOMapper;
import com.zjl.dao.UserPasswordDOMapper;
import com.zjl.dataobject.UserDO;
import com.zjl.dataobject.UserPasswordDO;
import com.zjl.service.UserService;
import com.zjl.service.model.UserMoldel;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;


    @Override
    public UserMoldel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if(userDO == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO, userPasswordDO);
    }

    private UserMoldel convertFromDataObject(UserDO userDO, UserPasswordDO userPasswordDO) {
        if(userDO == null) {
            return null;
        }
        UserMoldel userMoldel = new UserMoldel();
        BeanUtils.copyProperties(userDO, userMoldel);
        if(userPasswordDO != null) {
            userMoldel.setEncrptPassword(userPasswordDO.getEncrptPassword());
        }
        return userMoldel;
    }
}
