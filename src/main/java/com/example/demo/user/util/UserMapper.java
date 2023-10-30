package com.example.demo.user.util;

import com.example.demo.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.example.demo.user.dto.UserDTO;
import com.example.demo.util.BaseMapper;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<User,UserDTO>{
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);
}
