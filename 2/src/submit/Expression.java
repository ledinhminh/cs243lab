package submit;

import joeq.Compiler.Quad.Operand;
import joeq.Compiler.Quad.Operator;
import joeq.Compiler.Quad.Quad;

public class Expression {
	private Operator opr;
	private Operand[] operands = new Operand[3];

	public Expression(Quad q) {
		this.opr = q.getOperator();
		operands[0] = q.getAllOperands().getOperand(0);
		operands[1] = q.getAllOperands().getOperand(1);
		operands[2] = q.getAllOperands().getOperand(2);

	}
	public String toString(){
		return (opr.toString() + " " + operands[0].toString()
				+ " " + operands[1].toString() + " " + operands[2].toString());
    }
	
	@Override
	public int hashCode() {
		return (opr.toString() + operands[0].toString()
				+ operands[1].toString() + operands[2].toString()).hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null) return false;
		if (!(o instanceof Expression)) return false;
        Expression p = (Expression) o;
        return ((opr.toString().equals(p.opr.toString())) &&
        		(operands[0].toString().equals(p.operands[0].toString())) && 
        		(operands[1].toString().equals(p.operands[1].toString())) && 
        		(operands[2].toString().equals(p.operands[2].toString())));

	}

	public boolean contains(String operand) {
		return (operands[0].toString().equals(operand) || 
        		operands[1].toString().equals(operand) || 
        		operands[2].toString().equals(operand));
	}
}
