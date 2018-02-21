package com.company;

/**
 * Created by Michaela on 21.2.2018.
 */
public class Transition {
    private String input;
    private String stackInput;
    private String result;

    public Transition(String input, String stackInput, String result) {
        this.input = input;
        this.stackInput = stackInput;
        this.result = result;
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
}

