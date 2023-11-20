package com.example.backendproject.entity.sc5;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "language")
@Getter
@Setter
public class StudentProjectEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "student_code")
    private String studentCode;

    @Column(name = "class_id")
    private Long classId;

    @Column(name = "is_assigned")
    private Integer isAssigned;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "teacher_1_id")
    private String teacher1Id;

    @Column(name = "teacher_2_id")
    private String teacher2Id;

    @Column(name = "teacher_3_id")
    private String teacher3Id;

    @Column(name = "teacher_assigned_id")
    private String teacherAssignedId;
}
