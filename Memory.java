/**
 *  Memory Class - Simulation of a computers main memory
 *
 *  @author Ken Schulz
 *  @version 09.10.1998, 12.06.1998
 */

public class Memory
{
   protected String [] mem;
   protected boolean [] freeBlock;
   protected static final int MEMSIZE = 30; // Number of blocks in memory

/**
 * Default Constructor initializes memory with 30 blocks.
 * 1 Block = 10 Words
 * 1 Word = 4 Bytes
 * Each word is a element in the mem array
 * Memory consists of 30 Blocks = 300 Words = 1200 Bytes
 */
   public Memory()
   {
      mem = new String[MEMSIZE*10];
      for (int i = 0; i < (MEMSIZE*10); i++) {
         mem[i] = "    ";
      }
      freeBlock = new boolean[MEMSIZE];
      for (int i = 0; i < MEMSIZE; i++) {
         freeBlock[i] = true;
      }
   }

/**
 * readBlock returns the indicated block as an array of strings
 *
 * @param blockNumber is the index of mem that will be returned
 * @return An array of words which equals a block (10 Words)
 */
   public String [] readBlock(int blockNumber)
   {
      String [] returnBlock = new String[10];
      int offset = blockNumber * 10;

      for (int i = 0; i < 10; i++) {
         returnBlock[i] = mem[offset + i];
       }

      return returnBlock;
   }

/**
 * writeBlock writes given information to a specific block of memory
 * NOTE: This method must take in a string array of 10 Elements!!!
 *
 * @param blockNumber is the index of mem that the data will be writen to
 * @param newBlock is the new block that will be set at index blockNumber
 * @return void
 */
   public void writeBlock(int blockNumber, String [] newBlock)
   {
      int offset = blockNumber * 10;

      for (int i = 0; i < 10; i++) {
         mem[offset + i] = newBlock[i];
       }
   }

/**
 * readWord returns the indicated word as a string
 *
 * @param wordNumber is the index of mem that will be returned
 * @return A String which is a 4 Byte Word
 */
   public String readWord(int wordNumber)
   {
      return mem[wordNumber];
   }

/**
 * writeWord writes given information to a specific word in memory
 *
 * @param wordNumber is the index of mem that the data will be writen to
 * @param newWord is the new word that will be set at index wordNumber
 * @return void
 */
   public void writeWord(int wordNumber, String newWord)
   {
      mem[wordNumber] = newWord;
   }

/**
 * allocate returns the block number of a newly allocated memory block
 * returns -1 if none available
 *
 * @return int of allocated block
 */
   public int allocate()
   {
      if (!this.isFull()) {
         int allocated = this.getFreeBlock();
         freeBlock[allocated] = false;
         return allocated;
      }
      else {return -1;}
   }

/**
 * deallocate sends sepcfied block back into the abyss of reusable memory
 *
 * @param freeBlockNumber is the block number that will be released to the wild
 * @return void
 */
   public void deallocate(int freeBlockNumber)
   {
      freeBlock[freeBlockNumber] = true;
   }

/**
 * getFreeBlock returns the lowest available free block
 * returns -1 if none available
 *
 * @return int of free block
 */
   public int getFreeBlock()
   {
      for (int i = 0; i < MEMSIZE; i++) {
         if (freeBlock[i]) {return i;}
       }
      return -1;  // If Drisk is full
   }

/**
 * isFree returns true if the block is free
 *
 * @param blockNumber is the track number to be checked
 * @return boolean status that will be checked for free status
 */
   public boolean isFree(int blockNumber)
   {
      return freeBlock[blockNumber];
   }

/**
 * isFull returns boolean true if memory is full
 *
 * @return true if memory is full
 */
   public boolean isFull()
   {
      for (int i = 0; i < MEMSIZE; i++) {
         if (freeBlock[i]) {return false;}
       }
      return true;
   }

/**
 * getFreeBlocks return the number of blocks that are free in memory
 *
 * @return number of free blocks
 */
   public int getFreeBlocks()
   {
      int free = 0;
      for (int i = 0; i < MEMSIZE; i++) {
         if (freeBlock[i]) {free++;}
      }
      return free;
   }

  /**
   *  Dump the contents of memory to the Trace File...
   *
   *  @param n Dump memory 0 to n...
   */
  public void dump(int n)
  {
    int i;
    for (i = 0; i < n; i++)
    {
      Trace.write(" - - - |" + readWord(i) + "| Memory " + Integer.toString(i));
      if ((i % 10) == 0)
      {
        if (isFree(i / 10)) { Trace.writeln("  FREE"); }
        else { Trace.writeln("  ALLOCATED"); }
      }
      else { Trace.writeln(""); }
    }
  }
}  // END Class Memory