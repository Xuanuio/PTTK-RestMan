package com.pttk.restman.model;

import java.time.LocalDate;

public class Customer extends User{
    private Integer id;


    public Customer() {}

    public Customer(Integer id) {
        super();
        this.id = id;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

}
