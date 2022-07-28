package com.guanghe.takeout.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.guanghe.takeout.entity.User;
import com.guanghe.takeout.mapper.UserMapper;
import com.guanghe.takeout.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
