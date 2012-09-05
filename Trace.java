/**
 *  Handles tracing. Outputs to a file.
 *
 *  @author Ethan Georgi
 *  @version 9.29.1998, 12.06.1998
 */

public class Trace
{
  private static FileIO out = new FileIO("trace.mos", 1);

  public Trace() {};
  public static void write(String s) { out.write(s); }
  public static void writeln(String s) { out.writeln(s); }
}  // END - Class Trace