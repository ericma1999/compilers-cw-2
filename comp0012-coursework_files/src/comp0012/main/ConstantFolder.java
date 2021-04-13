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

				int index = ((LoadInstruction) currentInstruction).getIndex();
				
				Constant test = constantPoolGen.getConstant(index);
				return this.storeDictionary.get(index);
				// System.out.println(constantPoolGen.getConstant(index));

			}

			return null;
	}

	// private String getArithmeticType

	private String getOperationType(InstructionHandle instructionHandle, ConstantPoolGen constantPoolGen){
		return ((ArithmeticInstruction) instructionHandle.getInstruction()).getType(constantPoolGen).toString();
	}

	private String getArithmeticOperationType(InstructionHandle instructionHandle){
		Instruction instruction = instructionHandle.getInstruction();

		if (instruction instanceof IADD || instruction instanceof LADD || instruction instanceof DADD || instruction instanceof FADD){
			return "add";
		}

		if (instruction instanceof IMUL || instruction instanceof LMUL || instruction instanceof DMUL || instruction instanceof FMUL){
			return "mul";
		}

		if (instruction instanceof IDIV || instruction instanceof LDIV || instruction instanceof DDIV || instruction instanceof FDIV){
			return "div";
		}

		if (instruction instanceof ISUB || instruction instanceof LSUB || instruction instanceof DSUB || instruction instanceof FSUB){
			return "sub";
		}

		return null;

	}

	private Number performAdditionAction(Number firstValue, Number secondValue, String operationType, InstructionList instructionList, InstructionHandle currentHandle, ConstantPoolGen constantPoolGen){
		Number result = null;
		switch (operationType){
			case "int":
				result = firstValue.intValue() + secondValue.intValue();
				instructionList.append(currentHandle, new LDC(constantPoolGen.addInteger((int) result)));
				break;
			case "long":
				result = firstValue.longValue() + secondValue.longValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addLong((long) result)));
				break;
			case "double":
				result = firstValue.doubleValue() + secondValue.doubleValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addDouble((double) result)));
				break;
			case "float":
				result = firstValue.floatValue() + secondValue.floatValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addFloat((float) result)));
				break;
		}
		return result;
	}

	private Number performDivisionAction(Number firstValue, Number secondValue, String operationType, InstructionList instructionList, InstructionHandle currentHandle, ConstantPoolGen constantPoolGen){
		Number result = null;
		switch (operationType){
			case "int":
				result = firstValue.intValue() / secondValue.intValue();
				instructionList.append(currentHandle, new LDC(constantPoolGen.addInteger((int) result)));
				break;
			case "long":
				result = firstValue.longValue() / secondValue.longValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addLong((long) result)));
				break;
			case "double":
				result = firstValue.doubleValue() / secondValue.doubleValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addDouble((double) result)));
				break;
			case "float":
				result = firstValue.floatValue() / secondValue.floatValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addFloat((float) result)));
				break;
		}
		return result;
	}

	private Number performMultiplicationAction(Number firstValue, Number secondValue, String operationType, InstructionList instructionList, InstructionHandle currentHandle, ConstantPoolGen constantPoolGen){
		Number result = null;
		switch (operationType){
			case "int":
				result = firstValue.intValue() * secondValue.intValue();
				instructionList.append(currentHandle, new LDC(constantPoolGen.addInteger((int) result)));
				break;
			case "long":
				result = firstValue.longValue() * secondValue.longValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addLong((long) result)));
				break;
			case "double":
				result = firstValue.doubleValue() * secondValue.doubleValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addDouble((double) result)));
				break;
			case "float":
				result = firstValue.floatValue() * secondValue.floatValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addFloat((float) result)));
				break;
		}
		return result;
	}

	private Number performSubtractionAction(Number firstValue, Number secondValue, String operationType, InstructionList instructionList, InstructionHandle currentHandle, ConstantPoolGen constantPoolGen){
		Number result = null;
		switch (operationType){
			case "int":
				result = firstValue.intValue() - secondValue.intValue();
				instructionList.append(currentHandle, new LDC(constantPoolGen.addInteger((int) result)));
				break;
			case "long":
				result = firstValue.longValue() - secondValue.longValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addLong((long) result)));
				break;
			case "double":
				result = firstValue.doubleValue() - secondValue.doubleValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addDouble((double) result)));
				break;
			case "float":
				result = firstValue.floatValue() - secondValue.floatValue();
				instructionList.append(currentHandle, new LDC2_W(constantPoolGen.addFloat((float) result)));
				break;
		}
		return result;
	}

	/* Remove the instruction for first and second value as well as the arithmetic insturction */
	private void removeOperands(InstructionHandle currentHandle, InstructionList instructionList){
		
		try{
			instructionList.delete(currentHandle.getPrev().getPrev());
			instructionList.delete(currentHandle.getPrev());
			instructionList.delete(currentHandle);
		}catch(TargetLostException e){

		}
	}

	private Number calculateArithmetic(InstructionHandle currentHandle, ConstantPoolGen constantPoolGen, InstructionList instructionList){
			InstructionHandle firstHandle = currentHandle.getPrev().getPrev();
			InstructionHandle secondHandle = currentHandle.getPrev();
			Number firstValue = getValueFromInstruction(firstHandle, constantPoolGen);
			Number secondValue = getValueFromInstruction(secondHandle, constantPoolGen);
			if (firstValue == null || secondValue == null){
				return null;
			}

			String operationType = getOperationType(currentHandle, constantPoolGen);

			String arithmeticOperationType = getArithmeticOperationType(currentHandle);

			Number arithmeticResult = null;

			switch(arithmeticOperationType){
				case "add":
					arithmeticResult = performAdditionAction(firstValue, secondValue, operationType, instructionList, currentHandle, constantPoolGen);
					break;
				case "sub":
					arithmeticResult = performSubtractionAction(firstValue, secondValue, operationType, instructionList, currentHandle, constantPoolGen);
					break;
				case "mul":
					arithmeticResult = performMultiplicationAction(firstValue, secondValue, operationType, instructionList, currentHandle, constantPoolGen);
					break;
				case "div":
					arithmeticResult = performDivisionAction(firstValue, secondValue, operationType, instructionList, currentHandle, constantPoolGen);
					break;
			}

			removeOperands(currentHandle, instructionList);
			return arithmeticResult;
	}

	public InstructionList simpleFolding(InstructionList instructionList, ConstantPoolGen constantPoolGen){
		for(InstructionHandle instructionHandle: instructionList.getInstructionHandles()){
			Instruction currentInstruction = instructionHandle.getInstruction();
			InstructionHandle nextInstructionHandle = instructionHandle.getNext();
			Instruction nextInstruction = null;
			boolean isArithmetic = false;

			Number arithmeticResult = null;

			if (currentInstruction instanceof ConversionInstruction){
				// skip conversion step since we can conver the value to our desired type
				try{
					instructionList.delete(currentInstruction);
				}catch (Exception e){

				}
				continue;
			}

			if (nextInstructionHandle != null){
				nextInstruction = nextInstructionHandle.getInstruction();
			}


			if (currentInstruction instanceof ArithmeticInstruction){

				// set this to true to load the result value into hashmap
				isArithmetic = true;

				// the previous two values in the stack must be a value to be able to do arithmetic operation
				// InstructionHandle firstHandle = instructionHandle.getPrev();
				// InstructionHandle secondHandle = firstHandle.getPrev();

				arithmeticResult = calculateArithmetic(instructionHandle, constantPoolGen, instructionList);
				System.out.println(arithmeticResult);
				
			}

			if (nextInstruction != null && nextInstruction instanceof StoreInstruction){
				int variablePosition = ((StoreInstruction) nextInstruction).getIndex();
				if (isArithmetic && arithmeticResult != null){
					Number value = arithmeticResult;
					this.storeDictionary.put(variablePosition, value);
				}else{
					Number value = getValueFromInstruction(instructionHandle, constantPoolGen);
					this.storeDictionary.put(variablePosition, value);
				}

			}

		}
		return instructionList;
	}

	private void optimiseMethod(ClassGen cgen, Method currentMethod, ConstantPoolGen constPoolGen){
		MethodGen methodGen = new MethodGen(currentMethod, cgen.getClassName(), constPoolGen);
		Code currentCode = currentMethod.getCode();
		InstructionList test = methodGen.getInstructionList(); 
		// simpleFolding(test, constPoolGen);
		new SimpleFoldingOptimiser(test, constPoolGen).optimise();
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
			this.storeDictionary = new HashMap();

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