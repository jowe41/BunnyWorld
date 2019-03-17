package com.example.bunnyworld;

import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Script {

    private String scriptStr;
    private ArrayList<String> clauseList = new ArrayList<String>();;
    private ArrayList<String> onClickList = new ArrayList<String>();
    private ArrayList<String> onEnterList = new ArrayList<String>();
    private ArrayList<String> onDropList = new ArrayList<String>();

    private static final String ONCLICK = "on click";
    private static final String ONENTER = "on enter";
    private static final String ONDROP = "on drop";

    // Take in a shape, read through its script, get separated clauses and
    // classify all the actions into corresponding "Trigger Lists".
    public Script(String scr) {
        this.scriptStr = scr;
        String[] splitedScript = scriptStr.split(";");
        for (String str: splitedScript) {
            str.trim();
            clauseList.add(str);
        }

        addOnClickAction();
        addOnEnterAction();
        addOnDropAction();

    }

    // Determine if a script is legal (format only)
//    private boolean isScriptLegal(ArrayList<String> cList) {
//        int legalClause = 0;
//        for (String clause: cList) {
//            if (isClauseLegal(clause)) {
//                legalClause++;
//            }
//        }
//        if (legalClause == cList.size()) {
//            return true;
//        }
//        return false;
//    }
//
//    private boolean isClauseLegal(String clause) {
//        // The clause should include ONLY one trigger
//        int count = countOccurrence(clause, ONCLICK) + countOccurrence(clause, ONENTER) + countOccurrence(clause, ONDROP);;
//        if (count != 1) {
//            return false;
//        }
//        return true;
//    }

    // Add actions corresponding to ONCLICK trigger
    private void addOnClickAction() {
        String[] onClickAction = new String[0];
        for (int i=0; i<clauseList.size(); i++) {
            String clause = clauseList.get(i).trim();
            // Assume that there is ONLY ONE trigger, may require error check
            if (clause.contains(ONCLICK)) {
                int startIndex = clause.indexOf(ONCLICK) + ONCLICK.length() + 1;
                onClickAction = clause.substring(startIndex).split(" ");
            }
            if (onClickAction.length != 0) {
                for (String str: onClickAction) {
                    onClickList.add(str);
                }
            }
        }
    }

    // Add actions corresponding to ONENTER trigger
    private void addOnEnterAction() {
        String[] onEnterAction = new String[0];
        for (int i=0; i<clauseList.size(); i++) {
            String clause = clauseList.get(i);
            // Assume that there is ONLY ONE trigger, may require error check
            if (clause.contains(ONENTER)) {
                int startIndex = clause.indexOf(ONENTER) + ONENTER.length() + 1;
                onEnterAction = clause.substring(startIndex).split(" ");
            }
            if (onEnterAction.length != 0) {
                for (String str: onEnterAction) {
                    onEnterList.add(str);
                }
            }
        }
    }

    // Add actions corresponding to ONDROP trigger
    private void addOnDropAction() {
        String[] onDropAction = new String[0];
        for (int i=0; i<clauseList.size(); i++) {
            String clause = clauseList.get(i).trim();
            // Assume that there is ONLY ONE trigger, may require error check
            if (clause.contains(ONDROP)) {
                int startIndex = clause.indexOf(ONDROP) + ONDROP.length() + 1;
                onDropAction = clause.substring(startIndex).split(" ");
            }
            if (onDropAction.length != 0) {
                for (String str: onDropAction) {
                    onDropList.add(str);
                }
                onDropList.add(" ");      // Add a white space to indicate different onDrop objects and actions
            }
        }
    }

    // Take in a trigger and an object (only applicable for onDrop trigger),
    // return the corresponding actions
    public ArrayList<String> getActions(String trigger, String onDropObject) {
        if (trigger.equals(ONCLICK)) {
            return onClickList;
        } else if (trigger.equals(ONENTER)) {
            return onEnterList;
        } else if (trigger.equals(ONDROP)) {
            ArrayList<String> result = new ArrayList<String>();
            if (onDropList.contains(onDropObject)) {
                int startIndex = onDropList.indexOf(onDropObject) + 1;
                int endIndex = searchIndexOfSpace(startIndex, onDropList);
                for (int i = startIndex; i < endIndex; i++) {
                    result.add(onDropList.get(i));
                }
            }
            return result;
        }
        return null;
    }

    private int searchIndexOfSpace(int startIndex, ArrayList<String> searchList) {
        for (int i = startIndex; i<searchList.size(); i++) {
            if (searchList.get(i).equals(" ")) {
                return i;
            }
        }
        return 0;
    }

    private int countOccurrence(String str, String subStr) {
        int lastIndex = 0; int count = 0;
        while (lastIndex != -1) {
            lastIndex = str.indexOf(subStr, lastIndex);
            if (lastIndex != -1) {
                count++;
                lastIndex += subStr.length();
            }
        }
        return count;
    }

    @Override
    public String toString() {
        return scriptStr;
    }

}
