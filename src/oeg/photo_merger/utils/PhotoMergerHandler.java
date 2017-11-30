package oeg.photo_merger.utils;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JTextArea;

/**
 * Simple class used to display log messages to a JTextArea
 */
public class PhotoMergerHandler extends Handler 
{
  /* The text area used to display log messages */
  JTextArea textArea = null;

  /**
   * Instantiates a new object using the text area to display log messages
   * 
   * @param textArea The text area used to display log messages
   */
  public PhotoMergerHandler(JTextArea textArea) 
  {
    super();
    this.textArea = textArea;
  }

  /**
   * Publish a LogRecord.  The logging request was made initially to a Logger 
   * object, which initialized the LogRecord and forwarded it here.
   * The Handler is responsible for formatting the message, when and if 
   * necessary. The formatting should include localization.
   * 
   * @param record description of the log event. A null record is silently 
   *        ignored and is not published
   */
  public void publish(LogRecord record) 
  {
    // ensure that this log record should be logged by this Handler
    if (!isLoggable(record))
      return;
    
    // Output the formatted data to the text area
    String txt = this.textArea.getText() + "\n";
    this.textArea.setText(txt + getFormatter().format(record));
  }

  /**
   * Flush any buffered output.
   */
  public void flush() 
  {
    this.textArea.repaint();
  }

  /**
   * Close the Handler and free all associated resources.
   * 
   * The close method will perform a flush and then close the Handler. After 
   * close has been called this Handler should no longer be used. Method calls 
   * may either be silently ignored or may throw runtime exceptions.
   * 
   * Throws: SecurityException - if a security manager exists and if the caller 
   *         does not have LoggingPermission("control").
   */
  public void close() throws SecurityException 
  {
    
  }
}