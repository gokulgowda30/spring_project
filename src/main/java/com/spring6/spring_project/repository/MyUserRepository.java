package com.spring6.spring_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring6.spring_project.dto.MyUser;

public interface MyUserRepository extends JpaRepository<MyUser,Integer>{

    //boolean existsByEmail();

    boolean existsByEmail(String email);

    MyUser findByEmail(String email);

}
