/**
 *  PCB. Process Control Block. Keeps track of all the
 *  important numbers for a process. Whoo-hoo!
 *
 *  @author Ethan Georgi
 *  @version 9.10.1998, 12.06.1998
 */

public class PCB
{
  private final int maxcards = 30;
  private int[] pMemoryCards, pProgramCards, pDataCards, pOutput;
  private int numMemoryCards, numProgramCards, numDataCards, numOutput;
  private int mLastCard = 0, pLastCard = 0, dLastCard = 0, lastOutput = 0;
  private String jobID;
  private String R, IR;
  private int IC, C;
  private int timeSlice, timer;
  private int TimeLimit, PrintLimit;
  public String[] eojMessage;
  public String[] remarks;
  private int numRemarks = 0;
  public int[] pageTable = new int[10];
  private MOS refToMOS;
  private CPU refToCPU;


  /**
   *  Constructor. Creates a process.
   *
   *  @param id The Job ID
   *  @param tl The maximum number of clock cycles to run this process for
   *  @param pl The maximum number of lines to print. (PD instructions)
   */
  public PCB(String id, int tl, int pl, MOS rtMOS, CPU rtCPU)
  {
    jobID = id; TimeLimit = tl; PrintLimit = pl;
    R = "0000"; IR = "0000"; IC = 0; C = 0;
    numMemoryCards = 0; numDataCards = 0; numOutput = 0; numProgramCards = 0;
    pMemoryCards = new int[maxcards];
    pProgramCards = new int[maxcards];
    pDataCards = new int[maxcards];
    pOutput = new int[PrintLimit];
    eojMessage = new String[2];
    remarks = new String[13];
    timeSlice = 0; timer = 0;
    int i;
    for (i = 0; i < 10; i++)
    {
      pageTable[i] = -1;
    }
    for (i = 0; i < maxcards; i++)
    {
      pProgramCards[i] = -1;
      pDataCards[i] = -1;
    }
    for (i = 0; i < PrintLimit; i++)
    {
      pOutput[i] = -1;
    }
    refToMOS = rtMOS;
    refToCPU = rtCPU;
  }

  /**
   *  This function translates a virtual address to a physical
   *  address, if it's possible. If there's no page loaded where
   *  you're looking... well, we just won't go there.
   *
   *  @param v The Virtual Address
   *  @return The Physical Address
   */
  public int virtualToPhysical(int v)
  {
    int vPage = v / 10, rPage = -1;
    if (pageTable[vPage] != -1)
    {
      rPage = (pageTable[vPage] * 10) + (v % 10);
    }
    return rPage;
  } //end of the world

  /**
   *  Tell me the job id.
   *
   *  @return The Job ID
   */
  public String getJobID() { return jobID; }

  /**
   *  Give me the IC for this PCB. This contains the next instruction that
   *  this process is to execute. Used in context switching.
   *
   *  @return the IC of the process
   */
  public int getIC() { return IC; }

  /**
   *  Save the IC for posterity. Used in context switching.
   *
   *  @param ic The values of the IC to save
   */
  public void setIC(int ic) { IC = ic; }

  /**
   *  Returns R. Used in context switching.
   *
   *  @return the register R
   */
  public String getR() { return R; }

  /**
   *  Save R for later. Used in context switching.
   *
   *  @param r The value to save.
   */
  public void setR(String r) { R = r; }

  /**
   *  Get the IR, which contains the last instruction this process
   *  executed. This is handy when Channel3 is given this process
   *  and has to find out if it needs a GD or PD...
   *  Used in context switching.
   *
   *  @return the IR
   */
  public String getIR() { return IR; }

  /**
   *  Save the IR. Used in context switching.
   *
   *  @param ir The last instruction executed.
   */
  public void setIR(String ir) { IR = ir; }

  /**
   *  Return the results of the last compare, stored in the C register
   *
   *  @return the contents of the C register
   */
  public int getC() { return C; }

  /**
   *  Hang on to the contents of the C register, so that we can have
   *  them around when we need them... Used in context switching.
   *
   *  @param c The contents of the C register (0/1)
   */
  public void setC(int c) { C = c; }

  /**
   *  Quick way to store the R, IR, C, and IC. For context switching.
   *
   *  @param r The R register
   *  @param ir The IR register
   *  @param c The C register
   *  @param ic The IC register
   */
  public void storeRegisters(String r, String ir, int c, int ic)
  {
    setR(r); setIR(ir); setC(c); setIC(ic);
  }

  /**
   *  Tells the process that a card has been loaded into memory
   *  at a specified location. The PCB must create a pointer to this card
   *  and update the number of cards it's keeping track of.
   *
   *  @param n The track number that the card was written to memory.
   */
  public void cardLoadedInMem(int n)
  {
    pMemoryCards[numMemoryCards] = n;
    numMemoryCards++;
  }

