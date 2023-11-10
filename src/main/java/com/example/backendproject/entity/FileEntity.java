package com.example.backendproject.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import vn.ghtk.ewallet.commonlib.util.generator.Generator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "file")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FileEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = Generator.SNOW_FLAKE)
    @GenericGenerator(name = Generator.SNOW_FLAKE,
            parameters = @org.hibernate.annotations.Parameter(name = "generator", value = "vn.ghtk.ewallet.commonlib.util.generator.DefaultUniqueGenerator"),
            strategy = Generator.CLIENT_ID_CLASS)
    @Column(name = "id")
    private Long id;

    @Column(name = "duplicate_key")
    private String duplicateKey;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "description")
    private String description;

    @Column(name = "status")
    private String status;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;
}
