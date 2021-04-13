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
import org.apache.bcel.util.InstructionFinder;
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


public class Cleanup{
    HashMap <Integer, Number> storeDictionary = new HashMap();
    InstructionList instructionList;
    ConstantPoolGen constantPoolGen;

    public Cleanup(InstructionList instructionList, ConstantPoolGen constantPoolGen){
        this.instructionList = instructionList;
        this.constantPoolGen = constantPoolGen;
    }

    public InstructionList optimise(){
        for(InstructionHandle instructionHandle: instructionList.getInstructionHandles()){
        }
		return instructionList;
    }
}