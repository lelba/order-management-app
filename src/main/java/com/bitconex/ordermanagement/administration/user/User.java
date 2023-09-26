package com.bitconex.ordermanagement.administration.user;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity(name = "User")
@Table(name = "\"T_USER\"")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @SequenceGenerator(name = "user_seq", sequenceName = "user_seq", allocationSize = 1, initialValue = 2)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @Column(
            name = "ID",
            updatable = false
    )
    private Long id;
    @Column(
            name = "USERNAME",
            nullable = false,
            unique = true
    )
    private String userName;

    @Column(
            name = "PASSWORD",
            nullable = false
    )
    private String password;
    @Column(
            name = "EMAIL",
            nullable = false
    )
    private String email;

    @Column(
            name = "ROLE",
            nullable = false
    )
    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Column(
            name = "NAME"
    )
    private String name;

    @Column(
            name = "SURNAME"
    )
    private String surname;

    @Column(
            name = "DOB"
    )
    private Date dateOfBirth;

    @Column(
            name = "ACTIVE",
            columnDefinition = "boolean default true"
    )
    private boolean active;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private Address address;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
