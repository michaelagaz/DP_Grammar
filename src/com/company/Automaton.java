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

    private static final String EPSILON = "ε";

    private String derivatedWord = "ab";

    private int indexOption = 0;

    private int tserialNo = 0;
    List<String> tempstack;

    int pos;

    public Automaton(Grammar grammar) {
        this.grammar = grammar;
    }

    public Automaton() {
        Grammar grammar = new Grammar(addNonTerminals(addRules()), addTerminals(addRules()), "S", addRules());
        this.grammar = grammar;
        System.out.println("NonTerminals: " + addNonTerminals(addRules()));
        System.out.println("Terminals: " + addTerminals(addRules()));
        grammar.getRulesOutput(addRules());

//        createPDAfromCFG(grammar);
        mainFunction(grammar);
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
        rules.add(new Rule("r4", "T", "ε"));

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


    //TODO: return accepted or declined instead of Stack, redo the automaton -> new rules to obey in diary (consultation),
    //TODO: TREBA ZACAT S NETERMINALMI!!! -> v loope robit cely cyklus push-> check -> push -> check etc etc.

    public String mainFunction(Grammar grammar) {
//        Stack stack = new Stack();
//
//        stack.push("$");
//        printPush("$");

        List<String> stack = new ArrayList();

        stack.add("$");
        stack.add(grammar.getInitTerminal());

        for (pos = 0; pos < derivatedWord.length(); pos++) {
            if (checkFunction(grammar, stack) && pos == derivatedWord.length() - 1 && stack.get(pos).equals("$")) {
                return "accepted";
            }
        }

        return "declined";
    }


    public boolean checkFunction(Grammar grammar, List<String> stack) {
        tempstack = new ArrayList<>();
        tempstack = cloneStack(stack);
        if (grammar.isTerminal(String.valueOf(stack.get((stack.size() - 1) - pos))) && String.valueOf(derivatedWord.charAt(pos)).equals(stack.get((stack.size() - 1) - pos))) {
            pos++;
            return true;
        } else if (grammar.isNonTerminal(String.valueOf(stack.get((stack.size() - 1) - pos)))) {

            String wordBackWard = getWordBackWards(getAllSameRules(grammar.getRules(), String.valueOf(tempstack.get((tempstack.size() - 1) - pos))).get(indexOption).getRightSide());
            tempstack.remove(String.valueOf(tempstack.get((tempstack.size() - 1) - pos)));
            for (int j = 0; j < wordBackWard.length(); j++) {
                tempstack.add(String.valueOf(wordBackWard.charAt(j)));
            }
//            if (checkFunction(grammar, tempstack)) {
//                pos++;
//            }
            if(!checkFunction(grammar, tempstack)){
                tempstack = cloneStack(stack);
                indexOption++;
            }


        }
        return false;
    }

    public String createPDAfromCFG(Grammar grammar) {
        Stack stack = new Stack();
        boolean flag = true;
        boolean flag2 = true;

        stack.push("$");
        printPush("$");

        List<Transition> allTransitions = makeTransitions(grammar);

        stack.push(grammar.getInitTerminal());

        //TU ZACNE LOOP (while)...!!!

        while (flag) {
            if (grammar.isNonTerminal(String.valueOf(stack.peek()))) {
                //tu zavolat funkciu, ktora prejde vsetky allTranstions
                //ak je collection TRUE, treba aby zbehli vsetky pravidla za sebou v ramci jedneho serioveho cisla
                //ak je stack input transition  == to co je na stacku -> replace prve pravidlo v kolekcii, ostatne push ->
                //-> zase kontrola vo while cykle

                //dorobit este - ak je viacero transitions v ramci jedneho pravidla (loop) -> bud ako list v Liste,
                //alebo si nejako oznacit kolekciu, ktore pravidla k sebe patria atd - done


                //dorobit vetvenie -> ak S tak si moze vybrat 2 smery kadial ist atd.
                while (flag2) {
                    Stack tempStack = new Stack();
                    tempStack = (Stack) stack.clone();
                    for (Transition t : getAllSameLetterTransitions(stack.peek().toString(), allTransitions)) {
                        if (t.getStackInput().equals(tempStack.peek())) {
                            if (t.isCollection() && (t.getSerialNo() == tserialNo)) {
                                for (Transition tc : getAllTransitionsInCollection(tserialNo, allTransitions)) {
                                    if (tc.getStackInput().equals(tempStack.peek()) && !tc.getResult().equals(EPSILON)) {
                                        tempStack.set(tempStack.search(tc.getStackInput()), tc.getResult());
                                    } else if (tc.getStackInput().equals(EPSILON) && tc.getInput().equals(EPSILON)) {
                                        tempStack.push(tc.getResult());
                                    }
                                }
                            } else {
                                tserialNo = t.getSerialNo();
                            }
                        }
                        if (grammar.isTerminal(String.valueOf(tempStack.peek()))) {
                            if (tempStack.peek().equals(String.valueOf(derivatedWord.charAt(0)))) {
                                tempStack.pop();
                                derivatedWord = deleteFirstChar(derivatedWord);

                            } else {
//                                flag2 = false;
//                                break;
                                //return declined
                            }
                        } else {

                        }
                    }
                }
            } else if (grammar.isTerminal(String.valueOf(stack.peek()))) {
                if (stack.peek().equals(derivatedWord.charAt(0))) {
                    stack.pop();
                    derivatedWord = deleteFirstChar(derivatedWord);

                } else {
                    flag = false;
                    return "declined";
                    //return declined
                }
            }
        }

//        for (Rule r : grammar.getRules()) {
//            if (stack.peek() == "$") {
//                stack.push(grammar.getInitTerminal());
//                printPush(grammar.getInitTerminal());
//                if (r.getLeftSide() == stack.peek() && r.getRightSide().length() > 1) {
//                    stack.set(stack.search(grammar.getInitTerminal()), r.getRightSide().charAt(r.getRightSide().length() - 1));
//                    for (int i = r.getRightSide().length() - 2; i >= 0; i--) {
//                        stack.push(r.getRightSide().charAt(i));
//                    }
//
//                }
//            } else {
//                if (r.getRightSide().length() > 1) {
////                    stack.push(r.getLeftSide());
//                    //r.getLeftSide() == stack.peek();
//                    System.out.println(r.getRightSide().charAt(r.getRightSide().length() - 1));
//                    System.out.println(stack.search(Character.valueOf(r.getLeftSide().charAt(0))));
//                    stack.set(stack.indexOf(Character.valueOf(r.getLeftSide().charAt(0))), r.getRightSide().charAt(r.getRightSide().length() - 1));
//                    for (int i = r.getRightSide().length() - 2; i >= 0; i--) {
//                        stack.push(r.getRightSide().charAt(i));
//                    }
//
//                }
//            }
//        }
//
//        for (Rule r : grammar.getRules()) {
//            if (r.getRightSide().length() == 1) {
//                if (stack.indexOf(Character.valueOf(r.getLeftSide().charAt(0))) != -1) {
//                    System.out.println(Character.valueOf(r.getLeftSide().charAt(0)));
//                    System.out.println(stack.indexOf(Character.valueOf(r.getLeftSide().charAt(0))));
//                    stack.set(stack.indexOf(Character.valueOf(r.getLeftSide().charAt(0))), r.getRightSide().charAt(0));
//                }
//            }
//        }
//
//        while(stack.size() > 1){
//            for (String t : grammar.getTerminals()) {
//                System.out.println(Character.valueOf(t.charAt(0)));
//                System.out.println(stack.peek());
//                System.out.println(stack.peek().equals(Character.valueOf(t.charAt(0))));
//                    if(stack.peek().equals(Character.valueOf(t.charAt(0)))){
//                        System.out.println(Character.valueOf(t.charAt(0)));
//                       Object returned = stack.pop();
//                    }
//                }
//            }

        return "accepted";
    }

    public List<Transition> makeTransitions(Grammar grammar) {
        List<Transition> transitions = new ArrayList<>();
        int counter = 0;


        for (Rule r : grammar.getRules()) {
            if (r.getRightSide().length() > 1) {
                if (transitions.indexOf(new Transition(EPSILON, r.getLeftSide(), String.valueOf(r.getRightSide().charAt(r.getRightSide().length() - 1)), counter, true)) == -1) {
                    transitions.add(new Transition(EPSILON, r.getLeftSide(), String.valueOf(r.getRightSide().charAt(r.getRightSide().length() - 1)), counter, true));
                }
                for (int i = r.getRightSide().length() - 2; i >= 0; i--) {
                    if (transitions.indexOf(new Transition(EPSILON, EPSILON, String.valueOf(r.getRightSide().charAt(i)), counter, true)) == -1) {
                        transitions.add(new Transition(EPSILON, EPSILON, String.valueOf(r.getRightSide().charAt(i)), counter, true));
                    }

                }
                counter++;
            } else {
                if (transitions.indexOf(new Transition(EPSILON, r.getLeftSide(), r.getRightSide(), counter, false)) == -1) {
                    transitions.add(new Transition(EPSILON, r.getLeftSide(), r.getRightSide(), counter, false));
                    counter++;
                }
            }
        }

        for (String terminal : grammar.getTerminals()) {
            if (transitions.indexOf(new Transition(terminal, terminal, EPSILON, counter, false)) == -1 && !terminal.equals(EPSILON)) {
                transitions.add(new Transition(terminal, terminal, EPSILON, counter, false));
                counter++;
            }
        }

        return transitions;
    }

    public void printPush(String mess) {
        System.out.println("Pushed " + mess);
    }


    public void printPop(String mess) {
        System.out.println("Popped " + mess);
    }

    public String deleteFirstChar(String word) {
        StringBuilder sb = new StringBuilder(word);
        sb.deleteCharAt(0);
        String resultString = sb.toString();
        return resultString;
    }

    public List<Transition> getAllTransitionsInCollection(int serialNo, List<Transition> transitions) {
        List<Transition> allTransitionInCollection = new ArrayList<>();

        for (Transition t : transitions) {
            if (t.getSerialNo() == serialNo) {
                allTransitionInCollection.add(t);
            }
        }
        return allTransitionInCollection;
    }

    public List<Transition> getAllSameLetterTransitions(String beginLetter, List<Transition> transitions) {
        List<Transition> allTransition = new ArrayList<>();

        for (Transition t : transitions) {
            if (t.getStackInput() == beginLetter) {
                allTransition.add(t);
            }
        }
        return allTransition;
    }

    public List<Rule> getAllSameRules(List<Rule> rules, String letter) {
        List<Rule> sortedRules = new ArrayList<>();
        for (Rule r : rules) {
            if (r.getLeftSide().equals(letter)) {
                sortedRules.add(r);
            }
        }

        return sortedRules;
    }

    public String getWordBackWards(String word) {
        String temp = "";

        for (int i = word.length() - 1; i >= 0; i--) {
            temp += String.valueOf(word.charAt(i));
        }
        return temp;
    }

    public List<String> cloneStack(List<String> stack) {
        List<String> temp = new ArrayList<>();
        for (String s : stack) {
            temp.add(s);
        }

        return temp;
    }
}
