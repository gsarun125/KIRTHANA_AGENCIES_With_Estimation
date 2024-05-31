package com.ka.billingsystem.model;

public class DataModel {
    private int estimationId;
    private String escusName;
    private String amount;

    public DataModel(int estimationId, String escusName, String amount) {
        this.estimationId = estimationId;
        this.escusName = escusName;
        this.amount = amount;
    }

    public int getEstimationId() {
        return estimationId;
    }

    public String getEscusName() {
        return escusName;
    }

    public String getAmount() {
        return amount;
    }
}
