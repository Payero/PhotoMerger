package oeg.photo_merger.utils;


import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * A simple formatter class used to print simple messages to the logger.
 * 
 * @author Oscar E. Ganteaume
 *
 */
public class PhotoMergerFormatter extends Formatter
{
  /* Determine whether to add an new line character or not after each record */
  private boolean insertNewLine = true;
  
  /**
   *  Instantiates a new object and sets the insertion of a new line to true.
   */
  public PhotoMergerFormatter()
  {
    this(true);
  }
   /**
    * Instantiates a new object and sets whether to insert a new line after the
    * recording of each log record or not.
    *  
    * @param insertNewLine whether to insert a new line after the recording of 
    *        each log record or not
    */
  public PhotoMergerFormatter(boolean insertNewLine)
  {
    super();
    this.insertNewLine = insertNewLine;
  }
  
  /**
   * Displays the given message to the handlers.
   * 
   * @param record the object containing the message to be display on the 
   *        handlers
   */
  public String format(LogRecord record)
  {
    StringBuilder builder = new StringBuilder(1000);
    builder.append("[").append(record.getLevel()).append("] - ");
    builder.append(formatMessage(record));
    if( this.insertNewLine )
      builder.append("\n");
    return builder.toString();
  }

  /**
   * Gets the header of the handler
   * 
   * @return the header contained in the handler
   */
  public String getHead(Handler h)
  {
    return super.getHead(h);
  }

  /**
   * Gets the footer of the handler
   * 
   * @return the footer contained in the handler
   */
  public String getTail(Handler h)
  {
    return super.getTail(h);
  }
}