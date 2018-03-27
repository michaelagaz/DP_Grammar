package com.company;

import java.util.List;

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

    //check, if terminal is already in use -> used in GRAMMAR GENERATOR
    public boolean isTerminalUsed(String terminal, String nonTerminal, List<Rule> rules) {
        for (Rule r : rules) {
            if (r.getLeftSide().equals(nonTerminal)) {
                if (String.valueOf(r.getRightSide().charAt(0)).equals(terminal)) {
                    return true;
                }
            }
        }

        return false;
    }
}
