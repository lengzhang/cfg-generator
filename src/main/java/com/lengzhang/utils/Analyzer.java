package com.lengzhang.utils;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer {
  private ArrayList<Statement> statements;

  private ArrayList<Node> graph;

  public Analyzer(ArrayList<Statement> statements) {
    this.statements = statements;
    this.graph = new ArrayList<>();
  }

  public ArrayList<Node> getGraph() {
    return this.graph;
  }

  public Node analyse() {
    return analyse(0, this.statements.size() - 1, new Stack<Node>());
  }

  private Node analyse(int i, int j, Stack<Node> nodeStack) {
    Node root = createNewNode();
    nodeStack.push(root);

    int index = i;

    while (i <= index && index <= j) {
      Statement statement = this.statements.get(index);
      NodeType type = getStatementType(statement);

      Node currentNode = nodeStack.pop();

      /** IF Statement */
      if (type == NodeType.IF) {
        /** IF or ELSE IF or ELSE statement block */
        int pos;
        Stack<Node> result = new Stack<>();
        while (type == NodeType.IF || type == NodeType.ELSE_IF || type == NodeType.ELSE) {
          if (currentNode.getStatements().size() > 0) {
            Node tmp = createNewNode();
            currentNode.addChild(tmp);
            tmp.addParent(currentNode);
            currentNode = tmp;
          }
          currentNode.addStatement(statement);
          currentNode.type = type;

          while (!result.isEmpty()) {
            Node n = result.pop();
            n.addChild(currentNode);
            currentNode.addParent(n);
          }

          pos = findClosedParenthesis(index);
          result = new Stack<>();
          Node header = analyse(index + 1, pos - 1, result);

          currentNode.addChild(header);
          header.addParent(currentNode);
          index = pos;
          statement = this.statements.get(index);
          type = getStatementType(statement);
        }

        /** Create Close Parenthesis Node */
        Node closeParenthesisNode = createNewNode();
        closeParenthesisNode.addStatement(statement);
        currentNode.addChild(closeParenthesisNode);
        closeParenthesisNode.addParent(currentNode);
        while (!result.isEmpty()) {
          Node n = result.pop();
          n.addChild(closeParenthesisNode);
          closeParenthesisNode.addParent(n);
        }

        nodeStack.push(closeParenthesisNode);

      }
      /** WHILE Statement */
      else if (type == NodeType.WHILE) {
        if (currentNode.getStatements().size() > 0) {
          Node tmp = createNewNode();
          currentNode.addChild(tmp);
          tmp.addParent(currentNode);
          currentNode = tmp;
        }
        currentNode.addStatement(statement);
        currentNode.type = NodeType.WHILE;

        /** Get the code block inside while loop */
        int pos = findClosedParenthesis(index);
        Stack<Node> result = new Stack<>();
        Node header = analyse(index + 1, pos - 1, result);
        currentNode.addChild(header);
        header.addParent(currentNode);

        /** Create end node for close parenthesis */
        Node endNode = createNewNode();
        endNode.addStatement(this.statements.get(pos));
        endNode.addChild(currentNode);
        currentNode.addParent(endNode);
        while (!result.isEmpty()) {
          Node n = result.pop();
          n.addChild(endNode);
          endNode.addParent(n);
        }

        nodeStack.push(currentNode);
        currentNode.type = NodeType.WHILE_END;
        index = pos;
      }
      /** DO WHILE statement */
      else if (type == NodeType.DO_WHILE) {
        if (currentNode.getStatements().size() > 0) {
          Node tmp = createNewNode();
          currentNode.addChild(tmp);
          tmp.addParent(currentNode);
          currentNode = tmp;
        }
        currentNode.addStatement(statement);
        currentNode.type = NodeType.DO_WHILE;

        /** Get the code block inside do while loop */
        int pos = findClosedParenthesis(index);
        Stack<Node> result = new Stack<>();
        Node header = analyse(index + 1, pos - 1, result);
        currentNode.addChild(header);
        header.addParent(currentNode);

        /** Create end node for WHILE statement with close parenthesis */
        Node endNode = createNewNode();
        endNode.addStatement(this.statements.get(pos));
        endNode.type = NodeType.DO_WHILE_END;
        endNode.addChild(currentNode);
        currentNode.addParent(endNode);
        while (!result.isEmpty()) {
          Node n = result.pop();
          n.addChild(endNode);
          endNode.addParent(n);
        }

        nodeStack.push(endNode);
        index = pos;
      }
      /** FOR statement */
      else if (type == NodeType.FOR) {
        Pattern pattern = Pattern.compile("\\(([^;]*);([^;]*);([^;]*)\\)");
        Matcher matcher = pattern.matcher(statement.getStatement());

        if (matcher.find()) {
          Statement statement1 = new Statement("    " + matcher.group(1).trim(), statement.getLineNumber() + 'a');
          Statement statement2 = new Statement("    " + matcher.group(2).trim(), statement.getLineNumber() + 'b');
          Statement statement3 = new Statement("    " + matcher.group(3).trim(), statement.getLineNumber() + 'c');

          /** Statement 1 */
          if (currentNode.getStatements().size() > 0) {
            Node tmp = createNewNode();
            currentNode.addChild(tmp);
            tmp.addParent(currentNode);
            currentNode = tmp;
          }
          currentNode.addStatement(statement1);

          /** Condition Statement */
          Node conditionNode = createNewNode();
          conditionNode.type = NodeType.FOR;
          conditionNode.addStatement(statement2);
          conditionNode.addParent(currentNode);
          currentNode.addChild(conditionNode);

          /** Get the code block inside while loop */
          int pos = findClosedParenthesis(index);
          Stack<Node> result = new Stack<>();
          Node header = analyse(index + 1, pos - 1, result);
          conditionNode.addChild(header);
          header.addParent(conditionNode);

          /** Create end node for close parenthesis */
          Node endNode = createNewNode();
          endNode.type = NodeType.FOR_END;
          endNode.addStatement(this.statements.get(pos));
          endNode.addStatement(statement3);
          endNode.addChild(conditionNode);
          conditionNode.addParent(endNode);
          while (!result.isEmpty()) {
            Node n = result.pop();
            n.addChild(endNode);
            endNode.addParent(n);
          }

          nodeStack.push(conditionNode);
          index = pos;
        }
      }
      /** Others */
      else {
        if (currentNode.type != NodeType.NONE) {
          Node tmp = createNewNode();
          currentNode.addChild(tmp);
          tmp.addParent(currentNode);
          currentNode = tmp;
        }
        currentNode.addStatement(statement);
        nodeStack.push(currentNode);
      }

      index++;
    }

    return root;
  }

  private int findClosedParenthesis(int from) {
    int numberOfOpenParenthesis = 1;
    int index = from + 1;
    while (index < this.statements.size()) {
      Statement statement = this.statements.get(index);
      if (hasCloseParenthesis(statement.getStatement())) {
        numberOfOpenParenthesis--;
      }
      if (numberOfOpenParenthesis == 0) {
        break;
      }
      if (hasOpenParenthesis(statement.getStatement())) {
        numberOfOpenParenthesis++;
      }
      index++;
    }
    return index;
  }

  private Node createNewNode() {
    Node node = new Node(this.graph.size());
    this.graph.add(node);
    return node;
  }

  public void printGraph() {
    String vertices = "";
    ArrayList<String> edges = new ArrayList<>();
    String details = "";
    for (Node node : graph) {
      int id = node.getId();
      vertices += id + " ";
      details += node + "\n";

      ArrayList<Integer> children = new ArrayList<>(node.getChildren());
      String edgeStr = "";
      for (int i = 0; i < children.size(); i++) {
        if (i != 0) {
          edgeStr += ", ";
        }
        edgeStr += children.get(i);
      }

      if (!edgeStr.isBlank()) {
        edges.add(id + " ->\t" + edgeStr);
      }
    }

    String edgesStr = "";
    for (String edge : edges) {
      edgesStr += String.format("\t%s\n", edge);
    }

    String printStr = String.format("Vertices:\n\t%s\nEdges:\n%s\nDetails:\n%s", vertices, edgesStr, details);
    System.out.println(printStr);
  }

  private NodeType getStatementType(Statement statement) {
    return this.getStatementType(statement.getStatement());
  }

  private NodeType getStatementType(String statement) {
    if (isIfStatement(statement))
      return NodeType.IF;
    else if (isElseIfStatement(statement))
      return NodeType.ELSE_IF;
    else if (isElseStatement(statement))
      return NodeType.ELSE;
    else if (isDoStatement(statement))
      return NodeType.DO_WHILE;
    else if (isWhileStatement(statement))
      return NodeType.WHILE;
    else if (isForStatement(statement))
      return NodeType.FOR;
    else
      return NodeType.NONE;
  }

  private boolean findByRegex(String regex, String str) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(str);
    return matcher.find();
  }

  private boolean isIfStatement(String statement) {
    return findByRegex("^(\\s)*if", statement);
  }

  private boolean isElseIfStatement(String statement) {
    return findByRegex("^(\\s)*else(\\s)+if", statement);
  }

  private boolean isElseStatement(String statement) {
    return findByRegex("^(\\s)*\\}(\\s)*else", statement);
  }

  private boolean isDoStatement(String statement) {
    return findByRegex("^(\\s)*do", statement);
  }

  private boolean isWhileStatement(String statement) {
    return findByRegex("^(\\s)*while", statement);
  }

  private boolean isForStatement(String statement) {
    return findByRegex("^(\\s)*for", statement);
  }

  private boolean hasOpenParenthesis(String statement) {
    return findByRegex("\\{(\\s)*$", statement);
  }

  private boolean hasCloseParenthesis(String statement) {
    return findByRegex("^(\\s)*\\}", statement);
  }
}
