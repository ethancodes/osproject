/**
 *  Channel 2 simulates output to a printer by writing to a file.
 *
 *  @author Ethan Georgi
 *  @version 10.10.1998, 12.06.1998
 */

public class Channel2
{
  private static BufferPool refToBufPool;
  private static CPU refToCPU;
  private static FileIO printer;
  private static String OutputFullBuffer, EmptyBuffer;
  private static boolean busy;
  private static int timer = 0;

  /**
   *  Constructor.
   *
   *  @param bp Reference to the BufferPool
   *  @param cpu Reference to the CPU
   */
  public Channel2(BufferPool bp, CPU cpu)
  {
    refToBufPool = bp;
    refToCPU = cpu;
    printer = new FileIO("printer.mos", 1);
    busy = false;
  }

  /**
   *  What's the timer?
   */
  public int getTimer() { return timer; }

  /**
   *  Writes to the "printer"
   */
  public void write()
  {
    if (!refToBufPool.isOUTEmpty())
    {
      OutputFullBuffer = refToBufPool.getOUT();
      printer.writeln(OutputFullBuffer);
      EmptyBuffer = new String();
    }
    else { timer = 0; busy = false; }
  }

  /**
   *  House cleaning...
   */
  public void cleanUp()
  {
    refToCPU.setIOI(-2);
    if (OutputFullBuffer != null)
    {
      Trace.writeln("Channel2 printed " + OutputFullBuffer);
      refToBufPool.putEM(EmptyBuffer);
    }
    if (!refToBufPool.isOUTEmpty())
    {
      busy = true;
      timer = 0;
      write();
    }
  }

  /**
   *  Increment the clock
   */
  public void incClock()
  {
    timer++;
    if (done())
    {
      refToCPU.setIOI(2);
      busy = false;
      timer = 0;
    }
  }

  /**
   *  Is channel 2 busy, slaving away, or otherwise occupied
   */
  public boolean isBusy() { return busy; }

  /**
   *  Kick Channel2 into gear.
   */
  public void setBusy() { timer = 0; busy = true; write(); }

  /**
   *  Is it time to raise in interrupt?
   */
  public boolean done() { return (timer == 5); }

} //end of class ch2