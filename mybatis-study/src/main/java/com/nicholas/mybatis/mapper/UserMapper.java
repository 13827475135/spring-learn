package com.nicholas.mybatis.mapper;

import com.nicholas.mybatis.entity.User;

public interface UserMapper {

    User getByName(String name);
}
