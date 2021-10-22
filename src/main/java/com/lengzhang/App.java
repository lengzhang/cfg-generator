package com.lengzhang;

import java.util.ArrayList;

import com.lengzhang.utils.Analyzer;
import com.lengzhang.utils.CodeReader;
import com.lengzhang.utils.MethodFinder;
import com.lengzhang.utils.Statement;

public class App {
    public static void main(String[] args) throws Exception {
        /** Get source code */
        CodeReader codeReader = new CodeReader();
        ArrayList<Statement> lines = codeReader.read("test-code/Test.java");

        /** Get methods */
        MethodFinder methodFinder = new MethodFinder();
        ArrayList<ArrayList<Statement>> methods = methodFinder.findMethods(lines);

        /** Analyse Methods */
        Analyzer analyzer = new Analyzer(methods.get(0));
        analyzer.analyse();

        System.out.println("+++++++++++++");
        System.out.println(" Graph ");
        System.out.println("+++++++++++++");

        analyzer.printGraph();
    }
}
