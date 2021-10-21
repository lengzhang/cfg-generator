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
    // System.out.println("++++++++++ analyse ++++++++++");
    // System.out.println("i=" + i + "; j=" + j);
    Node head = createNewNode();
    nodeStack.push(head);

    int index = i;

    while (i <= index && index <= j) {
      // System.out.println("++++++++++ nodeStack ++++++++++");
      // nodeStack.forEach((n) -> {
      // n.print();
      // });
      // System.out.println("+++++++++++++++++++++++++++++++++++++++++");

      Statement statement = this.statements.get(index);
      NodeType type = getStatementType(statement);

      Node currentNode = nodeStack.pop();

      if (type == NodeType.IF) {
        if (currentNode.type != NodeType.NONE) {
          nodeStack.push(currentNode);
          Node tmp = createNewNode();
          tmp.addParent(currentNode);
          currentNode.addChild(tmp);
          currentNode = tmp;
        }
        // else if (currentNode.isClosed) {
        // nodeStack.push(currentNode);
        // Node tmp = createNewNode();
        // tmp.addParent(currentNode);
        // currentNode.addChild(tmp);
        // while (!nodeStack.isEmpty()) {
        // currentNode = nodeStack.pop();
        // currentNode.addChild(tmp);
        // tmp.addParent(currentNode);
        // }
        // currentNode = tmp;
        // }
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
          // for (int a = index + 1; a <= pos - 1; a++) {
          // System.out.println("++" + this.statements.get(a));
          // }
          result = new Stack<>();
          Node subHeader = analyse(index + 1, pos - 1, result);

          blockHeader.addChild(subHeader);
          subHeader.addParent(blockHeader);
          index = pos;
          statement = this.statements.get(index);
          type = getStatementType(statement);
        }

        // System.out.println("++" + statement);

        Node closeParenthesisNode = createNewNode();
        closeParenthesisNode.addStatement(statement);
        // closeParenthesisNode.isClosed = true;
        blockHeader.addChild(closeParenthesisNode);
        closeParenthesisNode.addParent(blockHeader);
        while (!result.isEmpty()) {
          Node n = result.pop();
          n.addChild(closeParenthesisNode);
          closeParenthesisNode.addParent(n);
        }

        nodeStack.push(closeParenthesisNode);

      } else {
        if (currentNode.type != NodeType.NONE) {
          nodeStack.push(currentNode);
          Node tmp = createNewNode();
          currentNode.addChild(tmp);
          tmp.addParent(currentNode);
          currentNode = tmp;
        }
        // else if (currentNode.isClosed) {
        // nodeStack.push(currentNode);
        // Node tmp = createNewNode();
        // currentNode.addChild(tmp);
        // tmp.addParent(currentNode);
        // while (!nodeStack.isEmpty()) {
        // currentNode = nodeStack.pop();
        // currentNode.addChild(tmp);
        // tmp.addParent(currentNode);
        // }
        // currentNode = tmp;
        // }
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
