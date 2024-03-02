package com.javadevjournal.dto;

import com.javadevjournal.jpa.entity.Role;
import com.javadevjournal.jpa.enums.RoleName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class FullCustomerDTO {

    private Long id;
    private String userName;
    private String password;
    private RoleName role;
    private int karmaNegative = 0;
    private boolean banned = false;

}
