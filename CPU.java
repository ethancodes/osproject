/**
 *  CPU. This puppy does everything!<P>
 *
 *  CPU has the following registers.<UL>
 *    IR (4 bytes) Instruction Register
 *    IC (2 bytes) Instruction Counter
 *    C  (1 byte)  Compare (0 or 1)
 *    R  (4 bytes) Register (for mischief)</UL><P>
 *
 *  @author Ethan Georgi
 *  @version 9.10.1998, 12.06.1998
 */

public class CPU
{
  String IR, R;
  int IC, C;

  Memory refToMemory;
  Drisk refToDrisk;
  PCB refToPCB;
  MOS refToMOS;

  int SI, PI, IOI, TI; //interrupt flags

  /**
   *  Constructor. Monkies.
   *
   *  @param m A reference to Memory
   *  @param d A reference to Drisk
   *  @param z A reference to the MOS
   *  @param k A reference to the Terminate Q
   *  @param pcb The Next PCB on ReadyQ
   */
  public CPU(Memory m, Drisk d, MOS z, PCB p)
  {
    IR = "0000"; R = "0000"; IC = 0; C = 0;
    refToMemory = m;
    refToDrisk = d;
    refToMOS = z;
    refToPCB = p;
    SI = 0; PI = 0; IOI = 0; TI = 0;
  }

  /**
   *  Gets the IR
   *
   *  @return the IR
   */
  public String getIR() { return IR; }

  /**
   *  Return the instruction counter
   *
   *  @return The IC
   */
  public int getIC() { return IC; }

  /**
   *  Return the fabulous register of golden chicken nuggets
   *
   *  @return R
   */
  public String getR() { return R; }

  /**
   *  Return the compare byte
   *
   *  @return C (1 = True, 2 = False
   */
  public int getC() { return C; }

  /**
   *  Fetch the next instruction from memory
   */
  public void fetch()
  {
    IR = refToMemory.readWord(refToPCB.virtualToPhysical(IC));
    incIC();
  }

  /**
   *  Increment the instruction counter
   */
  public void incIC() { IC++; }

  /**
   *  Set an arbitrary value for the IC
   *
   *  @param v The new value for the IC
   */
  public void setIC(int v) { IC = v; }

  /**
   *  What is the SI flag set to?
   *
   *  @return The value if the Supervisor Interrupt flag
   */
  public int getSI() { return SI; }

  /**
   *  Set the SI flag to a value.
   *
   *  @param v The value to set SI to.
   */
  public void setSI(int v)
  {
    SI = v;
  }

  /**
   *  What is the PI flag set to?
   *
   *  @return The value if the Program Interrupt flag
   */
  public int getPI() { return PI; }

  /**
   *  Set the PI flag to a value.
   *
   *  @param v The value to set PI to.
   */
  public void setPI(int v)
  {
    PI = v;
  }

  /**
   *  What is the IOI flag set to?
   *
   *  @return The value if the Input Output Interrupt flag
   */
  public int getIOI() { return IOI; }

  /**
   *  Set the IOI flag. Note that this ADDS the value you send to the
   *  flag. If you want to subtract from this flag, pass a negative number.<P>
   *  <LI>1 Channel 1 is done
   *  <LI>2 Channel 2 is done
   *  <LI>3 Channels 1 & 2 are done
   *  <LI>4 Channel 3 is done
   *  <LI>5 Channels 1 & 3 are done
   *  <LI>6 Channels 2 & 3 are done
   *
   *  @param v The value to add to IOI.
   */
  public void setIOI(int v) { IOI += v; }

  /**
   *  What is the TI flag set to?
   *
   *  @return The value if the Timer Interrupt flag
   */
  public int getTI() { return TI; }

  /**
   *  Add to the TI flag<P>
   *
   *  <LI>1 Time Slice up, switch to another process
   *  <LI>2 Time Limit up, kill it
   *  <LI>3 both of the above
   *
   *  @param v The value to add to the TI flag
   */
  public void setTI(int v) { TI += v; }

