package com.jac.app.job.service.Impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jac.app.job.entity.UserEntity;
import com.jac.app.job.mapper.UserMapper;
import com.jac.app.job.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@Transactional(rollbackFor = Exception.class, readOnly = true)
public class UserServiceImpl extends ServiceImpl<UserMapper, UserEntity> implements UserService {



}
