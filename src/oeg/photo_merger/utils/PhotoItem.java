package oeg.photo_merger.utils;

import java.util.Comparator;

import java.util.Date;

/**
 * Simple data container class used to store photo items.  Each PhotoItem object
 * contains the name of the file, the date the picture was taken and the 
 * extension of the file.  The class implements Comparable and Comparator to
 * sort items in a Collection based on the date it was taken rather than name.
 * 
 * The extension is used later on to rename the picture using a prefix and an 
 * index number and using the same extension as the original file.
 * 
 * @author Oscar E. Ganteaume
 *
 */
public class PhotoItem implements Comparator<PhotoItem>, Comparable<PhotoItem> 
{
  /**
   * Stores the name of the picture file
   */
  private String filename = "";
  /**
   * Stores the date when the picture was taken
   */
  private Date dateTaken = null;
  /**
   * Stores the extension of the original file so it can be used later when 
   * renaming the file
   */
  private String extension = "";
  
  /**
   * Empty constructor which sets the filename and the extension to an empty
   * string and the date to null.
   */
  public PhotoItem()
  {
    this.filename = "";
    this.dateTaken = null;
    this.extension = "";
  }
  
  /**
   * Creates a new PhotoItem object and sets all the different parameters
   * 
   * @param filename the name of the picture file
   * @param dateTaken the date when the picture was taken
   * @param extension the extension of the original file so it can be used 
   *        later when renaming the file
   */
  public PhotoItem(String filename, Date dateTaken, String extension)
  {
    this.setFilename(filename);
    this.setDateTaken(dateTaken);
    this.setExtension(extension);
  }
  
  /**
   * Gets the name of the picture file
   * 
   * @return  the name of the picture file
   */
  public String getFilename()
  {
    return filename;
  }
  
  /**
   * Sets the name of the picture file
   * 
   * @param filename  the name of the picture file
   */
  public void setFilename(String filename)
  {
    this.filename = filename;
  }
  
  /**
   * Gets the date when the picture was taken
   * 
   * @return the date when the picture was taken
   */
  public Date getDateTaken()
  {
    return dateTaken;
  }
  
  /**
   * Sets the date when the picture was taken
   * 
   * @param dateTaken the date when the picture was taken
   */
  public void setDateTaken(Date dateTaken)
  {
    this.dateTaken = dateTaken;
  }
  
  /**
   * Gets the extension the extension of the original file so it can be used 
   * later when renaming the file
   * 
   * @return the extension of the original file so it can be used 
   *         later when renaming the file
   */
  public String getExtension()
  {
    return this.extension;
  }
  
  /**
   * Sets the extension the extension of the original file so it can be used 
   * later when renaming the file
   * 
   * @param extension the extension of the original file so it can be used 
   *        later when renaming the file
   */
  public void setExtension(String extension)
  {
    this.extension = extension;
  }
  
  /**
   * 
   * Compares the given item against the current object.  The comparison is
   * done based on when the date was taken and returns the following:
   * 
   *    -1 if the current photo was taken prior the given item
   *     0 if the current photo was taken at the same time as the given item
   *     1 if the current photo was taken after the given item 
   * 
   * @return an integer based on the values explained above
   */
  public int compareTo(PhotoItem item)
  {
    // if the item does not have date ( regular image rather than a picture)
	  if( item == null || item.dateTaken == null )
	    return -1;
	  // if the current picture does not have a date taken
	  else if( this.dateTaken == null )
	    return 1;
	  // else, we can compare dates...
	  else
	    return this.dateTaken.compareTo(item.getDateTaken());
  }
  
  /**
   * Compares the item1 against the item2.  The comparison is done based on 
   * when the date was taken and returns the following:
   * 
   *    -1 if item1 was taken prior to item2
   *     0 if item1 was taken at the same time as item2
   *     1 if item1 was taken after item2
   * 
   * @return an integer based on the values explained above
   */
  public int compare(PhotoItem item1, PhotoItem item2)
  {
    return item1.getDateTaken().compareTo(item2.getDateTaken());
  }
  
  /**
   * Returns a string representation of the object.  The string is of the form:
   *    Picture: <filename>, Taken: <date taken>
   *    
   * @return a string representation of the stored object as shown above
   */
  public String toString()
  {
    StringBuffer buffer = new StringBuffer();
    buffer.append("Picture: ");
    buffer.append(this.filename);
    buffer.append(", ");
    
    buffer.append("Taken: ");
    buffer.append(this.dateTaken.toString());
    
    return buffer.toString();
  }
}
