package com.spring6.spring_project.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring6.spring_project.dto.Student;



public interface StudentJpa extends JpaRepository<Student,Integer> {

}
