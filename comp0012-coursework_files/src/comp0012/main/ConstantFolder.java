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
//import org.apache.bcel.generic.ClassGen;
//import org.apache.bcel.generic.LCONST;
//import org.apache.bcel.generic.RETURN;
//import org.apache.bcel.generic.ConstantPoolGen;
//import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.*;
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


	private Number getValueFromInstruction(InstructionHandle handle, ConstantPoolGen constantPoolGen){
			Instruction currentInstruction = handle.getInstruction();

			if (currentInstruction instanceof ConstantPushInstruction){
				return ((ConstantPushInstruction) currentInstruction).getValue();
			}

			if (currentInstruction instanceof LDC){
				return (Number) ((LDC) currentInstruction).getValue(constantPoolGen);
			}

			if (currentInstruction instanceof LDC2_W){
				return (Number) ((LDC2_W) currentInstruction).getValue(constantPoolGen);	
			}

			if (currentInstruction instanceof LoadInstruction){
				// int index = ((LoadInstruction) currentInstruction).getIndex();
				// System.out.println("this is the index");
				// System.out.println(index);
				// System.out.println(currentInstruction);
				// Constant test = constantPoolGen.getConstant(index);
				// System.out.println(test);
				// System.out.println(constantPoolGen.getConstant(index).getClass());
				// if (test instanceof ConstantLong){
				// 	System.out.println("BRUHKLJASKJSAKD");
				// 	ConstantLong output = ((ConstantLong) test);
				// 	System.out.println(output.getConstantValue(constantPoolGen.getConstantPool()));
				// 	// System.out.println(test.getConstantValue());
				// }


				// return constantPoolGen.getConstant(index);
			}


			return null;


	}



	public InstructionList simpleFolding(InstructionList instructionList, ConstantPoolGen constantPoolGen){
		for(InstructionHandle insturctionHandle: instructionList.getInstructionHandles()){
			Instruction currentInstruction = insturctionHandle.getInstruction();

			if (currentInstruction instanceof ArithmeticInstruction){

				// the previous two values in the stack must be a value to be able to do arithmetic operation
				InstructionHandle firstHandle = insturctionHandle.getPrev();
				InstructionHandle secondHandle = firstHandle.getPrev();
				
				// System.out.println("firsthandle");
				// System.out.println(firstHandle);
				// System.out.println(getValueFromInstruction(firstHandle, constantPoolGen));

				// System.out.println("secondhandle");
				// System.out.println(secondHandle);
				// System.out.println(getValueFromInstruction(secondHandle, constantPoolGen));



//				System.out.println(((LDC) firstHandle.getInstruction()).getValue());


				// if (firstHandle.getInstruction() instanceof ConstantPushInstruction){
//					System.out.println("test");
					// ConstantPushInstruction test = ((ConstantPushInstruction) firstHandle.getInstruction());
					// System.out.println(firstHandle);
					// System.out.println(test.getValue());
//					System.out.println(((ConstantPushInstruction) firstHandle.getInstruction()).getValue(constantPoolGen));
				// }


				// if (firstHandle.getInstruction() instanceof LDC){
				// 	LDC test = ((LDC) firstHandle.getInstruction());
				// 	System.out.println("bruh");
				// 	System.out.println(firstHandle.getInstruction());
				// 	System.out.println(test.getValue(constantPoolGen));
				// }



//				System.out.println(((LDC) firstHandle.getInstruction()).getValue());
//				 System.out.println(((LDC) firstHandle.getInstruction()));
//				 System.out.println(secondHandle);
			}



			if (currentInstruction instanceof StoreInstruction){
				System.out.println(currentInstruction);
				StoreInstruction test = ((StoreInstruction) currentInstruction);
				System.out.println(test.getIndex());
			}

		}
		return instructionList;
	}




	private void optimiseMethod(ClassGen cgen, Method currentMethod, ConstantPoolGen constPoolGen){
		MethodGen methodGen = new MethodGen(currentMethod, cgen.getClassName(), constPoolGen);
		Code currentCode = currentMethod.getCode();
		InstructionList test = new InstructionList(currentCode.getCode());

		methodGen.setInstructionList(simpleFolding(test, constPoolGen));
		cgen.replaceMethod(currentMethod, methodGen.getMethod());

	}


	
	public void optimize()
	{
		ClassGen cgen = new ClassGen(original);

		ConstantPoolGen constPoolGen = cgen.getConstantPool();
		Method[] methodList = cgen.getMethods();
		for (int i = 0; i< methodList.length; i++) {

			optimiseMethod(cgen, methodList[i], constPoolGen);

			// MethodGen methodGen = new MethodGen(methodList[i], cgen.getClassName(), constPoolGen);
			// System.out.println(cgen.getClassName() + " --------- " + methodList[i].getName());

			// InstructionList test = new InstructionList(methodList[i].getCode().getCode());

			// methodGen.setInstructionList(test);

			// cgen.replaceMethod(methodList[i], methodGen.getMethod());
			
		}
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