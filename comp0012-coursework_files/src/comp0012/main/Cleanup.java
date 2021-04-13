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
    HashMap <Integer, InstructionHandle> referenceDictionary = new HashMap();
    InstructionList instructionList;
    ConstantPoolGen constantPoolGen;

    public Cleanup(InstructionList instructionList, ConstantPoolGen constantPoolGen){
        this.instructionList = instructionList;
        this.constantPoolGen = constantPoolGen;
    }

    public InstructionList optimise(){

        this.referenceDictionary = new HashMap();


        for(InstructionHandle instructionHandle: instructionList.getInstructionHandles()){
            Instruction currentInstruction = instructionHandle.getInstruction();

            if (currentInstruction instanceof StoreInstruction){
                // if the current instruction is store we know that the previous instruction handle must be result of some value
                // when we delete we can just delete the store instruction and the previous handle
                StoreInstruction storeInstruction = (StoreInstruction) currentInstruction;

                int variableIndex = storeInstruction.getIndex();

                if (this.referenceDictionary.get(variableIndex) != null){
                    
                    InstructionHandle instructionHandleInDict = this.referenceDictionary.get(variableIndex);

                    try {
                        instructionList.delete(instructionHandleInDict.getPrev());
                        instructionList.delete(instructionHandleInDict);
                    }catch(TargetLostException e){

                    }
                }else {
                    this.referenceDictionary.put(variableIndex, instructionHandle);
                }
            }


        }

        for (InstructionHandle leftOverHandle: this.referenceDictionary.values()){
              try {
                        System.out.println("DELETING");
                        System.out.println(leftOverHandle.getPrev());
                        instructionList.delete(leftOverHandle.getPrev());
                    }catch(TargetLostException e){

                    } 

            try {

                        System.out.println("DELETING Store");
                        System.out.println(leftOverHandle);

                        instructionList.delete(leftOverHandle);
            }catch (TargetLostException e){
                
            }

            instructionList.setPositions(true);
        }




		return instructionList;
    }
}