package utils;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MethodFinder {
  boolean functionNameNotFound = false;
  boolean nextLineIsNeeded = false;
  ArrayList<ArrayList<Statement>> allMethods = new ArrayList<>();

  public ArrayList<ArrayList<Statement>> findMethods(ArrayList<Statement> allLines) {

    for (int i = 0; i < allLines.size(); i++) {
      String currentStatement = allLines.get(i).getStatement();
      String nextStatement = i < allLines.size() - 1 ? allLines.get(i + 1).getStatement() : null;
      if (isStartingStatement(currentStatement, nextStatement)) {
        if (nextLineIsNeeded && !currentStatement.endsWith("{")) {
          i++;
          nextLineIsNeeded = false;
          functionNameNotFound = false;
        }
        i++;
        ArrayList<Statement> method = new ArrayList<>();
        boolean nextMethodFound = false;
        while (!nextMethodFound) {
          /**
           * Try to find next method
           */
          String tmpCurrent = null, tmpNext = null;
          if (i < allLines.size())
            tmpCurrent = allLines.get(i).getStatement();
          if (i < allLines.size() - 1)
            tmpNext = allLines.get(i + 1).getStatement();
          if (isStartingStatement(tmpCurrent, tmpNext)) {
            i--;
            nextMethodFound = true;
            break;
          }
          if (i >= allLines.size())
            break;
          method.add(allLines.get(i));
          i++;
        }
        /**
         * Remove the close curly bracket of the method
         */
        method.remove(method.size() - 1);
        allMethods.add(method);
      }
    }
    /**
     * Remove the close curly bracket of the class
     */
    allMethods.get(allMethods.size() - 1).remove(allMethods.get(allMethods.size() - 1).size() - 1);
    return allMethods;
  }

  private boolean isStartingStatement(String currentStatement, String nextStatement) {
    if (currentStatement == null)
      return false;

    boolean curlyBracketFound = currentStatement.endsWith("{");

    if (nextStatement != null) {
      Matcher matcher = Pattern.compile("\\{").matcher(nextStatement);
      if (!curlyBracketFound)
        functionNameNotFound = true;
      if (matcher.find()) {
        curlyBracketFound = true;
        if (functionNameNotFound)
          nextLineIsNeeded = true;
      }
    }

    return !hasClassDecleration(currentStatement) && !hasKeyWords(currentStatement)
        && !endWithSemicolon(currentStatement) && hasWords(currentStatement) && hasBreacket(currentStatement)
        && curlyBracketFound;
  }

  private boolean hasKeyWords(String str) {
    ArrayList<String> keyWords = new ArrayList<String>();
    keyWords.add("for");
    keyWords.add("while");
    keyWords.add("if");
    keyWords.add("}");

    for (String keyword : keyWords) {
      Pattern pattern = Pattern.compile(keyword);
      Matcher matcher = pattern.matcher(str);
      if (matcher.find())
        return true;
    }

    return false;
  }

  private boolean endWithSemicolon(String str) {
    return str.endsWith(";");
  }

  private boolean hasBreacket(String str) {
    boolean startingBracker = false, endingBracket = false;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c == '(')
        startingBracker = true;
      if (c == ')')
        endingBracket = true;
    }
    return startingBracker && endingBracket;
  }

  private boolean hasWords(String str) {
    String[] words = str.split(" ");
    return words.length >= 2;
  }

  private boolean hasClassDecleration(String str) {
    Pattern pattern = Pattern.compile("class");
    Matcher matcher = pattern.matcher(str);
    return matcher.find();
  }
}
