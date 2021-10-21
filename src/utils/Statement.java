package utils;

public class Statement {
  private String statement;
  private int lineNumber;

  public Statement(String statement, int lineNumber) {
    this.statement = statement;
    this.lineNumber = lineNumber;
  }

  public String getStatement() {
    return this.statement;
  }

  public int getLineNumber() {
    return this.lineNumber;
  }

  public String toString() {
    return String.format("%d:%s", this.lineNumber, this.statement);
  }
}