  /**
   *  Tells the PCB that a program card has been loaded to the drisk, and
   *  supplies the track number where the card was written. The PCB must
   *  create a pointer to this card and icnrease the counter that keeps
   *  track of the number of program cards we're keeping track of.
   *
   *  @param n The track number where the program card was written.
   */
  public void programCardLoadedAt(int n)
  {
    pProgramCards[numProgramCards] = n;
    numProgramCards++;
  }

  /**
   *  Tells the PCB that a data card has been loaded to the drisk, and
   *  supplies the track number where the card was written. The PCB must
   *  create a pointer to this card and icnrease the counter that keeps
   *  track of the number of data cards we're keeping track of.
   *
   *  @param n The track number where the data card was written.
   */
  public void dataCardLoadedAt(int n)
  {
    pDataCards[numDataCards] = n;
    numDataCards++;
  }

  /**
   *  Given the number of the card to look at (0, 1, 2, 3...) returns the
   *  track number where the card is stored on the drisk.
   *
   *  @param c The number of the card
   */
  public int getMemoryCard(int c) { return pMemoryCards[c]; }

  /**
   *  Given the number of the card to look at (0, 1, 2, 3...) returns the
   *  track number where the card is stored on the drisk.
   *
   *  @param c The number of the card
   */
  public int getDataCard(int c) { return pDataCards[c]; }

  /**
   *  Given the number of the card to look at (0, 1, 2, 3...) returns the
   *  track number where the card is stored on the drisk.
   *
   *  @param c The number of the card
   */
  public int getProgramCard(int c) { return pProgramCards[c]; }

  /**
   *  Increments the time slice counter and timer counter.
   */
  public void incClocks() { timeSlice++; timer++; }

  /**
   *  Reset the time slice counter to 0;
   */
  public void resetTimeSlice() { timeSlice = 0; }

  /**
   *  Fetch the value of the time slice counter. This value is the number of
   *  CPU clock cycles for which this process has been active/running.
   *  Therefore, it is always <= 15. When time slice counter reaches 15, the
   *  process gets switched.
   *
   *  @return The value of the time slice counter
   */
  public int getTimeSlice() { return timeSlice; }

  /**
   *  Fetch the value of the timer. This is the total number of CPU clock
   *  cycles for which this process has run. It's ceiling is the TimeLimit
   *  set by the control card on creation.
   *
   *  @return The total number clock cycles this process has been active.
   */
  public int getTimer() { return timer; }

  /**
   *  Get me the next data card.
   *
   *  @return The track number of the next card on the drisk.
   */
  public int getNextDataCard()
  {
    int next = getDataCard(dLastCard);
    dLastCard++;
    return next;
  }

  /**
   *  Get me the next program card.
   *
   *  @return The track number of the next card on the drisk.
   */
  public int getNextProgramCard()
  {
    int next = getProgramCard(pLastCard);
    pLastCard++;
    return next;
  }

  /**
   *  Lets the PCB know that where on the drisk the output was placed.
   *
   *  @param l The track number where the output was spooled on the drisk
   */
  public void putOutput(int l)
  {
    pOutput[numOutput] = l;
    numOutput++;
  }

  /**
   *  Gets the track number of the specified block that has been spooled
   *  for printing.
   *
   *  @param b The block number
   *  @return The track number on the drisk
   */
  public int getSpooledBlock(int b)
  {
    return pOutput[b];
  }

  /**
   *  The track number to write the block to for printing.
   *
   *  @return The track number where the next block is stored on the drisk
   */
  public int getNextSpooled()
  {
    int next;
    if (numOutput > 0)
    {
      next = getSpooledBlock(lastOutput);
      lastOutput++;
      numOutput--;
    }
    else { next = -1; }
    return next;
  }

  /**
   *  Fetch the number of blocks spooled.
   *
   *  @return The number of blocks spooled for printing
   */
  public int getNumSpooled() { return lastOutput; }

  /**
   *  Set the number of blocks spooled.
   *
   *  @param n The new number of blocks spooled for printing
   */
  public void setNumSpooled(int n) { lastOutput = n; }

  /**
   *  Fetch the number of cards in memory.
   *
   *  @return The number of cards in memory.
   */
  public int getNumMemoryCards() { return numMemoryCards; }

  /**
   *  Fetch the number of data cards on the drisk.
   *
   *  @return The number of data cards on the drisk.
   */
  public int getNumDataCards() { return numDataCards; }

  /**
   *  Fetch the number of program cards on the drisk
   *
   *  @return The number of program cards on the drisk.
   */
  public int getNumProgramCards() { return numProgramCards; }

  /**
   *  Append a remark.
   *
   *  @param r The remark to add to the array of remarks.
   */
  public void addRemark(String r)
  {
    if (numRemarks < 13)
    {
      remarks[numRemarks] = r;
      numRemarks++;
    }
  }

  /**
   *  What is the PrintLimit for this process?
   *
   *  @return the PrintLimit
   */
  public int getPrintLimit() { return PrintLimit; }

  /**
   *  What is the SmegLimit for this process?
   *
   *  @return the TimeLimit
   */
  public int getTimeLimit() { return TimeLimit; }

} //end of class PCB