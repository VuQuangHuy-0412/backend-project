package com.example.backendproject.entity.sc5;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "constraint")
@Getter
@Setter
public class ConstraintEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "compare")
    private String compare;

    @Column(name = "column_compare")
    private String columnCompare;

    @Column(name = "value_compare")
    private String valueCompare;

    @Column(name = "status")
    private Integer status;
}
