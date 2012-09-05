/**
 *  Generic Buffer Queue - Designed for Channels 1, 2 and 3
 *
 *  @author Ken Schulz
 *  @version 10.10.1998, 12.06.1998
 */

public class BufferPool
{
   protected Queue emptyBuffer;
   protected Queue inputFullBuffer;
   protected Queue outputFullBuffer;
   protected int eb;
   protected int ifb;
   protected int ofb;

/**
 * Default constructor
 */
   public BufferPool()
   {
      eb = 10;
      ifb = 0;
      ofb = 0;
      emptyBuffer = new Queue(eb);
      inputFullBuffer = new Queue(ifb);
      outputFullBuffer = new Queue(ofb);
         // Initialize the empty buffer
      for (int i = 0; i < eb; i++) {
         emptyBuffer.enqueue(new String());
       }
   }

/**
 * removes and returns an empty buffer from the eb queue
 * eb must be checked before this method is used
 *
 * @return String of an empty buffer
 */
   public String getEM()
   {
      eb--;
      return (String)emptyBuffer.dequeue();
   }

/**
 * removes and returns an inputFullBuffer if contains data
 * ifb must be checked before this method is used
 *
 * @return String of empty buffer
 */
   public String getIN()
   {
      ifb--;
      return (String)inputFullBuffer.dequeue();
   }

/**
 * removes and returns an outputFullBuffer if contains data
 * ofb must be checked before this method is used
 *
 * @return String of empty buffer
 */
   public String getOUT()
   {
      ofb--;
      return (String)outputFullBuffer.dequeue();
   }

/**
 * places an empty buffer on the emptyBufferQueue
 *
 * @param newEM is the empty buffer that will be placed back
 *        onto the emptyBuffer queue
 */
   public void putEM(String newEM)
   {
      eb++;
      emptyBuffer.enqueue(newEM);
   }

/**
 * places a new inputFull Buffer on the inputFullBuffer Queue
 *
 * @param newIN is a new inputFull buffer to be added to the queue
 */
   public void putIN(String newIN)
   {
      ifb++;
      inputFullBuffer.enqueue(newIN);
   }

/**
 * places a new outputFull Buffer on the outputFullBuffer Queue
 *
 * @param newOUT is a new outputFull buffer to be added to the queue
 */
   public void putOUT(String newOUT)
   {
      ofb++;
      outputFullBuffer.enqueue(newOUT);
   }

/**
 * checks to see if the emptyBuffer is empty
 *
 * @param return True if the emptyBuffer is empty
 */
   public boolean isEMEmpty()
   {
      return(emptyBuffer.getSize() == 0);
   }

/**
 * checks to see if the inputFullBuffer is empty
 *
 * @param return True if the inputFullBuffer is empty
 */
   public boolean isINEmpty()
   {
      return(inputFullBuffer.getSize() == 0);
   }

/**
 * checks to see if the outputFullBuffer is empty
 *
 * @param return True if the outputFullBuffer is empty
 */
   public boolean isOUTEmpty()
   {
      return(outputFullBuffer.getSize() == 0);
   }

}  // END of class BufferPool