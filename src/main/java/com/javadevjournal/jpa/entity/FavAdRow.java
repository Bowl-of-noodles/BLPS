package com.javadevjournal.jpa.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
public class FavAdRow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @OneToOne
    private Ad ad;
    @ManyToOne(fetch = FetchType.LAZY)
    private Customer customer;


}

