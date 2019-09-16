package com.dblib.db;

import java.util.List;

public interface IBaseDao<T> {

    //插入
    long insert(T entity);

    //更新
    long update(T entity, T where); //new User(1, "alan0", "123")

    //删除
    int delete(T where);

    List<T> query(T where);


    List<T> query(T where, String orderBy, Integer startIndex, Integer limit);
}
