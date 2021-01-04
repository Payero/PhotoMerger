package oeg.photo_merger.utils;

import java.io.Serializable;
import java.util.concurrent.locks.*;

import javax.swing.JTextArea;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.appender.AppenderLoggingException;
import org.apache.logging.log4j.core.config.plugins.*;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.core.config.Property;

// note: class name need not match the @Plugin name.
@Plugin(
    name = "TextAreaAppender", 
    category = "Core", 
    elementType = "appender", 
    printObject = true)
public final class TextAreaAppender extends AbstractAppender
{

  /* The text area used to display log messages */
  JTextArea textArea = null;

  private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
  private final Lock readLock = rwLock.readLock();

  protected TextAreaAppender(String name, Filter filter,
      Layout<? extends Serializable> layout, final boolean ignoreExceptions, Property[] props)
  {
    super(name, filter, layout, ignoreExceptions, props);
  }
  
  public void setTextArea( JTextArea area ) 
  {
    this.textArea = area;
  }

  /**
   * Flush any buffered output.
   */
  public void flush() 
  {
    this.textArea.repaint();
  }

  public void clearText() 
  {
    this.textArea.setText("");
  }
  
  public void setlevel(Level level ) {
    TextAreaAppender.LOGGER.atLevel(level);
  }
  
  // The append method is where the appender does the work.
  // Given a log event, you are free to do with it what you want.
  // This example demonstrates:
  // 1. Concurrency: this method may be called by multiple threads concurrently
  // 2. How to use layouts
  // 3. Error handling
  @Override
  public void append(LogEvent event)
  {
    readLock.lock();
    try
    {
      
//      final byte[] bytes = getLayout().toByteArray(event);
      String msg = new String(getLayout().toByteArray(event));
      
      if( this.textArea != null ) 
      {
        this.textArea.append(msg);
        this.textArea.update(this.textArea.getGraphics());
      }

    }
    catch (Exception ex)
    {
      if (!ignoreExceptions())
      {
        throw new AppenderLoggingException(ex);
      }
    }
    finally
    {
      readLock.unlock();
    }
  }

  // Your custom appender needs to declare a factory method
  // annotated with `@PluginFactory`. Log4j will parse the configuration
  // and call this factory method to construct an appender instance with
  // the configured attributes.
  @PluginFactory
  public static TextAreaAppender createAppender(
      @PluginAttribute("name") String name,
      @PluginElement("Layout") Layout<? extends Serializable> layout,
      @PluginElement("Filter") final Filter filter,
      @PluginAttribute("otherAttribute") String otherAttribute)
  {
    if (name == null)
    {
      return null;
    }
    if (layout == null)
    {
      layout = PatternLayout.createDefaultLayout();
    }
    
    
    return new TextAreaAppender(name, filter, layout, true, Property.EMPTY_ARRAY);
  }
}