package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;

/**
 * Created by Michaela on 5.2.2018.
 */
public class Automaton {

    private Grammar grammar;

    private static final String EPSILON = "ε";

    private String derivatedWord = "bb";

    private int indexOption = 0;

    List<Rule> usedRules = new ArrayList<>();

    private int tserialNo = 0;
    Stack tempstack;

    String binaryWord = "0101";

    int pos;

    //number of Terminals, nonTerminals and rules for one nonTerminal -> used for generating grammar
    int numGenTerm = 5;
    int numGenNonTerm = 3;
    int numRulesPerNonTerm = 2;

    List<String> unusedNonTerminals;


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
//        mainFunction(grammar);

//        getFirst(grammar, "A");
//        getFollow(grammar, "S");

        doMainFunction(grammar);
        List<String> terminals = generateTerminals(numGenTerm);
        List<String> nonTerminals = generateNonTerminals(numGenNonTerm);
        unusedNonTerminals = new ArrayList<>(nonTerminals);

        List<Rule> rules = generateRules(nonTerminals, terminals, numRulesPerNonTerm);
        Rule labelZero = pickLabelForZero(rules);
        Rule labelOne = pickLabelForOne(rules, labelZero);
//        String binaryWord = getEncryptedWord(getUsedRulesToLabels(usedRules), labelZero.getLabel(), labelOne.getLabel());

        rules = generateExtraRules(rules, labelZero, labelOne, terminals, nonTerminals);
        Grammar grammarGen = new Grammar();
        grammarGen.setInitTerminal("S");
        grammarGen.setTerminals(terminals);
        grammarGen.setNonTerminals(nonTerminals);
        grammarGen.setRules(rules);


        System.out.println(rules);

        Grammar newGrammar = createSpecialGrammar(grammarGen);

        List<String> encryptedList = getEncryptedWordToLabels(binaryWord, labelZero.getLabel(), labelOne.getLabel());

        FiniteAutomaton finAutomaton = setAutomatonForRegularExpression(labelZero,labelOne,rules,binaryWord);


        System.out.println(newGrammar);
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

        rules.add(new Rule("r1", "S", "Cc"));
        rules.add(new Rule("r3", "A", "b"));
        rules.add(new Rule("r4", "A", EPSILON));
        rules.add(new Rule("r5", "B", "aS"));
        rules.add(new Rule("r6", "B", EPSILON));
        rules.add(new Rule("r7", "C", "AeCf"));
        rules.add(new Rule("r8", "C", "aB"));
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


    public String mainFunction(Grammar grammar) {
        Stack stack = new Stack();

        stack.push("$");
        stack.push(grammar.getInitTerminal());

//       while (pos <= derivatedWord.length() - 1) {
        if (!checkFunction(grammar, stack)) {
            return "declined";
        }
//        }

        return "accepted";
    }


    public boolean checkFunction(Grammar grammar, Stack stack) {
        tempstack = new Stack();
        tempstack = (Stack) stack.clone();
        if (grammar.isTerminal(String.valueOf(tempstack.peek())) && String.valueOf(derivatedWord.charAt(pos)).equals(tempstack.peek())) {
            tempstack.pop();
            if (pos == derivatedWord.length() - 1) {
                if (tempstack.peek().equals("$")) {
                    return true;
                }
            } else {
                pos++;
                return true;
            }
        } else if (grammar.isNonTerminal(String.valueOf(tempstack.peek()))) {
            while (indexOption != getAllSameRules(grammar.getRules(), String.valueOf(tempstack.peek())).size() - 1) {
                String wordBackWard = getWordBackWards(getAllSameRules(grammar.getRules(), String.valueOf(tempstack.peek())).get(indexOption).getRightSide());
                tempstack.remove(String.valueOf(tempstack.peek()));
                for (int j = 0; j < wordBackWard.length(); j++) {
                    if (!String.valueOf(wordBackWard.charAt(j)).equals(EPSILON)) {
                        tempstack.push(String.valueOf(wordBackWard.charAt(j)));
                    }
                }
                if (stack.peek().equals(tempstack.peek())) {
                    return false;
                } else {
                    if (!checkFunction(grammar, tempstack)) {
                        tempstack = (Stack) stack.clone();
                        indexOption++;
                    }
                }
            }
//            if (checkFunction(grammar, tempstack)) {
//                pos++;
//            }
        }

        return false;
    }


