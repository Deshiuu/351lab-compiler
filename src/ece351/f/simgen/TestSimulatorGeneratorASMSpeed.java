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

package ece351.f.simgen;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import ece351.util.TestInputs351;

public class TestSimulatorGeneratorASMSpeed {

	@Test
	public void test() throws IOException {
		final Collection<Object[]> c = TestInputs351.formulaFiles("z_challenge");
		final File f = (File) c.iterator().next()[0];
		final TestSimulatorGeneratorASM t = new TestSimulatorGeneratorASM(f, 1000);
		t.simgen();
	}

}
