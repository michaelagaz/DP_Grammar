package com.company;

/**
 * Created by Michaela on 21.2.2018.
 */
public class Transition {
    private String input;
    private String stackInput;
    private String result;
    private int serialNo;
    private boolean collection;

    public Transition(String input, String stackInput, String result, int serialNo, boolean collection) {
        this.input = input;
        this.stackInput = stackInput;
        this.result = result;
        this.serialNo = serialNo;
        this.collection = collection;
    }

    public Transition() {
    }

    public String getInput() {
        return input;
    }

    public void setInput(String input) {
        this.input = input;
    }

    public String getStackInput() {
        return stackInput;
    }

    public void setStackInput(String stackInput) {
        this.stackInput = stackInput;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }
}

