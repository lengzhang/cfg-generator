package utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class CodeReader {
  public ArrayList<Statement> read(String path) {
    ArrayList<Statement> allLines = new ArrayList<>();
    File file = new File(path);
    BufferedReader bufferedReader = null;

    try {
      bufferedReader = new BufferedReader(new FileReader(file));
    } catch (FileNotFoundException e) {
      System.out.println("Cannot prepare BufferedReader :(");
      e.printStackTrace();
      System.out.println(")");
    }

    String str;
    int lineNumber = 1;
    try {
      while ((str = bufferedReader.readLine()) != null) {
        if (isNotBlankLine(str)) {
          allLines.add(new Statement(str, String.valueOf(lineNumber)));
        }
        lineNumber++;
      }
    } catch (IOException e) {
      System.out.println("Can not read Line from files :(");
      e.printStackTrace();
      System.out.println(")");
    }

    return allLines;
  }

  private boolean isNotBlankLine(String str) {
    int count = 0;
    for (int i = 0; i < str.length(); i++) {
      char c = str.charAt(i);
      if (c == ' ')
        continue;
      else if (c == '\n')
        continue;
      else if (c == '\t')
        continue;
      else
        count++;
    }
    return count > 0;
  }
}
