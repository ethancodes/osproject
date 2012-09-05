/**
 *  Simulates Channel3, which does so much stuff you won't
 *  believe it. It does Input Spooling, Output Spooling,
 *  Loading, handles GD and PD instructions, and
 *  contributes to the downfall of the Furbies.
 *
 *  @author Ethan Georgi & Ken Schulz
 *  @version 11.30.1998, 12.06.1998
 */
public class Channel3
{
  private static int timer;
  private static boolean busy;
  private static int taskflag;
  private static int loaded;

  protected int cardNumber;
  protected boolean dtaCards;

  protected String[] block, loadBlock, gdpdBlock;
  protected String outputFullBuffer;
  protected String inputFullBuffer;
  protected PCB inSpoolPCB;
  protected PCB outSpoolPCB;
  protected PCB ch3pcb;
  protected Queue Channel3Q;
  protected Queue LoadQ;

  protected Drisk refToDrisk;
  protected Memory refToMemory;
  protected MOS refToMOS;
  protected CPU refToCPU;
  protected BufferPool refToBufPool;
  protected Queue refToReadyQ;
  protected Queue refToTerminateQ;
  protected boolean doneeoj1, doneeoj2, doneeoj3, doneeoj4, doneeoj5;

/**
 *  Constructor.
 *
 *  @param d reference to the drisk
 *  @param m ref to memory
 *  @param z ref to mos
 *  @param c ref to cpu
 *  @param b ref to buffy (the vampire slayer) pool
 *  @param q ref to the ReadyQ
 *  @param t ref to the TerminateQ (DIE! Die you BASTARDS!)
 */
   public Channel3(Drisk d, Memory m, MOS z, CPU c, BufferPool b,
                   Queue q, Queue t)
   {
      refToDrisk = d; refToMemory = m; refToMOS = z; refToCPU = c;
      refToBufPool = b; refToReadyQ = q; refToTerminateQ = t;
      timer = 0;
      busy = false;
      taskflag = 0;
      loaded = 0;
      cardNumber = 0;
      dtaCards = false;
      outputFullBuffer = new String();
      inputFullBuffer = new String();
      Channel3Q = new Queue();
      LoadQ = new Queue();
      block = new String[10];       // This guarantees that 1 line = 10 words
      loadBlock = new String[10];
      gdpdBlock = new String[10];
      inSpoolPCB = null;
      outSpoolPCB = null;
      ch3pcb = null;
      doneeoj1 = false;
      doneeoj2 = false;
      doneeoj3 = false;
      doneeoj4 = false;
      doneeoj5 = false;
   }

  /**
   *  What's the timer?
   */
  public int getTimer() { return timer; }

  /**
   *  Loading. Loads the program cards into memory. one. at. a. time.
   */
  public void load()
  {
    if (!refToMemory.isFull())
    {
      ch3pcb = (PCB)LoadQ.dequeue();
      int pageOnDrisk = ch3pcb.getNextProgramCard();
      if (pageOnDrisk != -1)
      {
        int w = refToMemory.allocate();
        loadBlock = refToDrisk.readBlock(pageOnDrisk);
        int i;
        Trace.write(ch3pcb.getJobID() + ": ");
        for (i = 0; i < 10; i++) { Trace.write(loadBlock[i]); }
        Trace.writeln(" assigned");
        refToMemory.writeBlock(w, loadBlock);
        ch3pcb.pageTable[loaded] = w;
        loaded++;
      }
      else //we are done loading this process
      {
        loaded = 0;
      }
    } //end if space in memory
  }

  /**
   *  Simulates the GD and PD instructions.
   */
  public void doGDPD()
  {
    ch3pcb = (PCB)Channel3Q.dequeue();
    int operand = Integer.parseInt(ch3pcb.getIR().substring(2, 4));
    if (ch3pcb.getIR().substring(0, 2).equals("GD"))
    {
      int pageOnDrisk = ch3pcb.getNextDataCard();
      if (pageOnDrisk != -1) {
      gdpdBlock = new String[10];
      int malloc;
      int where = ch3pcb.virtualToPhysical(operand);
      if ((where == -1) && (refToMemory.isFull()))
      {
        refToMOS.addToMemQ(ch3pcb);
        ch3pcb = null;
      }
      else
      {
        if (where == -1)
        {
          malloc = refToMemory.allocate();
        }
        else
        {
          malloc = where / 10;
        }
        gdpdBlock = refToDrisk.readBlock(pageOnDrisk);
        refToMemory.writeBlock(malloc, gdpdBlock);
        ch3pcb.pageTable[operand / 10] = malloc;
      }
      } //end if pageOnDrisk != -1
    } //done GD
    else if (ch3pcb.getIR().substring(0, 2).equals("PD"))
    {
      int where = ch3pcb.virtualToPhysical(operand);
      gdpdBlock = refToMemory.readBlock(where / 10);
      int malloc = refToDrisk.allocate();
      refToDrisk.writeBlock(malloc, gdpdBlock);
      ch3pcb.putOutput(malloc);
    } //done PD
    Trace.write(ch3pcb.getJobID() + ": ");
    for (int i = 0; i < 10; i++) { Trace.write(gdpdBlock[i]); }
    Trace.writeln(" assigned");
  }

