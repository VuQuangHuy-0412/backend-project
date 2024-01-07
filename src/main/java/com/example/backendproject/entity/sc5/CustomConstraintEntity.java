package com.example.backendproject.entity.sc5;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "custom_constraint")
@Getter
@Setter
public class CustomConstraintEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "teacher_compare")
    private String teacherCompare;

    @Column(name = "teacher_column_compare")
    private String teacherColumnCompare;

    @Column(name = "teacher_value_compare")
    private String teacherValueCompare;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "class_compare")
    private String classCompare;

    @Column(name = "class_column_compare")
    private String classColumnCompare;

    @Column(name = "class_value_compare")
    private String classValueCompare;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "dataset")
    private Long dataset;
}
