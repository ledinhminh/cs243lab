package optimize;

import java.util.List;
import joeq.Class.jq_Class;
import joeq.Main.Helper;
import flow.Flow.Analysis;
import flow.Flow.Solver;
import submit.MySolver;
import submit.NullCheckAnalysis;
import submit.BoundCheckAnalysis;

public class Optimize {
    /*
     * optimizeFiles is a list of names of class that should be optimized
     * if nullCheckOnly is true, disable all optimizations except "remove redundant NULL_CHECKs."
     */
    public static void optimize(List<String> optimizeFiles, boolean nullCheckOnly) {
    	Solver solver = new MySolver();
    	Analysis[] as = {
            new NullCheckAnalysis(), //Add extra credit analysis below
            new BoundCheckAnalysis()
        };
        for (int i = 0; i < optimizeFiles.size(); i++) {
            jq_Class classes = (jq_Class)Helper.load(optimizeFiles.get(i));
            // Run your optimization on each classes.
            for(Analysis a:as){
                if(nullCheckOnly && !(a instanceof NullCheckAnalysis)){
                    continue;
                }
                solver.registerAnalysis(a);
                Helper.runPass(classes, solver);
            }
        }
    }
}
