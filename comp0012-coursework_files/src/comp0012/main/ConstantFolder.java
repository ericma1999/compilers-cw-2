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
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.RETURN;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.util.InstructionFinder;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.TargetLostException;



public class ConstantFolder
{
	ClassParser parser = null;
	ClassGen gen = null;

	JavaClass original = null;
	JavaClass optimized = null;

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
	
	public void optimize()
	{
		ClassGen cgen = new ClassGen(original);

		ConstantPoolGen constPoolGen = cgen.getConstantPool();
		Method[] methods = cgen.getMethods();
		for (Method method : methods) {
			MethodGen methodGen = new MethodGen(method, cgen.getClassName(), constPoolGen);
			System.out.println(cgen.getClassName() + " --------- " + method.getName());

			InstructionList test = new InstructionList(method.getCode().getCode());
			test.insert(new RETURN());

			test.insert(new RETURN());

			// methodGen.setInstructionList(test);

			// System.out.println(methodGen.getInstructionList());

			methodGen.setInstructionList(new InstructionList());

			method = methodGen.getMethod();
			
		}
		cgen.setMethods(methods);
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