/* *********************************************************************
 * ECE351 
 * Department of Electrical and Computer Engineering 
 * University of Waterloo 
 * Term: Winter 2019 (1191)
 *
 * The base version of this file is the intellectual property of the
 * University of Waterloo. Redistribution is prohibited.
 *
 * By pushing changes to this file I affirm that I am the author of
 * all changes. I affirm that I have complied with the course
 * collaboration policy and have not plagiarized my work. 
 *
 * I understand that redistributing this file might expose me to
 * disciplinary action under UW Policy 71. I understand that Policy 71
 * allows for retroactive modification of my final grade in a course.
 * For example, if I post my solutions to these labs on GitHub after I
 * finish ECE351, and a future student plagiarizes them, then I too
 * could be found guilty of plagiarism. Consequently, my final grade
 * in ECE351 could be retroactively lowered. This might require that I
 * repeat ECE351, which in turn might delay my graduation.
 *
 * https://uwaterloo.ca/secretariat-general-counsel/policies-procedures-guidelines/policy-71
 * 
 * ********************************************************************/

package ece351.common.ast;

import ece351.util.Examinable;
import ece351.util.Examiner;

public abstract class CommutativeBinaryExpr extends BinaryExpr {

	public CommutativeBinaryExpr(final Expr left, final Expr right) {
		super(left, right);
	}

	/**
	 * Allow for arguments to be commuted (swapped).
	 * So either left=left + right=right OR
	 * left=right + right=left.
	 */
	@Override
	public final boolean isomorphic(final Examinable obj) {
		return examine(Examiner.Isomorphic, obj);
	}

	private boolean examine(final Examiner e, final Examinable obj) {
		// basics
		if (obj == null) return false;
		if (!this.getClass().equals(obj.getClass())) return false;
		final CommutativeBinaryExpr cbe = (CommutativeBinaryExpr) obj;
		
		// compare field values, both ways, using e.examine(x,y)
		boolean exa1 = e.examine(cbe.left, this.left) && e.examine(cbe.right, this.right);
		boolean exa2 = e.examine(cbe.left, this.right) && e.examine(cbe.right, this.left);
		return exa1 || exa2;
// TODO: short code snippet
//throw new ece351.util.Todo351Exception();
	}


}