    public String doMainFunction(Grammar grammar) {
        Stack stack = new Stack();
        usedRules = new ArrayList<>();

        stack.push("$");
//        stack.push(grammar.getInitTerminal());
        stack.push("A");
        stack.push("A");
        while (!stack.peek().equals("$")) {
            Rule rule = predict(grammar, String.valueOf(stack.peek()));
            usedRules.add(rule);
            if (rule.equals(null)) {
                return "declined";
            } else {
                stack.pop();
                for (int i = 0; i < getWordBackWards(rule.getRightSide()).length(); i++) {
                    stack.push(String.valueOf(getWordBackWards(rule.getRightSide()).charAt(i)));
                }
                stack.pop();
                derivatedWord = deleteFirstChar(derivatedWord);
            }

        }
        if (derivatedWord.length() == 0) {
            return "accepted";
        }

        return "declined";
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

    public String deleteCharAt(String word, int index) {
        StringBuilder sb = new StringBuilder(word);
        sb.deleteCharAt(index);
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

    public List<PredictRule> getFirst(Grammar grammar, String nonTerminal) {
        List<PredictRule> firstTerminals = new ArrayList<>();

        int i = 0;
        if (grammar.isTerminal(String.valueOf(nonTerminal.charAt(0)))) {
            firstTerminals.add(new PredictRule(String.valueOf(nonTerminal.charAt(0)), null));
            return firstTerminals;
        } else if (grammar.isNonTerminal(String.valueOf(nonTerminal.charAt(0)))) {
            for (Rule r : grammar.getRules()) {
                if (r.getLeftSide().equals(String.valueOf(nonTerminal.charAt(0)))) {
                    if (grammar.isTerminal(String.valueOf(r.getRightSide().charAt(0)))) {
                        if (String.valueOf(r.getRightSide().charAt(0)).equals(EPSILON)) {
                            if (i < nonTerminal.length() - 1) {
                                i++;
                                for (PredictRule str : getFirst(grammar, String.valueOf(nonTerminal.charAt(i)))) {
                                    if (!firstTerminals.contains(str)) {
                                        firstTerminals.add(str);
                                    }
                                }
                            } else {
                                firstTerminals.add(new PredictRule(EPSILON, r));
                            }
                        } else if (!firstTerminals.contains(new PredictRule(String.valueOf(r.getRightSide().charAt(0)), r))) {
                            firstTerminals.add(new PredictRule(String.valueOf(r.getRightSide().charAt(0)), r));
                        }
                    }
                    if (grammar.isNonTerminal(String.valueOf(r.getRightSide().charAt(0)))) {
                        for (PredictRule str : getFirst(grammar, String.valueOf(r.getRightSide().charAt(0)))) {
                            if (str.getTerminal().equals(EPSILON) && grammar.isTerminal(String.valueOf(r.getRightSide().charAt(1)))) {
                                if (!firstTerminals.contains(new PredictRule(String.valueOf(r.getRightSide().charAt(1)), r))) {
                                    firstTerminals.add(new PredictRule(String.valueOf(r.getRightSide().charAt(1)), r));
                                }
                            } else if (!firstTerminals.contains(str)) {
                                firstTerminals.add(str);
                            }
                        }
                    }
                }
            }
        }
        return firstTerminals;
    }

    public List<PredictRule> getFollow(Grammar grammar, String nonTerminal) {
        List<PredictRule> followTerminals = new ArrayList<>();
        int counter = 0;
        if (grammar.isNonTerminal(String.valueOf(nonTerminal.charAt(0)))) {
            for (Rule r : grammar.getRules()) {
                if (r.getRightSide().contains(String.valueOf(nonTerminal.charAt(0)))) {
                    for (int i = 0; i < r.getRightSide().length(); i++) {
                        if (String.valueOf(r.getRightSide().charAt(i)).equals(String.valueOf(nonTerminal.charAt(0)))) {
                            if (i + 1 < r.getRightSide().length() - 1) {
                                if (grammar.isTerminal(String.valueOf(r.getRightSide().charAt(i + 1)))) {
                                    if (!followTerminals.contains(new PredictRule(String.valueOf(r.getRightSide().charAt(i + 1)), r))) {
                                        followTerminals.add(new PredictRule(String.valueOf(r.getRightSide().charAt(i + 1)), r));
                                    }
                                } else if (grammar.isNonTerminal(String.valueOf(r.getRightSide().charAt(i + 1)))) {
                                    for (PredictRule str : getFollow(grammar, String.valueOf(r.getRightSide().charAt(i + 1)))) {
                                        if (!followTerminals.contains(str)) {
                                            followTerminals.add(str);
                                        }
                                    }
                                }
                            }
//                            else {
//                                if (!followTerminals.contains(EPSILON)) {
//                                    followTerminals.add(EPSILON);
//                                }
//                            }
                        }

                    }
                }
            }
        }

        return followTerminals;
    }

    public Rule predict(Grammar grammar, String nonTerminal) {
        List<PredictRule> predictRules = getFirst(grammar, nonTerminal);
        List<Rule> rules = new ArrayList<>();

        for (PredictRule rule : predictRules) {
            if (rule.getTerminal().equals(String.valueOf(derivatedWord.charAt(0)))) {
                rules.add(rule.getRule());
            }
        }
        return rules.size() == 1 ? rules.get(0) : null;
    }

    public String getUsedRulesToLabels(List<Rule> rules) {
        String labels = "";
        for (Rule r : rules) {
            labels += r.getLabel();
        }
        return labels;
    }

    public String getEncryptedWord(String word, String label0, String label1) {
        String encrypted = "";
        encrypted = word.replaceAll(label0, "0");
        encrypted = word.replaceAll(label1, "1");

        return encrypted;
    }

    public List<String> getEncryptedWordToLabels(String word, String label0, String label1) {
        List<String> encrypted = new ArrayList<>();
       for(int i= 0 ; i< word.length(); i++ ){
           if(String.valueOf(word.charAt(i)).equals("0")){
               encrypted.add(label0);
           }
           else if(String.valueOf(word.charAt(i)).equals("1")){
               encrypted.add(label1);
           }
       }

        return encrypted;
    }

    //GENERATOR OF GRAMMAR
    public Grammar generateGrammar() {
        return new Grammar();
    }

    public List<String> generateTerminals(int number) {
        List<String> terminals = new ArrayList<>();

        for (int i = 97; i < 97 + number; i++) {
            terminals.add(Character.toString((char) i));
        }
        return terminals;
    }

    public List<String> generateNonTerminals(int number) {
        List<String> nonTerminals = new ArrayList<>();

        //initNonTerminal - static
        nonTerminals.add("S");

        for (int i = 65; i < 65 + number - 1; i++) {
            if (!nonTerminals.contains(Character.toString((char) i))) {
                nonTerminals.add(Character.toString((char) i));
            }
        }
        return nonTerminals;
    }

    public List<Rule> generateRules(List<String> nonTerminals, List<String> terminals, int numRules) {
        List<Rule> generatedRules = new ArrayList<>();
        int counter = 0;
        for (String str : nonTerminals) {
            for (int i = 0; i < numRules; i++) {
                Rule rule = new Rule();
                rule.setLeftSide(str);
                rule.setLabel("r" + counter);
                String rightSide = generateRightSide(nonTerminals, terminals, str, generatedRules, i);

                rule.setRightSide(rightSide);

                generatedRules.add(rule);
                counter++;
            }


        }

        return generatedRules;
    }

    public String generateRightSideOriginal(List<String> nonTerminals, List<String> terminals, String nonTerminal, List<Rule> rules, int index) {
        String rightSide = "";
        boolean notGenerated = true;
        String terminal = "";

        while (notGenerated) {
            int randomNum = generateRandInt(0, terminals.size() - 1);
            terminal = terminals.get(randomNum);
            notGenerated = isTerminalUsed(terminal, nonTerminal, rules);
        }

        rightSide += terminal;

        if (index == 0) {
            //generovanie nahodnej dlzky pravidla
            int randomNum = generateRandInt(1, 5);
            for (int i = 0; i < randomNum; i++) {
                int randomPosition = generateRandInt(0, terminals.size() - 1);
                rightSide += terminals.get(randomPosition);
            }
        } else if (index == 1) {
            List<String> concatedList = new ArrayList<String>(nonTerminals);
            concatedList.addAll(terminals);

            int randomNum = generateRandInt(1, 3);
            for (int i = 0; i < randomNum; i++) {
                int randomPosition = generateRandInt(0, concatedList.size() - 1);
                rightSide += concatedList.get(randomPosition);
                if (unusedNonTerminals.contains(concatedList.get(randomPosition))) {
                    unusedNonTerminals.remove(concatedList.get(randomPosition));
                }
                unusedNonTerminals.remove(nonTerminal);
            }
            rightSide += nonTerminal;

            if (unusedNonTerminals.size() > 0) {
                rightSide += unusedNonTerminals.get(0);
                unusedNonTerminals.remove(0);
            }
        } else {
            List<String> concatedList = new ArrayList<String>(nonTerminals);
            concatedList.addAll(terminals);

            int randomNum = generateRandInt(1, 4);
            for (int i = 0; i < randomNum; i++) {
                int randomPosition = generateRandInt(0, concatedList.size() - 1);
                rightSide += concatedList.get(randomPosition);
            }
            if (unusedNonTerminals.size() > 0) {
                rightSide += unusedNonTerminals.get(0);
                unusedNonTerminals.remove(0);
            }
        }

        rightSide = shuffleString(rightSide);
        return rightSide;
    }

    public String generateRightSide(List<String> nonTerminals, List<String> terminals, String nonTerminal, List<Rule> rules, int index) {
        String rightSide = "";
        boolean notGenerated = true;
        String terminal = "";

        while (notGenerated) {
            int randomNum = generateRandInt(0, terminals.size() - 1);
            terminal = terminals.get(randomNum);
            notGenerated = isTerminalUsed(terminal, nonTerminal, rules);
        }

        rightSide += terminal;

        //iba terminaly
        if (index == 0) {
            //generovanie nahodnej dlzky pravidla
            int randomNum = generateRandInt(1, 5);
            for (int i = 0; i < randomNum; i++) {
                int randomPosition = generateRandInt(0, terminals.size() - 1);
                rightSide += terminals.get(randomPosition);
            }
        }

        //neterminal- rekurzia
        else if (index == 1) {
//            List<String> concatedList = new ArrayList<String>(nonTerminals);
//            concatedList.addAll(terminals);
            rightSide += nonTerminal;
            unusedNonTerminals.remove(nonTerminal);
            int randomNum = generateRandInt(0, 3);
            for (int i = 0; i < randomNum; i++) {
                int randomPosition = generateRandInt(0, terminals.size() - 1);
                rightSide += terminals.get(randomPosition);
            }
        }

        //hocico
        else {
            if (unusedNonTerminals.size() > 0) {
                rightSide += unusedNonTerminals.get(0);
                unusedNonTerminals.remove(0);

                int randomNum = generateRandInt(1, 3);
                for (int i = 0; i < randomNum; i++) {
                    int randomPosition = generateRandInt(0, terminals.size() - 1);
                    rightSide += terminals.get(randomPosition);
                }
            } else {
                int randomBool = generateRandInt(0, 1);
                if (randomBool == 1) {
                    int randomPosition = generateRandInt(0, nonTerminals.size() - 1);
                    rightSide += nonTerminals.get(randomPosition);

                }
                int randomNum = generateRandInt(1, 4);
                for (int i = 0; i < randomNum; i++) {
                    int randomPosition = generateRandInt(0, terminals.size() - 1);
                    rightSide += terminals.get(randomPosition);
                }
            }
        }

        rightSide = shuffleString(rightSide);
        return rightSide;
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

    //check if there is rekurzia in the rightside
    public boolean isLoopUsed(String nonTerminal, List<Rule> rules) {
        for (Rule r : rules) {
            if (r.getLeftSide().equals(nonTerminal)) {
                for (int i = 0; i < r.getRightSide().length(); i++) {
                    if (String.valueOf(r.getRightSide().charAt(i)).equals(nonTerminal)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public boolean areOnlyTerminals(String nonTerminal, List<Rule> rules, List<String> terminals) {
        for (Rule r : rules) {
            if (r.getLeftSide().equals(nonTerminal)) {
                for (int i = 0; i < r.getRightSide().length(); i++) {
                    if (!terminals.contains(String.valueOf(r.getRightSide().charAt(i)))) {
                        return false;
                    }
                }
            }
        }
        return true;
    }


    public String shuffleString(String input) {
        List<Character> characters = new ArrayList<Character>();
        for (char c : input.toCharArray()) {
            characters.add(c);
        }
        StringBuilder output = new StringBuilder(input.length());
        output.append(characters.remove(0));
        while (characters.size() != 0) {
            int randPicker = generateRandInt(0, characters.size() - 1);
            output.append(characters.remove(randPicker));
        }
        return (output.toString());
    }

    public int generateRandInt(int min, int max) {

        Random rand = new Random();

        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

    public Rule pickLabelForZero(List<Rule> rules) {
        int randomPosition = generateRandInt(0, rules.size() - 1);
        return rules.get(randomPosition);
    }

    public Rule pickLabelForOne(List<Rule> rules, Rule zeroLabel) {
        boolean flag = true;
        int randomPosition = 0;
        while (flag) {
            randomPosition = generateRandInt(0, rules.size() - 1);
            flag = rules.get(randomPosition).getLabel().equals(zeroLabel.getLabel());
        }
        return rules.get(randomPosition);
    }


    //extra rules for one and zero label
    public List<Rule> generateExtraRules(List<Rule> rules, Rule zero, Rule one, List<String> terminals, List<String> nonTerminals) {
        boolean nonGenerated = true;
        boolean nonGenerated2 = true;
        String terminal = "";

        String rightSide = "";

        if(!zero.getLeftSide().equals(one.getLeftSide())) {

            while (nonGenerated) {
                int randomNum = generateRandInt(0, terminals.size() - 1);
                terminal = terminals.get(randomNum);
                nonGenerated = isTerminalUsed(terminal, zero.getLeftSide(), rules);
            }

            Rule rule = new Rule();
            rule.setLeftSide(zero.getLeftSide());
            rule.setLabel("r" + rules.size());
            rightSide += terminal;
            rightSide += one.getLeftSide();

            List<String> concatedList = new ArrayList<String>(nonTerminals);
            concatedList.addAll(terminals);

            int randomNum = generateRandInt(1, 3);
            for (int i = 0; i < randomNum; i++) {
                int randomPosition = generateRandInt(0, concatedList.size() - 1);
                rightSide += concatedList.get(randomPosition);
            }

            rule.setRightSide(shuffleString(rightSide));
            rules.add(rule);
            rightSide = "";

            while (nonGenerated2) {
                int randomNum2 = generateRandInt(0, terminals.size() - 1);
                terminal = terminals.get(randomNum2);
                nonGenerated2 = isTerminalUsed(terminal, one.getLeftSide(), rules);
            }

            rule = new Rule();
            rule.setLeftSide(one.getLeftSide());
            rule.setLabel("r" + rules.size());
            rightSide += terminal;
            rightSide += zero.getLeftSide();

            randomNum = generateRandInt(1, 3);
            for (int i = 0; i < randomNum; i++) {
                int randomPosition = generateRandInt(0, concatedList.size() - 1);
                rightSide += concatedList.get(randomPosition);
            }

            rule.setRightSide(shuffleString(rightSide));
            rules.add(rule);
        }

        return rules;
    }

    public Grammar createSpecialGrammar(Grammar grammar){
        Grammar newGrammar= new Grammar();
        newGrammar.setInitTerminal(grammar.getInitTerminal());
        newGrammar.setNonTerminals(grammar.getNonTerminals());
        List<String> terminals = new ArrayList<>();
        List<Rule> rules = new ArrayList<>();

        for(Rule r: grammar.getRules()){
            Rule newRule = new Rule();
            newRule.setLeftSide(r.getLeftSide());
            terminals.add(r.getLabel());
            newRule.setLabel(r.getLabel());
            newRule.setRightSide(createSpecialRightside(r, grammar));

            rules.add(newRule);
        }
        newGrammar.setTerminals(terminals);
        newGrammar.setRules(rules);

        return newGrammar;
    }

    public String createSpecialRightside(Rule rule, Grammar grammar){
        String newLeft = "";
        newLeft +=rule.getLabel();
        for(int i = 0; i< rule.getRightSide().length(); i++){
            if(grammar.isNonTerminal(String.valueOf(rule.getRightSide().charAt(i)))){
                newLeft += String.valueOf(rule.getRightSide().charAt(i));
            }
        }

        return newLeft;
    }

    public boolean startAutomatonForRegularExpression(List<String> word){
        //pozriem co ma na vstupe a v akom stave som -> podla toho sa posuniem dalej
        for(String str : word){
        }

        return true;

    }

    public FiniteAutomaton setAutomatonForRegularExpression(Rule label0, Rule label1,List<Rule> rules, String word){
        List<String> encryptedWordToLabels = getEncryptedWordToLabels(word,label0.getLabel(), label1.getLabel());
        FiniteAutomaton automat = new FiniteAutomaton();
        List<String> states = new ArrayList<>();
        List<String> alphabet = new ArrayList<>();

        List<Rule> allOtherRules = getUnusedLabelsForOneAndZero(label0, label1, rules);

         List<AutomatonRule> fRules = new ArrayList<>();


        for(int i = 0; i<= word.length(); i++){
            states.add("q"+i);
        }
        automat.setStates(states);
        automat.setFiniteSymbol(states.get(states.size()-1));
        automat.setInitSymbol(states.get(0));

        for(int i = 0; i< states.size(); i++){

            for(Rule r: allOtherRules){
                fRules.add( new AutomatonRule(states.get(i), states.get(i), r.getLabel()));
            }
            if(!automat.getFiniteSymbol().equals(states.get(i))) {
                fRules.add(new AutomatonRule(states.get(i), states.get(i+1), encryptedWordToLabels.get(0)));
                encryptedWordToLabels.remove(0);
            }

        }
        automat.setRules(fRules);
        for(Rule r: rules){
            alphabet.add(r.getLabel());
        }

        automat.setAlphabet(alphabet);

        return automat;
    }


    public List<Rule> getUnusedLabelsForOneAndZero(Rule label0, Rule label1, List<Rule> allRules){
        List<Rule> allOtherRules = new ArrayList<>();

        for(Rule rule: allRules){
            if((rule.getLabel()!= label0.getLabel()) && rule.getLabel()!= label1.getLabel()){
                allOtherRules.add(rule);
            }
        }

        return allOtherRules;
    }



//    public List<AutomatonRule> getAllStatesFromThisState(String state){
//
//    }
}
