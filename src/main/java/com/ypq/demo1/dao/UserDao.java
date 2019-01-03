package com.ypq.demo1.dao;

import com.sun.xml.internal.bind.v2.model.core.ID;
import com.ypq.demo1.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao<T, ID> extends JpaRepository<User, Integer>, JpaSpecificationExecutor {
}