  /**
   *  Is channel 3 doing anything right now?
   *
   *  @return true if channel3 is busy, false otherwise
   */
  public boolean isBusy() { return busy; }

  /**
   *  Increment the clocks.
   */
  public void incClock()
  {
    timer++;
    if (done())
    {
      refToCPU.setIOI(4);
      busy = false;
      timer = 0;
    }
  }

  /**
   *  Are we done yet?
   */
  public boolean done() { return (timer == 2); }

  /**
   *  Add the PCB to the Channel3 queue, for GD or PD commands.
   *
   *  @param p The PCB to enqueue
   */
  public void addToCh3Q(PCB p) { Channel3Q.enqueue(p); }

  /**
   *  Add the PCB to the Load queue, for loading...
   *
   *  @param p The PCB to enqueue
   */
  public void addToLoadQ(PCB p) { LoadQ.enqueue(p); }

  /**
   *  What the $%@& is Channel3 doing?<P>
   *
   *  <LI>0 nothing
   *  <LI>1 output spooling
   *  <LI>2 GD or PD
   *  <LI>3 loading
   *  <LI>4 input spooling
   *
   *  @return The task flag
   */
  public int getTaskFlag() { return taskflag; }

  /**
   *  Assign channel 3 a new task, dammit.<P>
   *
   *  PRIORITY: Output Spooling, GD/PD, Loading, Input Spooling.
   */
  public void setBusy()
  {
    if (!refToBufPool.isEMEmpty() && !refToTerminateQ.isEmpty())
    {
      Trace.writeln("Channel3 is assigned output spooling");
      taskflag = 1; timer = 0; busy = true; outputSpooling();
    }
    else if (!Channel3Q.isEmpty())
    {
      Trace.writeln("Channel3 is assigned execution GD/PD");
      taskflag = 2; timer = 0; busy = true; doGDPD();
    }
    else if (!LoadQ.isEmpty())
    {
      Trace.writeln("Channel3 is assigned loading");
      taskflag = 3; timer = 0; busy = true; load();
    }
    else if (!refToBufPool.isINEmpty() && !refToDrisk.isFull())
    {
      Trace.writeln("Channel3 is assigned input spooling");
      taskflag = 4; timer = 0; busy = true; inputSpooling();
    }
    else { taskflag = 0; }
  }

/**
 * Input Spooling
 * Checks inputFullBuffer for data cards
 * Creates a new PCB if $JOB is called
 * Uses inSpoolPCB to build a PCB
 */
   public void inputSpooling()
   {
      inputFullBuffer = refToBufPool.getIN();
      cardNumber++;  // Increment Card Number

      if (inputFullBuffer.charAt(0) == '$')
      {
         if (inSpoolPCB != null) { Trace.write(inSpoolPCB.getJobID() + ": "); }
         else { Trace.write("Channel3: "); }
         Trace.writeln(inputFullBuffer + " assigned");
         if (inputFullBuffer.substring(1, 4).equals("JOB"))
         {
            Trace.writeln("Control Card - JOB. Create a new PCB, "
                        + "set dataCards flag false");
               // $JOB Called before $EOJ
            try
            {
               int timeLimit =
                   Integer.parseInt(inputFullBuffer.substring(8, 12));
               int printLimit =
                   Integer.parseInt(inputFullBuffer.substring(12, 16));
               inSpoolPCB = new PCB(inputFullBuffer.substring(4, 8),
                                timeLimit, printLimit, refToMOS, refToCPU);
            }
            catch (Exception except)
            {
               Trace.writeln("ERROR: JOB Limit Exception! - "
                           + "Card Number " + cardNumber);
            }
            dtaCards = false;
         }  // END $JOB

         else if (inputFullBuffer.substring(1, 4).equals("REM"))
         {
            Trace.writeln("Control Card - REM. Remark");
               // $REM Called out of $JOB bounds
            if (inSpoolPCB == null) {
               Trace.writeln("WARNING: $REM Called out of bounds - "
                           + "Card Number " + cardNumber);
            }
            else
            {  // Write comment to PCB
               inSpoolPCB.addRemark(inputFullBuffer.substring(4));
            }
         }

         else if (inputFullBuffer.substring(1, 4).equals("DTA"))
         {
            Trace.writeln("Control Card - DTA. Set dataCards flag true");
               // $DTA Called out of $JOB bounds
            if (inSpoolPCB == null) {
               Trace.writeln("ERROR: $DTA Called out of bounds - "
                           + "Card Number " + cardNumber);
            }
            else
            {  // Set dtaCards to True
               dtaCards = true;
            }
         }
         else if (inputFullBuffer.substring(1, 4).equals("EOJ")
               || inputFullBuffer.substring(1, 4).equals("END"))
         {
            Trace.writeln("Control Card - EOJ. Send PCB to LoadQ");
               // $EOJ Called out of $JOB bounds
            if (inSpoolPCB == null) {
               Trace.writeln("ERROR: $EOJ Called out of bounds - "
                           + "Card Number " + cardNumber);
            }
            else
            {  // Send pcb to loadQueue
               LoadQ.enqueue(inSpoolPCB);
               dtaCards = false;
            }
         }
         else  // Not a valid $ command
         {
            Trace.writeln("ERROR: $" + inputFullBuffer.substring(1, 4) + " "
                        + "Not recognized - "
                        + "Card Number " + cardNumber);

         }
      }  // END if (bufferBlock.charAt(0) == '$')
      else if (inSpoolPCB != null)
      {  // Handle data cards
         for (int i = 0, j = 0; i < inputFullBuffer.length(); i+=4, j++)
         {
            int x = 4;
            if ((inputFullBuffer.length() - i) < 4)
            {
               x = (inputFullBuffer.length() - i);
            }
            block[j] = inputFullBuffer.substring(i, i + x);
         }

            // Load Card onto Drisk
         int blockNumber = refToDrisk.allocate();
         refToDrisk.writeBlock(blockNumber, block);
         if (dtaCards) { inSpoolPCB.dataCardLoadedAt(blockNumber); }
         else  { inSpoolPCB.programCardLoadedAt(blockNumber); }
      }  // END else if (inSpoolPCB != null)
      else
      {
         Trace.writeln("ERROR: Data Card out of bounds - "
                     + "Card Number " + cardNumber);
      }
   }  // END of InputSpooling

/**
 * Handles the final termination of a process. Sends Message data to
 * an outputFullBuffer to be printed
 * says is supposed to be printed, and puts it in the bufferPool as an
 * outputFullBufer.
 */
   public void outputSpooling()
   {
         // Make sure enough empty buffers exist and there is a process to kill
      if (!refToBufPool.isEMEmpty())
      {
         outSpoolPCB = (PCB)refToTerminateQ.dequeue();

         Trace.write(outSpoolPCB.getJobID() + ": ");
         Trace.write(outputFullBuffer);
         Trace.writeln(" assigned");

         if (!doneeoj1)
         {  // Free Memory of process if outputspooling hasnt started yet
            for (int i = 0; i < 10; i++)
            {
               if (outSpoolPCB.pageTable[i] > -1)
               {
                  refToMemory.deallocate(outSpoolPCB.pageTable[i]);
               }
            }
            //deallocate program cards
            for (int i = 0; i < outSpoolPCB.getNumProgramCards(); i++)
            {
              int kill = outSpoolPCB.getProgramCard(i);
              refToDrisk.deallocate(kill);
            }
            //deallocate data cards
            for (int i = 0; i < outSpoolPCB.getNumDataCards(); i++)
            {
              int kill = outSpoolPCB.getDataCard(i);
              refToDrisk.deallocate(kill);
            }
         }  // END of freeing up memory

            // Print an empty line
         if (!doneeoj1)
         {
            outputFullBuffer = refToBufPool.getEM();
            outputFullBuffer = " ";
            doneeoj1 = true;
            refToTerminateQ.push(outSpoolPCB);
         }
            // Print an empty line
         else if (!doneeoj2)
         {
            outputFullBuffer = refToBufPool.getEM();
            outputFullBuffer = " ";
            doneeoj2 = true;
            refToTerminateQ.push(outSpoolPCB);
         }
            // First ID Line
         else if (!doneeoj3)
         {
            outputFullBuffer = refToBufPool.getEM();
            outputFullBuffer = "JOB_ID# " + outSpoolPCB.getJobID() + ": "
                              + outSpoolPCB.eojMessage[0];
            doneeoj3 = true;
            refToTerminateQ.push(outSpoolPCB);
         }
            // Second Line of termination information
         else if (!doneeoj4)
         {
            outputFullBuffer = refToBufPool.getEM();
            outputFullBuffer = "IC" + outSpoolPCB.getIC()
                            + " IR" + outSpoolPCB.getIR()
                            + " R" + outSpoolPCB.getR()
                            + " C" + outSpoolPCB.getC()
                            + " TT" + outSpoolPCB.getTimer()
                            + " LL" + outSpoolPCB.getNumSpooled();
            doneeoj4 = true;
            refToTerminateQ.push(outSpoolPCB);
         }
            // Print an empty line
         else if (!doneeoj5)
         {
            outputFullBuffer = refToBufPool.getEM();
            outputFullBuffer = " ";
            doneeoj5 = true;
            refToTerminateQ.push(outSpoolPCB);
         }
         else  // Ready to print output
         {
            outputFullBuffer = refToBufPool.getEM();  // Get an empty buffer

            int outputAddress = outSpoolPCB.getNextSpooled();
            if (outputAddress >= 0)
            {
               block = refToDrisk.readBlock(outputAddress);
               for (int i = 0; i < 10; i++)
               {
                 outputFullBuffer = outputFullBuffer + block[i];
               }
               refToDrisk.deallocate(outputAddress);
               refToTerminateQ.push(outSpoolPCB);
            }
            else  // Done printing
            {
               doneeoj1 = doneeoj2 = doneeoj3 = doneeoj4 = doneeoj5 = false;
            }
         }
      }  // END of OutputSpooling Condition Statement
   }  // END - OutputSpooling

