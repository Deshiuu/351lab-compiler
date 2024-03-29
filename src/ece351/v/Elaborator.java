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

package ece351.v;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import org.parboiled.common.ImmutableList;

import ece351.common.ast.AndExpr;
import ece351.common.ast.AssignmentStatement;
import ece351.common.ast.ConstantExpr;
import ece351.common.ast.EqualExpr;
import ece351.common.ast.Expr;
import ece351.common.ast.NAndExpr;
import ece351.common.ast.NOrExpr;
import ece351.common.ast.NaryAndExpr;
import ece351.common.ast.NaryOrExpr;
import ece351.common.ast.NotExpr;
import ece351.common.ast.OrExpr;
import ece351.common.ast.Statement;
import ece351.common.ast.VarExpr;
import ece351.common.ast.XNOrExpr;
import ece351.common.ast.XOrExpr;
import ece351.common.visitor.PostOrderExprVisitor;
import ece351.util.CommandLine;
import ece351.v.ast.Architecture;
import ece351.v.ast.Component;
import ece351.v.ast.DesignUnit;
import ece351.v.ast.IfElseStatement;
import ece351.v.ast.Process;
import ece351.v.ast.VProgram;

/**
 * Inlines logic in components to architecture body.
 */
public final class Elaborator extends PostOrderExprVisitor {

	private final Map<String, String> current_map = new LinkedHashMap<String, String>();
	
	public static void main(String[] args) {
		System.out.println(elaborate(args));
	}
	
	public static VProgram elaborate(final String[] args) {
		return elaborate(new CommandLine(args));
	}
	
	public static VProgram elaborate(final CommandLine c) {
        final VProgram program = DeSugarer.desugar(c);
        return elaborate(program);
	}
	
	public static VProgram elaborate(final VProgram program) {
		final Elaborator e = new Elaborator();
		return e.elaborateit(program);
	}

	private VProgram elaborateit(final VProgram root) {

		// our ASTs are immutable. so we cannot mutate root.
		// we need to construct a new AST that will be the return value.
		// it will be like the input (root), but different.
		VProgram result = new VProgram();
		int compCount = 0;
		int signalsCount = 0;
		for(DesignUnit units : root.designUnits){
		// iterate over all of the designUnits in root.
			Architecture cur_arch = units.arch;
			// for each one, construct a new architecture.
			// Architecture a = du.arch.varyComponents(ImmutableList.<Component>of());
			// this gives us a copy of the architecture with an empty list of components.
			// now we can build up this Architecture with new components.
			for(Component comp :units.arch.components){
			// In the elaborator, an architectures list of signals, and set of statements may change (grow)
				signalsCount = 0;
				compCount++;
				for(DesignUnit a : result.designUnits){
					//populate dictionary/map
					if(a.identifier.equals(comp.entityName)){
						for(String s: a.entity.input){
							current_map.put(s,comp.signalList.get(signalsCount));
							signalsCount++;
							//add input signals, map to ports
						}
						for(String s: a.entity.output){
							current_map.put(s,comp.signalList.get(signalsCount));
							signalsCount++;
							//add output signals, map to ports
						}
						for(String s: a.arch.signals){
							cur_arch = cur_arch.appendSignal("comp"+compCount+"_"+s);
							current_map.put(s,"comp"+compCount+"_"+s);
							//add local signals, add to signal list of current designUnit
						}
						for(Statement s: a.arch.statements){
							//loop through the statements in the architecture body
							if(s instanceof AssignmentStatement){
								cur_arch = cur_arch.appendStatement(changeStatementVars((AssignmentStatement)s));
								// make the appropriate variable substitutions for signal assignment statements
								// i.e., call changeStatementVars
							}
							else{
								cur_arch = cur_arch.appendStatement(expandProcessComponent((Process)s));
								// make the appropriate variable substitutions for processes (sensitivity list, if/else body statements)
								// i.e., call expandProcessComponent
							}
						}
					}
				}
			}
			result = result.append(units.setArchitecture(cur_arch.varyComponents(ImmutableList.<Component>of())));
			// append this new architecture to result
		}
// TODO: longer code snippet
		assert result.repOk();
		return result;
	}
	
	// you do not have to use these helper methods; we found them useful though
private Process expandProcessComponent(final Process process) {
		Process p = new Process();
		for(String s :process.sensitivityList){
			if(current_map.containsKey(s)){
				p = p.appendSensitivity(current_map.get(s));
			}
			else {
				p = p.appendSensitivity(s);
			}
		}
		for(Statement s :process.sequentialStatements){
			if(s instanceof AssignmentStatement){
				p = p.appendStatement(changeStatementVars((AssignmentStatement)s));
			}
			else{
				p = p.appendStatement(changeIfVars((IfElseStatement)s));
			}
		}
		return p;
}
// TODO: longer code snippet
//throw new ece351.util.Todo351Exception();
	// you do not have to use these helper methods; we found them useful though
private  IfElseStatement changeIfVars(final IfElseStatement s) {
		IfElseStatement s1 = new IfElseStatement(traverseExpr(s.condition));
		for(Statement if_s :s.ifBody){
			s1 = s1.appendToTrueBlock(changeStatementVars((AssignmentStatement)if_s));
		}
		for(Statement else_s :s.elseBody){
			s1 = s1.appendToElseBlock(changeStatementVars((AssignmentStatement)else_s));
		}
		return s1;
// TODO: longer code snippet
//throw new ece351.util.Todo351Exception();
	}

	// you do not have to use these helper methods; we found them useful though
	private AssignmentStatement changeStatementVars(final AssignmentStatement s){
		return new AssignmentStatement(current_map.get(s.outputVar.identifier), traverseExpr(s.expr));
// TODO: short code snippet
//throw new ece351.util.Todo351Exception();
	}
	
	
	@Override
	public Expr visitVar(VarExpr e) {
		// TODO replace/substitute the variable found in the map
		return new VarExpr(current_map.get(e.identifier));
// TODO: short code snippet
//throw new ece351.util.Todo351Exception();
	}
	
	// do not rewrite these parts of the AST
	@Override public Expr visitConstant(ConstantExpr e) { return e; }
	@Override public Expr visitNot(NotExpr e) { return e; }
	@Override public Expr visitAnd(AndExpr e) { return e; }
	@Override public Expr visitOr(OrExpr e) { return e; }
	@Override public Expr visitXOr(XOrExpr e) { return e; }
	@Override public Expr visitEqual(EqualExpr e) { return e; }
	@Override public Expr visitNAnd(NAndExpr e) { return e; }
	@Override public Expr visitNOr(NOrExpr e) { return e; }
	@Override public Expr visitXNOr(XNOrExpr e) { return e; }
	@Override public Expr visitNaryAnd(NaryAndExpr e) { return e; }
	@Override public Expr visitNaryOr(NaryOrExpr e) { return e; }
}
