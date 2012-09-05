/**
 *  Channel 1. Simulates reading cards from the card reader by reading
 *  records from a file. Each line is a record.
 *
 *  @author Ethan Georgi
 *  @version 10.10.1998, 12.06.1998
 */

public class Channel1
{
  private static BufferPool refToBufPool;
  private static CPU refToCPU;
  private static FileIO cardReader;
  private static String InputFullBuffer, EmptyBuffer;
  private static boolean busy;
  private static int timer = 0;  //ch1 takes 5 cycles to complete its job

  /**
   *  Constructor.
   *
   *  @param bp Reference to the Buffer Pool
   *  @param cpu Reference to the CPU
   */
  public Channel1(BufferPool bp, CPU cpu)
  {
    refToBufPool = bp;
    refToCPU = cpu;
    cardReader = new FileIO("testprogram.mos", 0);
    busy = false;
  }

  /**
   *  What's the timer set at?
   */
  public int getTimer() { return timer; }

  /**
   *  Reads a card from the cardReader and puts it in an InputFullBuffer
   */
  public void read()
  {
    if (!refToBufPool.isEMEmpty() && !cardReader.eof())
    {
      InputFullBuffer = refToBufPool.getEM(); //"empty"
      InputFullBuffer = cardReader.readln();
    }
    else { timer = 0; busy = false; }
  }

  /**
   *  House cleaning for the Interrupt Routine.
   */
  public void cleanUp()
  {
    if (InputFullBuffer != null)
    {
      Trace.writeln("Channel1 read " + InputFullBuffer);
      refToBufPool.putIN(InputFullBuffer);
      refToCPU.setIOI(-1);
    }
    if ((!cardReader.eof()) && (!refToBufPool.isEMEmpty()))
    {
      if (cardReader.eof()) { refToCPU.setIOI(-1); }
      else
      {
        busy = true;
        timer = 0;
        read();
      }
    }
  }

  /**
   *  Increment the clock.
   */
  public void incClock()
  {
    timer++;
    if (done())
    {
      refToCPU.setIOI(1);
      busy = false;
      timer = 0;
    }
  }

  /**
   *  Is channel 1 busy, slaving away, or otherwise occupied?
   */
  public boolean isBusy() { return busy; }

  /**
   *  Is it time to raise an interrupt yet?
   */
  public boolean done() { return (timer == 5); }

  /**
   *  Tell Channel1 to get back to work, slave!
   */
  public void setBusy() { busy = true; timer = 0; read(); }

} //end of class Ch1