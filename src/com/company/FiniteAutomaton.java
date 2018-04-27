package com.company;

import java.util.List;

/**
 * Created by Michaela on 27.4.2018.
 */
public class FiniteAutomaton {
    private String initSymbol;

    public String getFiniteSymbol() {
        return finiteSymbol;
    }

    public void setFiniteSymbol(String finiteSymbol) {
        this.finiteSymbol = finiteSymbol;
    }

    private String finiteSymbol;
    private List<String> states;
    private List<String> alphabet;
    private List<AutomatonRule> rules;

    public FiniteAutomaton() {
    }

    public FiniteAutomaton(String initSymbol, List<String> states, List<String> alphabet, List<AutomatonRule> rules) {
        this.initSymbol = initSymbol;
        this.states = states;
        this.alphabet = alphabet;
        this.rules = rules;
    }

    public String getInitSymbol() {
        return initSymbol;
    }

    public void setInitSymbol(String initSymbol) {
        this.initSymbol = initSymbol;
    }

    public List<String> getStates() {
        return states;
    }

    public void setStates(List<String> states) {
        this.states = states;
    }

    public List<String> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(List<String> alphabet) {
        this.alphabet = alphabet;
    }

    public List<AutomatonRule> getRules() {
        return rules;
    }

    public void setRules(List<AutomatonRule> rules) {
        this.rules = rules;
    }


}
