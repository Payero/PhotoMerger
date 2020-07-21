package oeg.photo_merger.main;

import java.io.File;
import java.util.Map;

import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

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
   * Runs the application using all the arguments given by the user.  If all
   * the arguments are set properly it runs the command line.
   * 
   * @param inputDir The name of the directory containing all the files to 
   *        rename or merge
   * @param outDir The name of the directory to place the files after renaming 
   *        or merging them
   * @param mergeDir The name of the directory containing all the files to 
   *        merge
   * @param startIndx The index number used when generating the name of the 
   *        files
   * @param prefix The prefix used when generating the name of the files
   * @param lvl The verbosity level or amount of messages print to the screen
   */
  public MainPhotoMerger(String inputDir, String outDir, String mergeDir, 
                         int startIndx, String prefix, String lvl)
  {
    String name = MainPhotoMerger.class.getName();
    if( lvl != null)
      logger = PhotoMergerUtils.getLogger(name, Level.getLevel(lvl) );
    else
      logger = PhotoMergerUtils.getLogger(name);
    
    // Making sure the inputDirectory is valid
    if( inputDir == null )
      inputDir = System.getProperty("user.dir");
    
    File f = new File(inputDir);
    try
    {
      if ( !f.isDirectory() )
      {
        System.err.println("The input directory is invalid");
        System.exit(-1);
      }
    }
    catch( Exception e )
    {
      throw new RuntimeException(e);
    }
    
    // checking for the output directory
    if( outDir == null )
      outDir = inputDir;
    else
    {
      f = new File(outDir);
      try
      {
        if ( !f.isDirectory() )
        {
          System.err.println("The output directory is invalid");
          System.exit(-1);
        }
      }
      catch( Exception e )
      {
        throw new RuntimeException(e);
      }
    }
    // making sure we have a valid start index
    if( startIndx < 0 )
    {
      System.err.println("The start index needs to be a positive number");
      System.exit(-1);
    }
    // making sure we have a valid prefix
    if( prefix == null )
    {
      System.err.println("The prefix is invalid");
      System.exit(-1);
    }
    new PhotoMerger(inputDir, outDir, mergeDir, startIndx, prefix);
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
    Map<String, String> map = PhotoMergerUtils.parseCommandLineArgs(args);
    
    new MainPhotoMerger( map.get("input-dir"), map.get("output-dir"), 
                         map.get("merge-dir"), 
                         Integer.parseInt(map.get("start-index")), 
                         map.get("prefix"), map.get("debug-level") );
  }
}
