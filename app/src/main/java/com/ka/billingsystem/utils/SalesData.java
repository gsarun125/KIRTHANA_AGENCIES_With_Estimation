package com.ka.billingsystem.utils;

import java.util.List;
import java.util.Objects;

public class SalesData {
    private int Bill_NO;
    private String customerName;
    private String phoneNo;
    private List<String>  mQty;
    private List<String> mProduct_name;
    private List<String> mCost;
    private int selectedOption;

    private int add_count;
    public  List<Long> mTotal;
    public  Long Net_AMT ;
    public  int count;

    public List<Long> getmTotal() {
        return mTotal;
    }

    public void setmTotal(List<Long> mTotal) {
        this.mTotal = mTotal;
    }

    public Long getNet_AMT() {
        return Net_AMT;
    }

    public void setNet_AMT(Long net_AMT) {
        Net_AMT = net_AMT;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getAdd_count() {
        return add_count;
    }

    public void setAdd_count(int add_count) {
        this.add_count = add_count;
    }

    public int getBill_NO() {
        return Bill_NO;
    }

    public void setBill_NO(int bill_NO) {
        Bill_NO = bill_NO;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public List<String> getmQty() {
        return mQty;
    }

    public void setmQty(List<String> mQty) {
        this.mQty = mQty;
    }

    public List<String> getmProduct_name() {
        return mProduct_name;
    }

    public void setmProduct_name(List<String> mProduct_name) {
        this.mProduct_name = mProduct_name;
    }

    public List<String> getmCost() {
        return mCost;
    }

    public void setmCost(List<String> mCost) {
        this.mCost = mCost;
    }

    public int getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(int selectedOption) {
        this.selectedOption = selectedOption;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            System.out.println("Objects are the same instance.");
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            System.out.println("Objects are different classes or null.");
            return false;
        }

        SalesData salesData = (SalesData) o;

        boolean isEqual = Bill_NO == salesData.Bill_NO &&
                selectedOption == salesData.selectedOption &&
                Objects.equals(customerName, salesData.customerName) &&
                Objects.equals(phoneNo, salesData.phoneNo) &&
                Objects.equals(mQty, salesData.mQty) &&
                Objects.equals(mProduct_name, salesData.mProduct_name) &&
                Objects.equals(mCost, salesData.mCost);

        if (isEqual) {
            System.out.println("Objects are equal.");
        } else {
            System.out.println("Objects are not equal.");
            System.out.println("Bill_NO: " + Bill_NO + " != " + salesData.Bill_NO);
            System.out.println("selectedOption: " + selectedOption + " != " + salesData.selectedOption);
            System.out.println("customerName: " + customerName + " != " + salesData.customerName);
            System.out.println("phoneNo: " + phoneNo + " != " + salesData.phoneNo);
            System.out.println("mQty: " + mQty + " != " + salesData.mQty);
            System.out.println("mProduct_name: " + mProduct_name + " != " + salesData.mProduct_name);
            System.out.println("mCost: " + mCost + " != " + salesData.mCost);
        }

        return isEqual;
    }

    @Override
    public int hashCode() {
        return Objects.hash(Bill_NO, customerName, phoneNo, mQty, mProduct_name, mCost, selectedOption);
    }

}
