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




    private void deleteInstruction(InstructionList instructionList, InstructionHandle deleteHandle, InstructionHandle replacementHandle){

        InstructionHandle toReplace = null;

        if (replacementHandle != null){
            toReplace = replacementHandle;
        }


        try {
            instructionList.delete(deleteHandle);
        }catch(TargetLostException e){
            for (InstructionHandle target : e.getTargets()) {
                for (InstructionTargeter targeter : target.getTargeters()) {
                    targeter.updateTarget(target, toReplace);
                }
            }
        }

        instructionList.setPositions(true);
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
                    InstructionHandle replacement = instructionHandleInDict.getNext();

                    deleteInstruction(instructionList, instructionHandleInDict.getPrev(), replacement);
                    deleteInstruction(instructionList, instructionHandleInDict, replacement);

                    // try {
                    //     instructionList.delete(instructionHandleInDict.getPrev());
                        
                    // }catch(TargetLostException e){

                    // }


                    // try {
                    //     instructionList.delete(instructionHandleInDict);
                    // }catch(TargetLostException e){

                    // }

                    instructionList.setPositions(true);
                }
                
                    this.referenceDictionary.put(variableIndex, instructionHandle);
                
            }
            
            // if there is an iinc mean there's a loop so remove the thing from the hashmap without the instructionList
            if (currentInstruction instanceof IINC){
                int variableIndex = ((IINC) currentInstruction).getIndex();
                this.referenceDictionary.remove(variableIndex);
            }


        }

        for (InstructionHandle leftOverHandle: this.referenceDictionary.values()){

            InstructionHandle replacement = leftOverHandle.getNext();

            deleteInstruction(instructionList, leftOverHandle.getPrev(), replacement);
            deleteInstruction(instructionList, leftOverHandle, replacement);
              
            //   try {
            //             System.out.println("DELETING");
            //             System.out.println(leftOverHandle.getPrev());
            //             if (leftOverHandle.getPrev() != null){
            //                  instructionList.delete(leftOverHandle.getPrev());
            //             }  
            //         }catch(TargetLostException e){

            //         } 

            // try {

            //             System.out.println("DELETING Store");
            //             System.out.println(leftOverHandle);

            //             instructionList.delete(leftOverHandle);
            // }catch (TargetLostException e){
                
            // }

            // instructionList.setPositions(true);
        }




		return instructionList;
    }
}