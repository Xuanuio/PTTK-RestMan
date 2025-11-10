package com.pttk.restman.model;

public class Table {
    private Integer id;
    private String numberTable;

    public Table() {}

    public Table(Integer id, String numberTable) {
        this.id = id;
        this.numberTable = numberTable;
    }

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNumberTable() { return numberTable; }
    public void setNumberTable(String numberTable) { this.numberTable = numberTable; }
}
