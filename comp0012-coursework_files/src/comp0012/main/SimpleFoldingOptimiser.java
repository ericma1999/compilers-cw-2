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


public class SimpleFoldingOptimiser{
    HashMap <Integer, Number> storeDictionary = new HashMap();
    InstructionList instructionList;
    ConstantPoolGen constantPoolGen;

    public SimpleFoldingOptimiser(InstructionList instructionList, ConstantPoolGen constantPoolGen){
        this.instructionList = instructionList;
        this.constantPoolGen = constantPoolGen;
    }

    public InstructionList optimise(){
        for(InstructionHandle instructionHandle: instructionList.getInstructionHandles()){
			Instruction currentInstruction = instructionHandle.getInstruction();
			InstructionHandle nextInstructionHandle = instructionHandle.getNext();
			Instruction nextInstruction = null;
			boolean isArithmetic = false;

			Number arithmeticResult = null;

			// if (currentInstruction instanceof LCMP){

			// 	InstructionHandle firstHandle = instructionHandle.getPrev();
			// 	InstructionHandle secondHandle = instructionHandle.getPrev().getPrev();


			// 	Number firstValue = getValueFromInstruction(firstHandle, constantPoolGen);
			// 	Number secondValue = getValueFromInstruction(secondHandle, constantPoolGen);

			// 	int valueToPush;
			// 	if ((Long) firstValue > (Long) secondValue) {
			// 		valueToPush = 1;
			// 	}else if ((Long) firstValue > (Long) secondValue) {
			// 		valueToPush = -1;
			// 	} else {
			// 		valueToPush = 0;
			// 	}	

			// 	instructionHandle.setInstruction(new LDC(constantPoolGen.addInteger(valueToPush)));

			// 	try{
			// 		instructionList.delete(firstHandle);
			// 		instructionList.delete(secondHandle);
			// 	}catch(Exception e){

			// 	}




			// }


			if (currentInstruction instanceof IfInstruction){
				// IfInstruction test = (IfInstruction) instructionHandle.getInstruction();
				// System.out.println(test.getName());
				System.out.println(getOperationType(instructionHandle, constantPoolGen));

				performComparator(instructionHandle, constantPoolGen);
			}





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

	private boolean performLessEqualComparison(Number firstValue, Number secondValue){
		return firstValue.longValue() <= secondValue.longValue();
	}

	private boolean performGreaterEqualComparison(Number firstValue, Number secondValue){
		return firstValue.longValue() >= secondValue.longValue();
	}

	private void foldIfInstruction(boolean result, InstructionList instructionList, InstructionHandle currentHandle, ConstantPoolGen constantPoolGen){

		try{
			instructionList.delete(currentHandle.getPrev().getPrev());
			instructionList.delete(currentHandle.getPrev());
			instructionList.delete(currentHandle);
		}catch(Exception e){

		}




		if (result){
			
		}
	}

	private void performComparator(InstructionHandle currentHandle, ConstantPoolGen constantPoolGen){
			InstructionHandle firstHandle = currentHandle.getPrev();
			InstructionHandle secondHandle = currentHandle.getPrev().getPrev();	

			Number firstValue = getValueFromInstruction(firstHandle, constantPoolGen);
			Number secondValue = getValueFromInstruction(secondHandle, constantPoolGen);

			String comparatorType = getOperationType(currentHandle, constantPoolGen);

			boolean result = false;
			boolean calculated = false;

			switch(comparatorType){
				case "less_equal":
					result = performLessEqualComparison(firstValue, secondValue);
					calculated = true;
					break;
				case "less_equal_zero":
					result = performLessEqualComparison(firstValue, 0);
					calculated = true;
					break;
				case "greater_equal":
					result = performGreaterEqualComparison(firstValue, secondValue);
					calculated = true;
					break;
				case "greater_equal_zero":
					result =  performGreaterEqualComparison(firstValue, 0);
					calculated = true;
					break;
			}


			if (calculated){
				foldIfInstruction(result, instructionList, currentHandle, constantPoolGen);
			}

	}

	private String getComparatortype(String type){
		switch(type){
			case "if_icmple":
				return "less_equal";
			case "ifle":
				return "less_equal_zero";
			case "if_icmpge":
				return "greater_equal";
			case "if_ge":
				return "greater_equal_zero";
			case "ifeq":
				return "equal_zero";
			case "ifnull":
				return "equal_null";
			case "ifnonnull":
				return "not_null";
			default:
				return null;
		}
	}

	private String getOperationType(InstructionHandle instructionHandle, ConstantPoolGen constantPoolGen){

		Instruction instruction = instructionHandle.getInstruction();

		if (instruction instanceof ArithmeticInstruction){
			return ((ArithmeticInstruction) instruction).getType(constantPoolGen).toString();
		}

		if (instruction instanceof IfInstruction){
			
			return getComparatortype(instruction.getName());
		}

		return null;





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





}