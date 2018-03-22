package com.company;

/**
 * Created by Michaela on 21.3.2018.
 */
public class PredictRule {
    String terminal;
    Rule rule;

    public PredictRule(String terminal, Rule rule) {
        this.terminal = terminal;
        this.rule = rule;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }

    public Rule getRule() {
        return rule;
    }

    public void setRule(Rule rule) {
        this.rule = rule;
    }
}
