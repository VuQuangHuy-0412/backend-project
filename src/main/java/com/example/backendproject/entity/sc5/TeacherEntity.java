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

    @Column(name = "gd_time")
    private Double gdTime;

    @Column(name = "hd_time")
    private Double hdTime;

    @Column(name = "rating")
    private Float rating;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "status")
    private Integer status;

    @Column(name = "total_time")
    private Integer totalTime;

    @Column(name = "dataset")
    private Long dataset;
}
