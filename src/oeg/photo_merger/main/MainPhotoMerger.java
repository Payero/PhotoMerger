package oeg.photo_merger.main;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import oeg.photo.runner.PhotoMergerArgs;
import oeg.photo_merger.utils.PhotoMergerUtils;

/**
 * Runs the command line application to interface with the PhotoMerger.  It 
 * uses the Apache CLI to get the arguments from the user.  It validates all
 * the parameters and if they are valid it runs the actual rename/merging.
 * 
 * There are two ways of configuring this application; through command line 
 * arguments or through a configuration file.  If the argument -c or 
 * --config-file is used, then the application ignores all other command line
 * arguments otherwise it uses them.
 * 
 * @author Oscar E. Ganteaume
 *
 */
public class MainPhotoMerger
{
  /* Object used to print messages to the screen */
  public static Logger logger = null;
  /**
   * Parses and prints all the options or arguments used by this application
   */
  private static HelpFormatter formatter = new HelpFormatter();
  /**
   * Stores all the options that can be used by this application
   */
  private static Options options = new Options();
  /**
   * Stores the object doing the actual work
   */
  private PhotoMerger photoMerger = null;
  
  /**
   * Runs the application using all the arguments given by the user.  If all
   * the arguments are set properly it runs the command line.
   * 
   * @param args the object containing all the runtime configuration parameters
   * 
   */
  public MainPhotoMerger(PhotoMergerArgs args)
  {
    if( args.getVerbosityLevel() != null)
    {
      logger = PhotoMergerUtils.getLogger("MainPhotoMerger", 
          args.getVerbosityLevel() );
    }
    else
      logger = PhotoMergerUtils.getLogger("MainPhotoMerger");
    
    this.photoMerger = new PhotoMerger( args );
  }
  
  public void submitProcessRequest()
  {
    this.photoMerger.processRequest();
  }
  
  public void findDuplicatesOnly()
  {
    List<List<String>> dups = this.photoMerger.findDuplicates();
    PhotoMergerArgs args = this.photoMerger.getPhotoMergerArguments();
    String inDir = args.getInputDir();
    String outDir = args.getOutputDir();
    BufferedWriter writer = null;
    try
    {
      File inFile = new File(inDir);
      File outFile = new File(outDir);
      String name = inFile.getName();
      String full = outFile.getAbsolutePath() + 
          File.separator + name + "_duplicates.txt";
      logger.info("Creating duplicates file: " + full);
      writer = new BufferedWriter(new FileWriter(full) );
      for( List<String> dup : dups )
      {
        writer.write(String.format("%s\n", dup.toString() ) );
      }  
      logger.info("Done writing " + dups.size() + " duplicates");
      
    }
    catch( Exception e )
    {
      logger.error("Got an exception when writing duplicates: " + e.getMessage() );
    }
    finally
    {
      try
      {
        writer.close();
      }
      catch (Exception e2)
      {
        logger.warn("Got an exception when closing writer");
      }
      
      
    }
    
  }
  
  /**
   * Prints a message indicating how to use this framework and then quits
   * 
   * @param msg the message to display on the screen along with the usage
   */
  public static void usage(String msg) 
  {
    if( msg != null )
      System.err.println(msg);
    
    formatter.printHelp(MainPhotoMerger.class.toString(), options);
    System.exit(1);
  }

  
  /**
   * Gets all the arguments from the user and then runs the show
   * 
   * @param args
   */
  public static void main(String[] args)
  {
    List<String> clean_args = new ArrayList<>();
    boolean dups_only = false;
    for( String arg : args )
    {
      if( arg.equals("-F") || arg.equalsIgnoreCase("--find-duplicates-only") )
      {
        System.err.println("Asking to find duplicates only");
        dups_only = true;
      }
      else
      {
        clean_args.add(arg);
      }
    }
    
    PhotoMergerArgs photoArgs = 
        PhotoMergerUtils.parseCommandLineArgs(clean_args.toArray(new String[0] ));
    
    MainPhotoMerger main = new MainPhotoMerger( photoArgs );
    if( dups_only )
    {
      main.findDuplicatesOnly();
    }
    else
      main.submitProcessRequest();
  }
}
