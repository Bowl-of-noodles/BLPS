package com.javadevjournal.security;

import com.javadevjournal.dto.TokenDTO;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "users")
public class UserToken {

    private List<TokenDTO> users;

    @XmlElement(name = "user")
    public List<TokenDTO> getPersons() {
        return users;
    }

    public void setPersons(List<TokenDTO> users) {
        this.users = users;
    }
}
