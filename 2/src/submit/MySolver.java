package submit;

import joeq.Compiler.Quad.*;
import flow.Flow;
import flow.Flow.DataflowObject;

import java.util.*;
/**
 * Skeleton class for implementing the Flow.Solver interface.
 */
public class MySolver implements Flow.Solver {

    protected Flow.Analysis analysis;
    /**
     * Sets the analysis.  When visitCFG is called, it will
     * perform this analysis on a given CFG.
     *
     * @param analyzer The analysis to run
     */
    public void registerAnalysis(Flow.Analysis analyzer) {
        this.analysis = analyzer;
    }

    /**
     * Runs the solver over a given control flow graph.  Prior
     * to calling this, an analysis must be registered using
     * registerAnalysis
     *
     * @param cfg The control flow graph to analyze.
     */
    public void visitCFG(ControlFlowGraph cfg) {

        // this needs to come first.
        analysis.preprocess(cfg);
        boolean changed = true;
        boolean buildTerminals=true;
        Set<Quad> terminals=new HashSet<Quad>();
        while (changed) {
        	changed = false;
        	QuadIterator qit = new QuadIterator(cfg);
        	while (qit.hasNext()) {
        		Quad q = qit.next();
        		Iterator<Quad> meets;
	        	if (analysis.isForward()){
	        		meets = qit.predecessors();
	        	} else {
	        		meets = qit.successors();       		
	        	}
	        	DataflowObject d_obj = null;
	            while(meets.hasNext()){
	            		Quad q1 = meets.next();
	            		if (d_obj == null) {
	            			d_obj = analysis.newTempVar();
	            			if (q1 != null) {
		            			if (analysis.isForward()) {
		            				d_obj.copy(analysis.getOut(q1));
                                    //System.out.print(q1.getID()+" ");
		            			} else {
		            				d_obj.copy(analysis.getIn(q1));
		            			}
		            		} else{
		            			if (analysis.isForward()) {
		            				d_obj.copy(analysis.getEntry());
		            			} else {
		            				d_obj.copy(analysis.getExit());
		            			}
	                        }
	            		} else {
		            		if (q1 != null) {
		            			if (analysis.isForward()) {
		            				d_obj.meetWith(analysis.getOut(q1));
                                    //System.out.print(q1.getID()+" ");
		            			} else {
		            				d_obj.meetWith(analysis.getIn(q1));
		            			}
		            		} else{
		            			if (analysis.isForward()) {
		            				d_obj.meetWith(analysis.getEntry());
		            			} else {
		            				d_obj.meetWith(analysis.getExit());
		            			}
	                        }
	            		}
	        	}
                //System.out.println();
                if(buildTerminals){
	        	    if (analysis.isForward()){
	        		    meets = qit.successors();       		
	        	    } else {
	        		    meets = qit.predecessors();
	        	    }
                    while(meets.hasNext()){
	            		Quad qt = meets.next();
	            		if (qt == null) {
                            terminals.add(q);
                        }
                    }
                }
	            DataflowObject old = analysis.newTempVar();
	            if (analysis.isForward()) {
	                old.copy(analysis.getOut(q));
	                analysis.setIn(q, d_obj);
	            } else {
	                old.copy(analysis.getIn(q));
	                analysis.setOut(q, d_obj);
	            }	               
	            analysis.processQuad(q);
                    DataflowObject terminal;
	        	    if (analysis.isForward()){
	        		    terminal=analysis.getExit();
	        	    } else {
	        		    terminal=analysis.getEntry();
	        	    }
                for(Quad qt:terminals){
	        	    if (analysis.isForward()){
	        		    terminal.meetWith(analysis.getOut(qt));       		
	        	    } else {
	        		    terminal.meetWith(analysis.getIn(qt));       		
	        	    }
                }   
	        	if (analysis.isForward()){
	        		    analysis.setExit(terminal);
	        	} else {
	        		    analysis.setEntry(terminal);
	        	}
	            DataflowObject res = analysis.newTempVar();
	            if (analysis.isForward()) {
	             	res.copy(analysis.getOut(q));
	            } else {
	                res.copy(analysis.getIn(q));    	
	            }
	            if (!changed && !old.equals(res)) {
	                changed = true;
	            }
        	}
                    buildTerminals=false;
        	
        }

        // this needs to come last.
        analysis.postprocess(cfg);
    }
}
