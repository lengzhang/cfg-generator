package utils;

import java.util.ArrayList;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Analyzer {
  private ArrayList<Statement> statements;

  private ArrayList<Node> graph;

  public Analyzer(ArrayList<Statement> statements) {
    this.statements = statements;
    this.initialize();
  }

  public ArrayList<Node> getGraph() {
    return this.graph;
  }

  public void analyse() {
    this.initialize();

    analyse(0, this.statements.size() - 1, new Stack<>());

  }

  private Node analyse(int i, int j, Stack<Node> nodeStack) {
    return analyse(i, j, nodeStack, createNewNode());
  }

  private Node analyse(int i, int j, Stack<Node> nodeStack, Node head) {
    System.out.println("++++++++++ analyse ++++++++++");
    System.out.println("i=" + i + "; j=" + j);
    nodeStack.push(head);

    int index = i;

    while (i <= index && index <= j) {
      Statement statement = this.statements.get(index);
      NodeType type = getStatementType(statement);

      Node currentNode = nodeStack.pop();

      /** IF Statement */
      if (type == NodeType.IF) {

        if (currentNode.type != NodeType.NONE) {
          Node tmp = createNewNode();
          currentNode.addChild(tmp);
          tmp.addParent(currentNode);
          currentNode = tmp;
        }

        currentNode.addStatement(statement);
        currentNode.type = NodeType.IF;

        /** IF or ELSE IF or ELSE statement block */
        Node blockHeader = currentNode;
        int pos;
        Stack<Node> result = new Stack<>();
        while (type == NodeType.IF || type == NodeType.ELSE_IF || type == NodeType.ELSE) {
          if (type == NodeType.ELSE_IF || type == NodeType.ELSE) {
            Node tmp = createNewNode();
            tmp.addStatement(statement);
            tmp.type = type;

            while (!result.isEmpty()) {
              Node n = result.pop();
              n.addChild(tmp);
              tmp.addParent(n);
            }

            blockHeader.addChild(tmp);
            tmp.addParent(blockHeader);
            blockHeader = tmp;
          }
          pos = findClosedParenthesis(index);
          result = new Stack<>();
          Node subHeader = analyse(index + 1, pos - 1, result);

          blockHeader.addChild(subHeader);
          subHeader.addParent(blockHeader);
          index = pos;
          statement = this.statements.get(index);
          type = getStatementType(statement);
        }

        /** Create Close Parenthesis Node */
        Node closeParenthesisNode = createNewNode();
        closeParenthesisNode.addStatement(statement);
        blockHeader.addChild(closeParenthesisNode);
        closeParenthesisNode.addParent(blockHeader);
        while (!result.isEmpty()) {
          Node n = result.pop();
          n.addChild(closeParenthesisNode);
          closeParenthesisNode.addParent(n);
        }

        nodeStack.push(closeParenthesisNode);

      }
      /** WHILE Statement */
      else if (type == NodeType.WHILE) {
        if (currentNode.type != NodeType.NONE
            || (currentNode.type == NodeType.NONE && currentNode.getStatements().size() > 0)) {
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
        Node subHeader = analyse(index + 1, pos - 1, result, currentNode);
        if (currentNode.getId() != subHeader.getId()) {
          currentNode.addChild(subHeader);
          subHeader.addParent(currentNode);
        }

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

        if (currentNode.type != NodeType.NONE
            || (currentNode.type == NodeType.NONE && currentNode.getStatements().size() > 0)) {
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
        Node subHeader = analyse(index + 1, pos - 1, result, currentNode);
        if (currentNode.getId() != subHeader.getId()) {
          currentNode.addChild(subHeader);
          subHeader.addParent(currentNode);
        }

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
      /** Others */
      else {

        if (currentNode.type != NodeType.NONE && currentNode.type != NodeType.DO_WHILE
            && currentNode.type != NodeType.WHILE) {
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

    return head;
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

  private void initialize() {
    this.graph = new ArrayList<>();
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
