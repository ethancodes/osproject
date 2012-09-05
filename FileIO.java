/**
 *  Reads and writes Strings to a file.<P>
 *
 *  <UL><CODE>
 *  FileIO in = new FileIO("one.txt", 0);<BR>
 *  FileIO out = new FileIO("two.txt", 1);<BR>
 *  int i;<BR>
 *  for (i = 0; !in.eof(); i++) { out.writeln(in.readln()); }
 *  </CODE></UL>
 *
 *  @author Ethan Georgi
 *  @version 9.7.1998, 12.06.1998
 */
import java.io.*;

public class FileIO
{
  private String filename;
  private File f;
  private int size;
  private int offset = 0;
  private char[] data;
  private String[] lines = new String[5000];
  private int in = -1;
  public int numlines;

  /**
   *  Constructor. Note that a FileIO object only does input or output
   *  on a file, not both.
   *
   *  @param newFileName The name of the file you want to operate on.
   *  @param mode The mode of operations, 0 = input, 1 = output
   */
  public FileIO (String newFileName, int mode)
  {
    filename = newFileName;
    f = new File(filename);

    if (mode == 0) //input
    {
      size = (int)f.length();
      data = new char[size];
      readEverything();
    }
    else //output
    {
      size = 0;
    }
  }

  /**
   *  End Of File. Have we read in all the lines we can.
   *
   *  @return true, cannot read in any more; false, otherwise
   */
  public boolean eof()
  {
    return ((in + 1) >= numlines);
  }

  /**
   *  Read a line from the file and return it.
   *
   *  @return the next line of text
   */

  public String readln()
  {
    in++;
    return lines[in];
  }

  /**
   *  Reads everything in the file in. Used by constructor.
   */

  public void readEverything()
  {
    String line = new String();
    while (offset < size)
    {
      try {
        FileReader in = new FileReader(f);
        offset += in.read(data, offset, size);
        line = new String(data);
      }
      catch (Exception ex)
      {
        System.out.println(ex);
        System.exit(0);
      }
    } //end of while
    //now hack it up into the string array
    int i, stra = 0;
    char[] str = new char[40];
    int strc = 0;
    for (i = 0; i < size; i++)
    {
      if (data[i] == 13) {}
      else if (data[i] == 10)
      {
        lines[stra] = new String(str);
        stra++;
        str = new char[40];
        strc = 0;
      }
      else
      {
        str[strc] = data[i];
        strc++;
      }
    } //end of for loop
    numlines = stra;
  }

  /**
   *  Writes a string to a file. Note that this method appends a
   *  carriage return-line feed, you do not need to add \n to the
   *  end of the string you want written.
   *
   *  @param s the string you want written.
   */

  public void writeln(String s)
  {
    char[] newline = { 13, 10 };
    try
    {
      FileWriter out = new FileWriter(filename, true);
      out.write(s);
      out.write(newline);
      out.flush();
      out.close();
    }
    catch (Exception ex)
    {
      System.out.println(ex);
      System.exit(0);
    }
  }

  /**
   *  Writes a string to a file.
   *
   *  @param s the string you want written.
   */

  public void write(String s)
  {
    try
    {
      FileWriter out = new FileWriter(filename, true);
      out.write(s);
      out.flush();
      out.close();
    }
    catch (Exception ex)
    {
      System.out.println(ex);
      System.exit(0);
    }
  }
}  // END - Class FileIO