   /**
    *  The interrupt routine. Cleans up after whatever just finished
    *  and then assigns a new task.
    */
   public void interruptRoutine()
   {
     switch (taskflag)
     {
       case 0 : //idle
         break;
       case 1 : //output spooling
         refToCPU.setIOI(-4);
         Trace.writeln("Channel 3 finished output spooling");
         Trace.write(outSpoolPCB.getJobID());
         Trace.writeln(": " + outputFullBuffer + " read");
         refToBufPool.putOUT(outputFullBuffer);
         outSpoolPCB = null;
         break;
       case 2 : //a GD or PD instruction
         refToCPU.setIOI(-4);
         Trace.writeln("Channel 3 finished GD or PD");
         Trace.write(ch3pcb.getJobID() + ": ");
         for (int i = 0; i < 10; i++) { Trace.write(gdpdBlock[i]); }
         Trace.writeln(" transferred");
         if (ch3pcb.getTimer() >= ch3pcb.getTimeLimit())
         {
           ch3pcb.eojMessage[0] = "JOB" + ch3pcb.getJobID()
                                + " ran out of time";
           Trace.writeln("PCB to TerminateQ");
           refToTerminateQ.enqueue(ch3pcb);
         }
         else
         {
           Trace.writeln("PCB to ReadyQ");
           refToReadyQ.enqueue(ch3pcb);
         }
         ch3pcb = null;
         break;
       case 3 : //loading
         refToCPU.setIOI(-4);
         Trace.writeln("Channel 3 finished loading");
         Trace.write(ch3pcb.getJobID() + ": ");
         for (int i = 0; i < 10; i++) { Trace.write(loadBlock[i]); }
         Trace.writeln(" loaded");
         if (loaded == 0)
         {
           Trace.writeln("Moving " + ch3pcb.getJobID() + " to ReadyQ");
           refToReadyQ.enqueue(ch3pcb);
         }
         else { LoadQ.push(ch3pcb); }
         ch3pcb = null;
         break;
       case 4 : //input spooling
         refToCPU.setIOI(-4);
         Trace.writeln("Channel 3 finished input spooling");
         if (inSpoolPCB != null) { Trace.write(inSpoolPCB.getJobID()); }
         else { Trace.write("Channel3"); }
         Trace.writeln(": " + inputFullBuffer + " written");
         refToBufPool.putEM(new String());
         break;
     } //end switch

     //assign a new task
     setBusy();
   } //end interrupt routine
} // END - Class Channel3