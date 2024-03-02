package com.javadevjournal.jpa.entity;

import javax.persistence.*;

import com.javadevjournal.jpa.enums.RoleName;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

@Entity
@NoArgsConstructor
@Table(name = "roles")
@Getter
@Setter
@ToString
public class Role implements GrantedAuthority {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "name")
    private RoleName name;

    @Override
    public String getAuthority() {
        return name.name();
    }
}
