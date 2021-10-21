import java.util.ArrayList;

import utils.Analyzer;
import utils.CodeReader;
import utils.MethodFinder;
import utils.Statement;

public class App {
    public static void main(String[] args) throws Exception {
        /** Get source code */
        CodeReader codeReader = new CodeReader();
        ArrayList<Statement> lines = codeReader.read("test-code/Test.java");

        /** Get methods */
        MethodFinder methodFinder = new MethodFinder();
        ArrayList<ArrayList<Statement>> methods = methodFinder.findMethods(lines);

        /** Analyse Methods */
        Analyzer analyzer = new Analyzer(methods.get(1));
        analyzer.analyse();

        System.out.println("+++++++++++++");
        System.out.println(" Graph ");
        System.out.println("+++++++++++++");

        analyzer.printGraph();
    }
}
