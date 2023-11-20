package com.example.backendproject.entity.sc5;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table(name = "language_teacher_mapping")
@Getter
@Setter
public class LanguageTeacherMappingEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "language_id")
    private Long languageId;
}
