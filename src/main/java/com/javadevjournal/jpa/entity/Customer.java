package com.javadevjournal.jpa.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@ToString
public class Customer {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id")
    private Long id;

    @Column(name = "userName")
    private String userName;

    @Column(name = "password")
    private String password;

    /*@ToString.Exclude
    @Column(name = "token")
    private String token;*/

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    private Role role;
    /*@Column(name = "role_id")
    private Long role;*/

    @Column(name = "karma_negative")
    private int karmaNegative = 0;

    @Column(name = "banned")
    private boolean banned = false;

    @Column(name = "ban_time")
    private LocalDateTime banTime;

    /*@ManyToMany (cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE
    })
    @JoinTable(name = "customer_ad",
            joinColumns = @JoinColumn(name = "customer_id"),
            inverseJoinColumns = @JoinColumn(name = "ad_id")
    )
    private List<Ad> ads=new ArrayList<>();*/
    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavAdRow> favAds;



    /*public void addAd(Ad ad){
        this.favAds.add(ad);
    }
    public void removeAd(Ad ad){
        this.ads.remove(ad);
    }

    /*public List<Ad> getAds(){
        return this.ads;
    }*/

    public void incNegative() {
        karmaNegative++;
        if (karmaNegative >= 5 && !banned) {
            banned = true;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Customer customer)) {
            return false;
        }
        return Objects.equals(getUserName(), customer.getUserName())
                && Objects.equals(getPassword(), customer.getPassword());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUserName(), getPassword());
    }
}
