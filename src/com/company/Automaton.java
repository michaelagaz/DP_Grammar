package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

/**
 * Created by Michaela on 5.2.2018.
 */
public class Automaton {

    private Grammar grammar;

    public Automaton(Grammar grammar) {
        this.grammar = grammar;
    }

    public Automaton() {
        Grammar grammar = new Grammar(addNonTerminals(addRules()), addTerminals(addRules()), "S", addRules());
        this.grammar = grammar;
        System.out.println("NonTerminals: " + addNonTerminals(addRules()));
        System.out.println("Terminals: " + addTerminals(addRules()));
        grammar.getRulesOutput(addRules());

        createPDAfromCFG(grammar);
    }


    public Grammar getGrammar() {
        return grammar;
    }

    public void setGrammar(Grammar grammar) {
        this.grammar = grammar;
    }

    //Rules for Grammar in Lecture Sheet ---> hardcoded
    public List<Rule> addRules() {
        List<Rule> rules = new ArrayList<>();

        rules.add(new Rule("r1", "S", "aTb"));
        rules.add(new Rule("r2", "S", "b"));
        rules.add(new Rule("r3", "T", "Ta"));
        rules.add(new Rule("r4", "S", "ε"));

//        rules.add(new Rule("r1", "S", "AB"));
////        rules.add(new Rule("r2", "S", "cAB"));
//        rules.add(new Rule("r3", "A", "aAb"));
//        rules.add(new Rule("r4", "A", "b"));
//        rules.add(new Rule("r5", "B", "AB"));
//        rules.add(new Rule("r6" , "B", "c"));


        return rules;
    }

    public List<String> addNonTerminals(List<Rule> rules) {
        List<String> nonTerminals = new ArrayList<>();
        for (Rule rule : rules) {
            for (int i = 0; i < rule.getLeftSide().length(); i++) {
                if (isUpperCase(rule.getLeftSide().charAt(i))) {
                    if (!nonTerminals.contains(String.valueOf(rule.getLeftSide().charAt(i)))) {
                        nonTerminals.add(String.valueOf(rule.getLeftSide().charAt(i)));
                    }
                }
            }
        }
        return nonTerminals;
    }

    public List<String> addTerminals(List<Rule> rules) {
        List<String> terminals = new ArrayList<>();
        for (Rule rule : rules) {
            for (int i = 0; i < rule.getRightSide().length(); i++) {
                if (isLowerCase(rule.getRightSide().charAt(i))) {
                    if (!terminals.contains(String.valueOf(rule.getRightSide().charAt(i)))) {
                        terminals.add(String.valueOf(rule.getRightSide().charAt(i)));
                    }
                }
            }
        }
        return terminals;
    }

    public List<Rule> toRules(String[] array) {
        List<Rule> rules = new ArrayList<>();

        for (int i = 0; i < array.length; i++) {
            rules.add(getGrammar().findRuleByLabel(array[i]));
        }
        return rules;
    }

    public Stack createPDAfromCFG(Grammar grammar) {
        Stack stack = new Stack();

        stack.push("$");
        printPush("$");


        for (Rule r : grammar.getRules()) {
            if (stack.peek() == "$") {
                stack.push(grammar.getInitTerminal());
                printPush(grammar.getInitTerminal());
                if (r.getLeftSide() == stack.peek() && r.getRightSide().length() > 1) {
                    stack.set(stack.search(grammar.getInitTerminal()), r.getRightSide().charAt(r.getRightSide().length() - 1));
                    for (int i = r.getRightSide().length() - 2; i >= 0; i--) {
                        stack.push(r.getRightSide().charAt(i));
                    }

                }
            } else {
                if (r.getRightSide().length() > 1) {
                    stack.push(r.getLeftSide());
                    if (r.getLeftSide() == stack.peek() && r.getRightSide().length() > 1) {
                        stack.set(stack.search(r.getLeftSide()), r.getRightSide().charAt(r.getRightSide().length() - 1));
                        for (int i = r.getRightSide().length() - 2; i >= 0; i--) {
                            stack.push(r.getRightSide().charAt(i));
                        }

                    }
                }
            }
        }

        return stack;
    }

    public void printPush(String mess) {
        System.out.println("Pushed " + mess);
    }


    public void printPop(String mess) {
        System.out.println("Popped " + mess);
    }


}
