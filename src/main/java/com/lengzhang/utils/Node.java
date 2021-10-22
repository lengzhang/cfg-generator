package com.lengzhang.utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

enum NodeType {
  IF, ELSE_IF, ELSE, DO_WHILE, DO_WHILE_END, WHILE, WHILE_END, FOR, FOR_END, NONE
}

public class Node {
  private Set<Integer> parents = new HashSet<>();
  private Set<Integer> children = new HashSet<>();
  private ArrayList<Statement> statements = new ArrayList<>();

  private int id;

  public NodeType type = NodeType.NONE;

  public boolean isIf = false;
  public boolean isElse = false;
  public boolean isElseIf = false;

  public boolean hasOpenParenthesis = false;
  public boolean hasCloseParenthesis = false;

  public boolean isClosed = false;

  public Node(int id) {
    this.id = id;
  }

  public Set<Integer> getParents() {
    return this.parents;
  }

  public Set<Integer> getChildren() {
    return this.children;
  }

  public ArrayList<Statement> getStatements() {
    return this.statements;
  }

  public int getId() {
    return this.id;
  }

  public void addParent(Node parent) {
    this.addParent(parent.getId());
  }

  public void addParent(int parentId) {
    this.parents.add(parentId);
  }

  public void addChild(Node child) {
    this.addChild(child.id);
  }

  public void addChild(int childNumber) {
    this.children.add(childNumber);
  }

  public void addStatement(Statement statement) {
    this.statements.add(statement);
  }

  public boolean hasChild(int childNumber) {
    return this.children.contains(childNumber);
  }

  public void print() {
    System.out.println("id:\t" + this.id);
    String parentsStr = "";
    for (int parentId : this.parents) {
      parentsStr += parentId + " ";
    }
    System.out.println("type:\t" + this.type);
    System.out.println("Parents:\t" + parentsStr);
    String childrenStr = "";
    for (int childId : this.children) {
      childrenStr += childId + " ";
    }
    System.out.println("Children:\t" + childrenStr);
    System.out.println("Statements:");

    for (Statement statement : this.statements) {
      System.out.println(statement.getLineNumber() + ":" + statement.getStatement());
    }

    System.out.println("isClosed:\t" + isClosed);
  }

  public String toString() {
    /** Parents */
    String parentsStr = "";
    for (int parentId : this.parents) {
      parentsStr += parentId + " ";
    }
    /** Children */
    String childrenStr = "";
    for (int childId : this.children) {
      childrenStr += childId + " ";
    }
    /** Statements */
    String statementsStr = "";
    for (Statement statement : this.statements) {
      statementsStr += "  Line " + statement + "\n";
    }

    return String.format("[%d]\ntype:\t\t%s\nParents:\t%s\nChildren:\t%s\nStatements:\n%s", this.id, this.type,
        parentsStr, childrenStr, statementsStr);
  }
}
