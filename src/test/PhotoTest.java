package test;

import java.awt.Dimension;
import java.text.SimpleDateFormat;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextArea;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.builder.api.AppenderComponentBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilder;
import org.apache.logging.log4j.core.config.builder.api.ConfigurationBuilderFactory;
import org.apache.logging.log4j.core.config.builder.api.RootLoggerComponentBuilder;
import org.apache.logging.log4j.core.config.builder.impl.BuiltConfiguration;

import oeg.photo_merger.utils.TextAreaAppender;


public class PhotoTest
{
  private Logger logger = LogManager.getLogger(PhotoTest.class.getName());
  SimpleDateFormat qtFormatter = 
      new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
  
  
  public PhotoTest()
  {
    
    try
    {
      this.runTest();
    }
    catch( Exception e )
    {
      this.logger.fatal("Message: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void runTest() throws Exception
  {
    this.createAndShowGUI();
    Thread.sleep(3000);
    this.logger.debug("Done sleeping");
    Thread.sleep(1000);
    this.logger.debug("Running test");
    Thread.sleep(1000);
    this.logger.info("Running test");
    Thread.sleep(1000);
    this.logger.warn("Running test");
    Thread.sleep(1000);
    this.logger.error("Running test");
    Thread.sleep(1000);
  }
  
  private void createAndShowGUI() {
    org.apache.logging.log4j.core.Logger concrete = (org.apache.logging.log4j.core.Logger)this.logger;
    Map<String, Appender> objs = concrete.getAppenders();
    
    JTextArea area = new JTextArea();
    Dimension dim = new Dimension(600, 300);
    area.setSize(dim);
    area.setMinimumSize(dim);
    
    for( String name : objs.keySet() ) {
      this.logger.info("The Name " + name );
      Appender obj = objs.get(name);
      if( obj instanceof TextAreaAppender ) 
      {
        this.logger.info("Found it!!");
        
        ((TextAreaAppender) obj).setTextArea(area);
      }
    }
    
    //Create and set up the window.
    JFrame frame = new JFrame("HelloWorldSwing");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(dim);
    frame.setMinimumSize(dim);

    //Add the ubiquitous "Hello World" label.
    JLabel label = new JLabel("Hello World");
    frame.getContentPane().add(label);
    frame.getContentPane().add(area);
    

    //Display the window.
    frame.pack();
    frame.setVisible(true);
}
  
    
  
  public static void main(String[] args)
  {
    new PhotoTest();
  }
}
