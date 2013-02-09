package optimize;

import submit.MySolver;
import submit.NullCheckAnalysis;
import flow.Flow.Analysis;
import flow.Flow.Solver;
import joeq.Class.jq_Class;
import joeq.Main.Helper;

public class FindRedundantNullChecks {

    /*
     * args is an array of class names
     * method should print out a list of quad ids of redundant null checks
     * for each function as described on the course webpage
     */
    public static void main(String[] args) {
    	Solver solver = new MySolver();
    	Analysis analysis = new NullCheckAnalysis();
    	// get the classes we will be visiting.
        jq_Class[] classes = new jq_Class[args.length];
        for (int i=0; i < classes.length; i++)
            classes[i] = (jq_Class)Helper.load(args[i]);

        // register the analysis with the solver.
        solver.registerAnalysis(analysis);

        // visit each of the specified classes with the solver.
        for (int i=0; i < classes.length; i++) {
            System.out.println("Now analyzing " + classes[i].getName());
            Helper.runPass(classes[i], solver);
        }
    }
}
