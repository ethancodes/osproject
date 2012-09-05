/**
 *  Drisk Class - Simulation of a computers hard disk
 *  ERRORS HANDLED:
 *  - Access to unallocated tracks
 *  ERRORS NOT HANDLED:
 *  - Index out of bounds
 *
 *  @author Ken Schulz
 *  @version 09.10.1998, 12.06.1998
 */

public class Drisk
{
   protected String [] track;
   protected boolean [] freeTrack;
   protected static final int TRACKS = 100; // Number of tracks on disk

/**
 * Default Constructor initializes the drisk with 100 free tracks.
 * 1 Card = 10 Words
 * 1 Track = 10 Words
 * 1 Block = 10 Words
 * 1 Drisk = 1000 Words = 100 Track
 * Each word is a element in the tracks array
 */
   public Drisk()
   {
      track = new String[TRACKS*10];
      for (int i = 0; i < (TRACKS*10); i++) {
         track[i] = "0000";
      }

      freeTrack = new boolean[TRACKS];
      for (int i = 0; i < TRACKS; i++) {
         freeTrack[i] = true;
      }
   }

/**
 * readTrack returns the indicated track as an array of strings
 * ERROR: If track is not allocated
 *
 * @param trackNumber is the index of the track that will be returned
 * @return An array of words which equals a track
 */
   public String [] readTrack(int trackNumber)
   {
      if (freeTrack[trackNumber]) {
         System.out.println("Track " + trackNumber + " is not initialized!");
         return null;
      }

      String [] returnTrack = new String[10];
      int offset = trackNumber * 10;

      for (int i = 0; i < 10; i++) {
         returnTrack[i] = track[offset + i];
       }
      return returnTrack;
   }

/**
 * writeTrack writes given information to a specific track
 * NOTE: This method must take in a string array of 10 Elements!!!
 * ERROR: If track is not allocated
 *
 * @param trackNumber is the index of the track that the data will be writen to
 * @param newTrack is the new track that will be set at index trackNumber
 * @return void
 */
   public void writeTrack(int trackNumber, String [] newTrack)
   {
      if (freeTrack[trackNumber]) {
         System.out.println("Track " + trackNumber + " is not initialized!");
      }
      else {
         int offset = trackNumber * 10;
         for (int i = 0; i < 10; i++) {
            track[offset + i] = newTrack[i];
         }
      }
   }

/**
 * readBlock returns the indicated block as an array of strings
 * ERROR: If track is not allocated
 *
 * @param blockNumber is the index of the track that will be returned
 * @return An array of words which equals a block
 */
   public String [] readBlock(int blockNumber)
   {
      if (freeTrack[blockNumber]) {
         System.out.println("Track " + blockNumber + " is not initialized!");
         return null;
      }

      String [] returnBlock = new String[10];
      int offset = blockNumber * 10;

      for (int i = 0; i < 10; i++) {
         returnBlock[i] = track[offset + i];
       }

      return returnBlock;
   }

/**
 * writeBlock writes given information to a specific Block of data on the track
 * NOTE: This method must take in a string array of 10 Elements!!!
 * ERROR: If track is not allocated
 *
 * @param blockNumber is the index of the track that the data will be writen to
 * @param newBlock is the new block of data that will be set at index
 *        blockNumber
 * @return void
 */
   public void writeBlock(int blockNumber, String [] newBlock)
   {
      if (freeTrack[blockNumber]) {
         System.out.println("Track " + blockNumber + " is not initialized!");
      }
      else {
         int offset = blockNumber * 10;
         for (int i = 0; i < 10; i++) {
            track[offset + i] = newBlock[i];
         }
      }
   }

/**
 * allocate returns the track number of a newly allocated track
 * returns -1 if none available
 *
 * @return int of allocated track
 */
   public int allocate()
   {
      if (!this.isFull()) {
         int allocated = this.getFreeTrack();
         freeTrack[allocated] = false;
         return allocated;
      }
      else {return -1;}
   }

/**
 * deallocate sends sepcfied track back into the abyss of reusable drisk space
 *
 * @param freeTrackNumber is the track number that will be released to the wild
 * @return void
 */
   public void deallocate(int freeTrackNumber)
   {
      freeTrack[freeTrackNumber] = true;
   }

/**
 * getFreeTrack returns the lowest available free track
 * returns -1 if none available
 *
 * @return int of free track
 */
   public int getFreeTrack()
   {
      for (int i = 0; i < TRACKS; i++) {
         if (freeTrack[i]) {return i;}
       }
      return -1;  // If Drisk is full
   }

/**
 * isFree returns true if the track is free
 *
 * @param trackNumber is the track th
 * @return boolean status oat will be checked if free
 */
   public boolean isFree(int trackNumber)
   {
      return freeTrack[trackNumber];
   }

/**
 * isFull returns boolean true if drisk is full
 *
 * @return true if drisk is full
 */
   public boolean isFull()
   {
      for (int i = 0; i < TRACKS; i++) {
         if (freeTrack[i]) {return false;}
       }
      return true;
   }

/**
 * getFreeTracks return the number of tracks that are free on Drisk
 *
 * @return number of free tracks
 */
   public int getFreeTracks()
   {
      int free = 0;
      for (int i = 0; i < TRACKS; i++) {
         if (freeTrack[i]) {free++;}
      }
      return free;
   }
}  // END Class Drisk