package submit;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

import joeq.Compiler.Quad.*;
import joeq.Compiler.Quad.Operand.RegisterOperand;
import flow.Flow;
/**
 * Skeleton class for implementing a faint variable analysis
 * using the Flow.Analysis interface.
 */
public class CommonExpressionAnalysis implements Flow.Analysis {

    /**
     * Class for the dataflow objects in the Faintness analysis.
     * You are free to change this class or move it to another file.
     */
	
    public static class MyDataflowObject implements Flow.DataflowObject {
        private Set<Expression> set;
        public static Set<Expression> universalSet;

        public boolean contains(Expression e) {
        	return set.contains(e);
        	
        }
        
        public MyDataflowObject() {
            set = new HashSet<Expression>();
        }

        /**
         * Methods from the Flow.DataflowObject interface. See Flow.java for the
         * meaning of these methods. These need to be filled in.
         */
        public void setToBottom() {
            set = new HashSet<Expression>();
        }

        
        public void setToTop() {
            set = new HashSet<Expression>(MyDataflowObject.universalSet);
        }

        public void meetWith(Flow.DataflowObject o) {

            MyDataflowObject a = (MyDataflowObject) o;
            set.retainAll(a.set);
        }

        public void copy(Flow.DataflowObject o) {
            MyDataflowObject a = (MyDataflowObject) o;
            set = new HashSet<Expression>(a.set);
        }

        @Override
        public boolean equals(Object o) 
        {
            if (o instanceof MyDataflowObject) 
            {
                MyDataflowObject a = (MyDataflowObject) o;
                return set.equals(a.set);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return set.hashCode();
        }

        /**
         * toString() method for the dataflow objects which is used by
         * postprocess() below. The format of this method must be of the form
         * "[ID0, ID1, ID2, ...]", where each ID is the identifier of a quad
         * defining some register, and the list of IDs must be sorted. See
         * src/test/test.rd.out for example output of the analysis. The output
         * format of your reaching definitions analysis must match this exactly.
         */
        @Override
        public String toString() {
            return set.toString();
        }
        
        public void genVar(Quad v) {set.add(new Expression(v));}
        public void killVar(String operand) {
        	Iterator<Expression> itr = set.iterator();
        	while (itr.hasNext()){
        		Expression e = itr.next();
        		if (e.contains(operand)) {
        			itr.remove();
        		}
        	}
        }
    }

    /**
     * Dataflow objects for the interior and entry/exit points
     * of the CFG. in[ID] and out[ID] store the entry and exit
     * state for the input and output of the quad with identifier ID.
     *
     * You are free to modify these fields, just make sure to
     * preserve the data printed by postprocess(), which relies on these.
     */
    private MyDataflowObject[] in, out;
    private MyDataflowObject entry, exit;
    public ArrayList<String> methodName=new ArrayList<String>();
    public ArrayList<ArrayList<Integer>> removedQuads=
        new ArrayList<ArrayList<Integer>>();
    
    /**
     * This method initializes the datflow framework.
     *
     * @param cfg  The control flow graph we are going to process.
     */
    public void preprocess(ControlFlowGraph cfg) {
        // this line must come first.

        // get the amount of space we need to allocate for the in/out arrays.
        QuadIterator qit = new QuadIterator(cfg);
        int max = 0;
        while (qit.hasNext()) {
            int id = qit.next().getID();
            if (id > max) 
                max = id;
        }
        max += 1;

        // allocate the in and out arrays.
        in = new MyDataflowObject[max];
        out = new MyDataflowObject[max];
        transferfn.val = new MyDataflowObject();
        // initialize the contents of in and out.
        qit = new QuadIterator(cfg);
        while (qit.hasNext()) {
            int id = qit.next().getID();
            in[id] = new MyDataflowObject();
            out[id] = new MyDataflowObject();
        }

        // initialize the entry and exit points.
        entry = new MyDataflowObject();
        exit = new MyDataflowObject();

        /************************************************
         * Your remaining initialization code goes here *
         ************************************************/
        MyDataflowObject.universalSet = new HashSet<Expression>();
        
        
        qit = new QuadIterator(cfg);
        while (qit.hasNext()) {
          Quad q = qit.next();
          if (q.getOperator() instanceof Operator.Binary){
        	  MyDataflowObject.universalSet.add(new Expression(q));
          }
        }
        qit = new QuadIterator(cfg);
        while (qit.hasNext()) {
            int id = qit.next().getID();
            out[id].setToTop();
        }

    }

    /**
     * This method is called after the fixpoint is reached.
     * It must print out the dataflow objects associated with
     * the entry, exit, and all interior points of the CFG.
     * Unless you modify in, out, entry, or exit you shouldn't
     * need to change this method.
     *
     * @param cfg  Unused.
     */
    public void postprocess (ControlFlowGraph cfg) {
        QuadIterator qit = new QuadIterator(cfg);
        while (qit.hasNext()) {
            Quad q = qit.next();
            if (q.getOperator() instanceof Operator.Binary){
   
        			if (((MyDataflowObject) this.getIn(q)).contains(new Expression(q)))
        			{
                       // System.out.println(q);
                        qit.remove();
        				
        			}
        				
        		
        	} 
        }
        
    }

    /**
     * Other methods from the Flow.Analysis interface.
     * See Flow.java for the meaning of these methods.
     * These need to be filled in.
     */
    /**
     * Other methods from the Flow.Analysis interface. See Flow.java for the
     * meaning of these methods. These need to be filled in.
     */
    public boolean isForward() {
        return true;
    }

    public Flow.DataflowObject getEntry() {
        Flow.DataflowObject result = newTempVar();
        result.copy(entry);
        return result;
    }

    public Flow.DataflowObject getExit() {
        Flow.DataflowObject result = newTempVar();
        result.copy(exit);
        return result;
    }

    public void setEntry(Flow.DataflowObject value) {
        entry.copy(value);
    }

    public void setExit(Flow.DataflowObject value) {
        exit.copy(value);
    }

    public Flow.DataflowObject getIn(Quad q) {
        Flow.DataflowObject result = newTempVar();
        result.copy(in[q.getID()]);
        return result;
    }

    public Flow.DataflowObject getOut(Quad q) {
        Flow.DataflowObject result = newTempVar();
        result.copy(out[q.getID()]);
        return result;
    }

    public void setIn(Quad q, Flow.DataflowObject value) {
        in[q.getID()].copy(value);
    }

    public void setOut(Quad q, Flow.DataflowObject value) {
        out[q.getID()].copy(value);
    }

    public Flow.DataflowObject newTempVar() {
        return new MyDataflowObject();
    }


    public void processQuad(Quad q) {
    	transferfn.val.copy(in[q.getID()]);
        transferfn.visitQuad(q);
        out[q.getID()].copy(transferfn.val);
        //System.out.println(transferfn.val);
    }
    private TransferFunction transferfn = new TransferFunction ();
    public static class TransferFunction extends QuadVisitor.EmptyVisitor {
    	MyDataflowObject val;

        @Override
        public void visitQuad(Quad q) {
        	
        		
        	
        		for (RegisterOperand def : q.getDefinedRegisters()) {
        			val.killVar(def.toString());
        		}
        		
        		if (q.getOperator() instanceof Operator.Binary) {
        			if (!q.getAllOperands().getOperand(1).toString().equals(q.getAllOperands().getOperand(0).toString()) &&
        					!q.getAllOperands().getOperand(2).toString().equals(q.getAllOperands().getOperand(0).toString()))
        			{
        				val.genVar(q);
        			}
        		}
        		
        	}
            
        
    }
}
