package com.example.backendproject.entity.sc5;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "class")
@Getter
@Setter
public class ClassEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "semester")
    private String semester;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "week")
    private String week;

    @Column(name = "day_of_week")
    private String dayOfWeek;

    @Column(name = "time_of_day")
    private String timeOfDay;

    @Column(name = "time_of_class")
    private Double timeOfClass;

    @Column(name = "language_id")
    private Long languageId;

    @Column(name = "is_assigned")
    private Integer isAssigned;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "building")
    private String building;

    @Column(name = "room")
    private String room;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "start_time")
    private Integer startTime;

    @Column(name = "end_time")
    private Integer endTime;

    @Column(name = "number_of_student")
    private Integer numberOfStudent;

    @Column(name = "number_of_credits")
    private Integer numberOfCredits;

    @Column(name = "class_type")
    private String classType;

    @Column(name = "program")
    private String program;
}
