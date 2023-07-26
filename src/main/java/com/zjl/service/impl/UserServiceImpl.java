package com.zjl.service.impl;

import com.zjl.dao.UserDOMapper;
import com.zjl.dao.UserPasswordDOMapper;
import com.zjl.dataobject.UserDO;
import com.zjl.dataobject.UserPasswordDO;
import com.zjl.error.BusinessException;
import com.zjl.error.EmBusinessError;
import com.zjl.service.UserService;
import com.zjl.service.model.UserMoldel;
import com.zjl.validator.ValidationResult;
import com.zjl.validator.ValidatorImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDOMapper userDOMapper;

    @Autowired
    private UserPasswordDOMapper userPasswordDOMapper;

    @Autowired
    private ValidatorImpl validator;


    @Override
    public UserMoldel getUserById(Integer id) {
        UserDO userDO = userDOMapper.selectByPrimaryKey(id);
        if(userDO == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        return convertFromDataObject(userDO, userPasswordDO);
    }

    @Override
    @Transactional
    public void register(UserMoldel userMoldel) throws BusinessException {
        if(userMoldel == null) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
        }
//        if(StringUtils.isEmpty(userMoldel.getName())
//        ||userMoldel.getGender() == null
//        ||userMoldel.getAge() == null
//        ||StringUtils.isEmpty(userMoldel.getTelphone())) {
//            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR);
//        }
        ValidationResult validationResult = validator.validate(userMoldel);
        if(validationResult.getHasErrors()) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, validationResult.getErrMsg());
        }

        UserDO userDO = convertFromModel(userMoldel);
        try {
            userDOMapper.insertSelective(userDO);
        }catch (DuplicateKeyException e) {
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR, "手机号已注册");
        }

        UserPasswordDO userPasswordDO = convertPasswordFromModel(userMoldel);
        userPasswordDO.setUserId(userDO.getId());
        userPasswordDOMapper.insertSelective(userPasswordDO);

    }

    @Override
    public UserMoldel ValidateLogin(String telphone, String encrptPassword) throws BusinessException {
        UserDO userDO = userDOMapper.selectByTelphone(telphone);
        if(userDO == null) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }
        UserPasswordDO userPasswordDO = userPasswordDOMapper.selectByUserId(userDO.getId());
        UserMoldel userMoldel = convertFromDataObject(userDO, userPasswordDO);

        if(!StringUtils.equals(userMoldel.getEncrptPassword(), encrptPassword)) {
            throw new BusinessException(EmBusinessError.USER_LOGIN_FAIL);
        }

        return userMoldel;
    }

    private UserPasswordDO convertPasswordFromModel(UserMoldel userMoldel) {
        if(userMoldel == null) {
            return null;
        }
        UserPasswordDO userPasswordDO = new UserPasswordDO();
        userPasswordDO.setEncrptPassword(userMoldel.getEncrptPassword());
        return userPasswordDO;
    }

    private UserDO convertFromModel(UserMoldel userMoldel) {
        if(userMoldel == null) {
            return null;
        }
        UserDO userDO = new UserDO();
        BeanUtils.copyProperties(userMoldel, userDO);
        return userDO;
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
