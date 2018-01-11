package test;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sound.midi.MidiDevice.Info;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.imaging.jpeg.JpegSegmentMetadataReader;
import com.drew.lang.annotations.Nullable;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataReader;
import com.drew.metadata.StringValue;
import com.drew.metadata.exif.ExifReader;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.iptc.IptcReader;
import com.drew.metadata.mov.QuickTimeDirectory;
import com.drew.metadata.mov.media.QuickTimeMediaDirectory;
import com.drew.metadata.mov.metadata.QuickTimeMetadataDirectory;
import com.drew.metadata.mp4.Mp4Directory;

import oeg.photo_merger.main.PhotoMerger;
import oeg.photo_merger.utils.ExifTool;
import oeg.photo_merger.utils.ExifTool.Feature;
import oeg.photo_merger.utils.ExifTool.Tag;
import oeg.photo_merger.utils.PhotoItem;
import oeg.photo_merger.utils.PhotoMergerHandler;
import oeg.photo_merger.utils.PhotoMergerUtils;


public class PhotoTest extends JFrame implements ActionListener
{
  private Logger logger = Logger.getLogger(PhotoTest.class.getName());
  SimpleDateFormat qtFormatter = 
      new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy");
  
  private JTextArea dbgTextArea;
  private JButton button;
  
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
    super("Events Example");
    
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

  private void runTest()
  {
    this.logger.info("Running test");
    long n1 = 2076927;
    long n2 = 2079596;
    long avg = (n1 + n2 ) / 2;
    this.logger.info("The average " + avg);
    double val = ( PhotoMerger.FILESIZE_PERCENT_DIFF * avg ) / 100;
    this.logger.info("The tolerance " + val);
    long diff = Math.abs( (n1 - n2) );
    this.logger.info("The difference " + diff);
    if( diff <= val )
      this.logger.info("They would be the same");
    else
      this.logger.info("They would have been different");
  }
  
  private void guiTest() throws Exception 
  {
    this.logger.fine("Running the PhotoTest");
    setLayout(new BorderLayout());
    // creates the text area to display logger messages
    this.dbgTextArea = new JTextArea();
    dbgTextArea.setBackground(Color.LIGHT_GRAY);
    dbgTextArea.setEditable(false);
    dbgTextArea.setBounds(421, 5, 4, 22);
    PhotoMergerHandler handler = new PhotoMergerHandler(dbgTextArea);
    this.logger = PhotoMergerUtils.getLogger(handler);
    this.logger.info("This is a test");
    this.button = new JButton("Click Me");
    this.button.addActionListener(this);
    
    add(this.dbgTextArea, BorderLayout.CENTER);
    add(this.button, BorderLayout.SOUTH);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    setSize(800, 600);
    setVisible(true);
    this.logger.info("Launching Thread");
    Thread t = new Thread( new Worker(this.logger, this));
    t.start();
    this.logger.info("Done With it");
  }
  public void sayBoo()
  {
    this.logger.fine("Boo!!");
  }
  
  public void actionPerformed( ActionEvent evt )
  {
    this.logger.info("Event " + evt.getActionCommand());
  }
  
  public class Worker implements Runnable
  {
    private Logger logger = null;
    private boolean done = false;
    private PhotoTest test = null;
    
    public Worker( Logger logger, PhotoTest test )
    {
      this.logger = logger;
      this.test = test;
    }
    
    public void run()
    {
      try
      {
        while( !this.done )
        {
          this.logger.info("Writing Data at: " + new Date() );
          Thread.sleep(1000);
          this.test.sayBoo();
        }
      }
      catch( Exception e )
      {
        this.logger.severe(e.getMessage());
      }
    }
    
    public void stopMe()
    {
      this.done = true;
    }
  }
  
  
  public static void main(String[] args)
  {
    new PhotoTest();
  }
}
