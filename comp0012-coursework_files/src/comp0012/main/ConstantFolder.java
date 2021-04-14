package comp0012.main;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;

import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.generic.LoadInstruction;
//import org.apache.bcel.generic.ClassGen;
//import org.apache.bcel.generic.LCONST;
//import org.apache.bcel.generic.RETURN;
//import org.apache.bcel.generic.ConstantPoolGen;
//import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.*;
import org.apache.bcel.util.InstructionFinder;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;
import java.util.HashMap;



public class ConstantFolder
{
	ClassParser parser = null;
	ClassGen gen = null;

	JavaClass original = null;
	JavaClass optimized = null;

	HashMap <Integer, Number> storeDictionary = new HashMap();

	public ConstantFolder(String classFilePath)
	{
		try{
			this.parser = new ClassParser(classFilePath);
			this.original = this.parser.parse();
			this.gen = new ClassGen(this.original);
		} catch(IOException e){
			e.printStackTrace();
		}
	}


	private void optimiseMethod(ClassGen cgen, Method currentMethod, ConstantPoolGen constPoolGen){
		MethodGen methodGen = new MethodGen(currentMethod, cgen.getClassName(), constPoolGen);
		Code currentCode = currentMethod.getCode();
		InstructionList test = methodGen.getInstructionList(); 
		// simpleFolding(test, constPoolGen);
		new Optimiser(test, constPoolGen).optimise();
		new Cleanup(test, constPoolGen).optimise();

		cgen.replaceMethod(currentMethod, methodGen.getMethod());
	}


	
	public void optimize()
	{
		ClassGen cgen = new ClassGen(original);

		ConstantPoolGen constPoolGen = cgen.getConstantPool();



		Method[] methodList = cgen.getMethods();
		System.out.println(cgen.getClassName());
		for (int i = 0; i< methodList.length; i++) {
			System.out.println(methodList[i].getName());
			optimiseMethod(cgen, methodList[i], constPoolGen);
			// reset the constant dictionary

			System.out.println();
			System.out.println();
			System.out.println();

			// MethodGen methodGen = new MethodGen(methodList[i], cgen.getClassName(), constPoolGen);
			// System.out.println(cgen.getClassName() + " --------- " + methodList[i].getName());

			// InstructionList test = new InstructionList(methodList[i].getCode().getCode());

			// methodGen.setInstructionList(test);

			// cgen.replaceMethod(methodList[i], methodGen.getMethod());
			
		}
		cgen.setMajor(50);
		this.optimized = cgen.getJavaClass();

		// Implement your optimization here
        
		//this.optimized = gen.getJavaClass();
	}

	
	public void write(String optimisedFilePath)
	{
		this.optimize();

		try {
			FileOutputStream out = new FileOutputStream(new File(optimisedFilePath));
			this.optimized.dump(out);
		} catch (FileNotFoundException e) {
			// Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// Auto-generated catch block
			e.printStackTrace();
		}
	}
}