  /**
   *  Execute the instruction
   */
  public void execute()
  {
    String type = getInstructionType(IR);
    if (type.equals("H ")) { setSI(3); }
    else if (type.equals("LR"))
    {
      int op = getOperand(IR); LR(op);
      Trace.write("Operand " + Integer.toString(op) + " ");
      Trace.write("Real Address ");
      Trace.writeln(Integer.toString(refToPCB.virtualToPhysical(op)));
    }
    else if (type.equals("SR"))
    {
      int op = getOperand(IR); SR(op);
      Trace.write("Operand " + Integer.toString(op) + " ");
      Trace.write("Real Address ");
      Trace.writeln(Integer.toString(refToPCB.virtualToPhysical(op)));
    }
    else if (type.equals("CR"))
    {
      int op = getOperand(IR); CR(op);
      Trace.write("Operand " + Integer.toString(op) + " ");
      Trace.write("Real Address ");
      Trace.writeln(Integer.toString(refToPCB.virtualToPhysical(op)));
    }
    else if (type.equals("BE"))
    {
      int op = getOperand(IR); BE(op);
      Trace.write("Operand " + Integer.toString(op) + " ");
      Trace.write("Real Address ");
      Trace.writeln(Integer.toString(refToPCB.virtualToPhysical(op)));
    }
    else if (type.equals("GD"))
    {
      int op = getOperand(IR); setSI(1);
      Trace.write("Operand " + Integer.toString(op) + " ");
      Trace.write("Real Address ");
      Trace.writeln(Integer.toString(refToPCB.virtualToPhysical(op)));
    }
    else if (type.equals("PD"))
    {
      int op = getOperand(IR); setSI(2);
      Trace.write("Operand " + Integer.toString(op) + " ");
      Trace.write("Real Address ");
      Trace.writeln(Integer.toString(refToPCB.virtualToPhysical(op)));
    }
    else { setPI(1); }

    //increment all the clocks...
    if (refToPCB != null)
    {
      refToPCB.incClocks();
      refToPCB.storeRegisters(R, IR, C, IC);
    }
  } //end of execute

  /**
   *  Provided an instruction, returns the instruction
   *  type; the first two bytes as a String
   *
   *  @param i The instruction to muck with
   *  @return The instruction type
   */
  private String getInstructionType(String i)
  {
    return i.substring(0, 2);
  }

  /**
   *  When given an instruction, returns the operand
   *  as a number.
   *
   *  @param i The instruction
   *  @return The operand as an int
   */
  private int getOperand(String i)
  {
    try { return Integer.parseInt(i.substring(2, 4)); }
    catch (Exception ex)
    {
      refToPCB.storeRegisters(getR(), getIR(), getC(), getIC());
      setPI(2);
    }
    return 0;
  }

  /**
   *  LR: Load a virtual memory loc's contents into R
   *
   *  @param loc The location in virtual memory to fetch
   */
  public void LR(int loc)
  {
    if (refToPCB.pageTable[loc / 10] == -1)
    {
      if (refToMemory.isFull())
      {
        refToPCB.storeRegisters(getR(), getIR(), getC(), getIC());
        refToMOS.addToMemQ(refToPCB);
        refToPCB = null;
      }
      else
      {
        int malloc = refToMemory.allocate();
        int offset = loc % 10;
        refToPCB.pageTable[loc / 10] = malloc;
        R = refToMemory.readWord(refToPCB.virtualToPhysical(loc));
      }
    }
    else
    {
      R = refToMemory.readWord(refToPCB.virtualToPhysical(loc));
    }
  }

  /**
   *  SR: Store contents of R into virtual mem loc
   *
   *  @param loc The location in virtual memory to write to
   */
  public void SR(int loc)
  {
    if (refToPCB.pageTable[loc / 10] == -1)
    {
      if (refToMemory.isFull())
      {
        refToPCB.storeRegisters(getR(), getIR(), getC(), getIC());
        refToMOS.addToMemQ(refToPCB);
        refToPCB = null;
      }
      else
      {
        int malloc = refToMemory.allocate();
        int offset = loc % 10;
        refToMemory.writeWord((malloc * 10) + offset, R);
        refToPCB.pageTable[loc / 10] = malloc;
      }
    }
    else
    {
      refToMemory.writeWord(refToPCB.virtualToPhysical(loc), R);
    }
  }

  /**
   *  CR: Compare R to contents of virtual mem loc
   *
   *  @param loc the location to compare to
   */
  public void CR(int loc)
  {
    loc = refToPCB.virtualToPhysical(loc);
    if (R.equals(refToMemory.readWord(loc))) { C = 1; }
    else { C = 0; }
  }

  /**
   *  BE: Branch if equal to a new location in the program
   *
   *  @param loc the location to branch to
   */
  public void BE(int loc)
  {
    loc = refToPCB.virtualToPhysical(loc);
    if (C == 1) { setIC(loc); }
  }

  /**
   *  assign a new PCB
   *
   *  @param p The new PCB
   */
  public void setPCB(PCB p)
  {
    refToPCB = p;
    if (p != null)
    {
      R = refToPCB.getR(); IR = refToPCB.getIR();
      C = refToPCB.getC(); IC = refToPCB.getIC();
    }
  }

} //end of class CPU