package test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.logging.ConsoleHandler;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import oeg.photo_merger.main.PhotoMerger;

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

  private void runTest()
  {
    this.logger.info("Running test");
  }
  
    
  
  public static void main(String[] args)
  {
    new PhotoTest();
  }
}
