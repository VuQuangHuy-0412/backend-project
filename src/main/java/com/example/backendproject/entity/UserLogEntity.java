package com.example.backendproject.entity;

import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import vn.ghtk.ewallet.commonlib.util.generator.Generator;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "user_log")
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserLogEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = Generator.SNOW_FLAKE)
    @GenericGenerator(name = Generator.SNOW_FLAKE,
            parameters = @org.hibernate.annotations.Parameter(name = "generator", value = "vn.ghtk.ewallet.commonlib.util.generator.DefaultUniqueGenerator"),
            strategy = Generator.CLIENT_ID_CLASS)
    @Column(name = "id")
    private Long id;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username")
    private String userName;

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "action")
    private String action;

    @Column(name = "data")
    private String data;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        UserLogEntity that = (UserLogEntity) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return 639559398;
    }
}
