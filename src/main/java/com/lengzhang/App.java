package com.lengzhang;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import com.lengzhang.utils.Analyzer;
import com.lengzhang.utils.CodeReader;
import com.lengzhang.utils.MethodFinder;
import com.lengzhang.utils.Statement;

import guru.nidi.graphviz.attribute.Label;
import guru.nidi.graphviz.attribute.Shape;
import guru.nidi.graphviz.attribute.Label.Justification;
import guru.nidi.graphviz.engine.Format;
import guru.nidi.graphviz.engine.Graphviz;
import guru.nidi.graphviz.model.MutableGraph;
import guru.nidi.graphviz.model.MutableNode;

import static guru.nidi.graphviz.model.Factory.*;

public class App {
    public static void main(String[] args) throws Exception {
        String filePath = args.length > 0 ? args[0] : "test-code/Test.java";
        /** Get source code */
        CodeReader codeReader = new CodeReader();
        ArrayList<Statement> lines = codeReader.read(filePath);

        /** Get methods */
        MethodFinder methodFinder = new MethodFinder();
        ArrayList<ArrayList<Statement>> methods = methodFinder.findMethods(lines);

        /** Analyse Methods */
        MutableGraph g = mutGraph(filePath).setDirected(true).setCluster(true);
        g.add(mutNode("start").add(Shape.NONE).add(Label.markdown("**" + filePath + "**")));
        for (int i = 0; i < methods.size(); i++) {
            ArrayList<Statement> method = methods.get(i);
            Analyzer analyzer = new Analyzer(method);
            analyzer.analyse();

            // System.out.println("+++++++++++++");
            // System.out.println(" Graph ");
            // System.out.println("+++++++++++++");

            // analyzer.printGraph();

            HashMap<String, MutableNode> nodeMap = new HashMap<>();
            /** Create each node */
            for (com.lengzhang.utils.Node n : analyzer.getGraph()) {
                String id = String.format("%d-%d", i, n.getId());
                String statementStr = "";
                for (Statement statement : n.getStatements()) {
                    statementStr += String.format("%s:%s\n", statement.getLineNumber(), statement.getStatement());
                }
                Label label = Label.lines(Justification.LEFT, "Node: " + n.getId(), statementStr);

                MutableNode node = mutNode(id).add(Shape.BOX);
                node.add(label);

                nodeMap.put(id, node);
            }

            /** Link each node to children */
            for (com.lengzhang.utils.Node n : analyzer.getGraph()) {
                String id = String.format("%d-%d", i, n.getId());
                MutableNode source = nodeMap.get(id);
                for (int childId : n.getChildren()) {
                    MutableNode target = nodeMap.get(String.format("%d-%d", i, childId));
                    source.addLink(target);
                }
            }

            /** Add each node to the graph */
            for (String key : nodeMap.keySet()) {
                MutableNode node = nodeMap.get(key);
                g.add(node);
            }
        }

        Graphviz.fromGraph(g).render(Format.PNG).toFile(new File("output/result.png"));
    }
}
