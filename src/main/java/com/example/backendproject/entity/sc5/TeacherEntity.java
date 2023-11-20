package com.example.backendproject.entity.sc5;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "teacher")
@Getter
@Setter
public class TeacherEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "full_name")
    private String fullName;

    @Column(name = "rank_and_degree")
    private String rankAndDegree;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "birthday")
    private Date birthday;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
