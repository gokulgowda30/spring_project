package com.spring6.spring_project.dto;

import java.time.LocalDate;

import org.springframework.cglib.core.Local;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import lombok.Data;

@Data
@Entity
public class Student {

    @Id
    @GeneratedValue(generator = "x")
    @SequenceGenerator(name = "x",initialValue = 1001,allocationSize = 1)//Name of generator = "x" should be same as name = "x"
    private int id;
    private String name;
    private int standard;
    private LocalDate dob;
    private long mobile;
    private int subject1;
    private int subject2;
    private int subject3;
    private int subject4;
    private int subject5;
    private int subject6;
    private String picture;

    public double getPercentage(){
        return (subject1+subject2+subject3+subject4+subject5+subject6)/6.0;
    }
}
