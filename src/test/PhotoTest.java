package test;


import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.StringValue;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.media.QuickTimeMediaDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import com.drew.metadata.mp4.Mp4Directory;

import oeg.photo_merger.utils.PhotoItem;
import oeg.photo_merger.utils.PhotoMergerUtils;


public class PhotoTest
{
  private Logger logger = Logger.getLogger(PhotoTest.class.getName());
  SimpleDateFormat qtFormatter = 
      new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

  
  private static Logger getLogger()
  {
    Logger log = Logger.getLogger("my.logger");
    log.setLevel(Level.INFO);
    ConsoleHandler handler = new ConsoleHandler();
    handler.setLevel(Level.INFO);
    handler.setFormatter(new SimpleFormatter());
    log.addHandler(handler);
    
    return log;
  }
  
  public PhotoTest()
  {
    this.logger = PhotoTest.getLogger();
    try
    {
      this.runTest();
    }
    catch( Exception e )
    {
      this.logger.severe("Message: " + e.getMessage());
      e.printStackTrace();
    }
  }

  private void runTest() throws Exception
  {
    this.logger.fine("Running the PhotoTest");
    
//    this.testPath("/home/oeg/Desktop/Pics/tmp");   
    this.getData("/home/oeg/Desktop/Pics/tmp/IMG_1576.MP4");
    this.getData("/home/oeg/Desktop/Pics/tmp/IMG_1756.MOV");
//    this.getData("");
  }

  private void testPath(String path) throws Exception
  {
    File root = new File(path);
    SimpleDateFormat qtFormatter = 
        new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");

    
    if( root != null && root.isDirectory() )
    {
      logger.fine("The Path is valid: " + root.getAbsolutePath());
      File[] files = root.listFiles();
      logger.fine("It has " + files.length + " Files" );
      for( File file : files )
      {
        String fname = file.getAbsolutePath();
        this.getData(fname);
      }
    }
  }
  
  
  private void getData(String path) throws Exception
  {
    this.logger.info("Processing " + path);
    File file = new File(path);
  
    Metadata metadata = ImageMetadataReader.readMetadata(file);
    this.printMetadata(metadata);

    
    Directory directory = null;
    Date date = null;
    
    if( metadata.containsDirectoryOfType(ExifSubIFDDirectory.class ) )
    {
      directory = metadata.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
      date = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
    }
    else if( metadata.containsDirectoryOfType(QuickTimeDirectory.class ) )
    {
      directory = metadata.getFirstDirectoryOfType(QuickTimeDirectory.class);
      date = directory.getDate(QuickTimeDirectory.TAG_CREATION_TIME);
    }
    else if( metadata.containsDirectoryOfType(Mp4Directory.class ) )
    {
      directory = metadata.getFirstDirectoryOfType(Mp4Directory.class);
      date = directory.getDate(Mp4Directory.TAG_CREATION_TIME);
    }
    
    this.logger.info("File " + path + " was created on " + date.toString());
  }

  /**
   * Gets a Date object formatted as Fri Sep 22 23:02:12 EDT 2017.  If the 
   * string cannot be parsed as a Date it returns null
   * 
   * @param dateStr the string containing the desired date
   * @return a date object if the string is parsed properly or null otherwise
   */
  private Date getDate( String dateStr )
  {
    Date date = null;
    try 
    {
      date = this.qtFormatter.parse(dateStr);

    } catch (ParseException e) 
    {
      // ignore formatting errors, just return null
    }
    return date;
  }
  

  
  /**
   * Write all extracted values to stdout.
   */
  private void printMetadata(Metadata metadata)
  {
      System.out.println();
      System.out.println("-------------------------------------------------");
      System.out.println();

      //
      // A Metadata object contains multiple Directory objects
      //
      for (Directory directory : metadata.getDirectories()) {
        System.out.println("Directory " + directory.getName());
        
          //
          // Each Directory stores values in Tag objects
          //
          for (Tag tag : directory.getTags()) {
//            System.out.println("Dir Name " + tag.getDirectoryName());
//            System.out.println("Tag Name " + tag.getTagName());
              System.out.println(tag);
          }

          //
          // Each Directory may also contain error messages
          //
          for (String error : directory.getErrors()) {
              System.err.println("ERROR: " + error);
          }
      }
  }
  
  public static void main(String[] args)
  {
    new PhotoTest();
  }
}
