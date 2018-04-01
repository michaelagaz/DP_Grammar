package com.company;

/**
 * Created by Michaela on 5.2.2018.
 */
public class Rule {
    private String label;
    private String leftSide;
    private String rightSide;

    public Rule(String label, String leftSide, String rightSide) {
        this.label = label;
        this.leftSide = leftSide;
        this.rightSide = rightSide;
    }

    public Rule() {
    }


    public void setLabel(String label) {
        this.label = label;
    }

    public String getLeftSide() {
        return leftSide;
    }

    public void setLeftSide(String leftSide) {
        this.leftSide = leftSide;
    }

    public String getRightSide() {
        return rightSide;
    }

    public void setRightSide(String rightSide) {
        this.rightSide = rightSide;
    }

    public String getLabel() {
        return label;
    }


}
