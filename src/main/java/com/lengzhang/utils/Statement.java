package com.lengzhang.utils;

public class Statement {
  private String statement;
  private String lineNumber;

  public Statement(String statement, String lineNumber) {
    this.statement = statement;
    this.lineNumber = lineNumber;
  }

  public String getStatement() {
    return this.statement;
  }

  public String getLineNumber() {
    return this.lineNumber;
  }

  public String toString() {
    return String.format("%s:%s", this.lineNumber, this.statement);
  }
}