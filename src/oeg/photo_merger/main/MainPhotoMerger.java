package oeg.photo_merger.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

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
    if( lvl != null)
      logger = PhotoMergerUtils.getLogger(Level.parse(lvl), new ConsoleHandler() );
    else
      logger = PhotoMergerUtils.getLogger(new ConsoleHandler());
    
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
  private static void usage(String msg) 
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
//    String configFile = "";
//    String lvl = "INFO";
//    String inputDir = null;
//    String mergeDir = null;
//    String outputDir = null;
//    int startIndx = 1;
//    String prefix = PhotoMergerUtils.PREFIX;
//    
//    /**
//     * CLI Options
//     */
//    Option help = new Option("h", "help", false, "prints this message");
//    
//    Option in = new Option( "i", "input-dir", true, 
//        "The path to the folder with the pictures");
//    
//    Option md = new Option("m", "merge-dir", true, 
//        "The path to the directory containing the pictures to merge");
//    Option out = new Option("o", "output-dir", true, 
//        "The path of the directory to store merged files");
//    Option config = new Option("c", "config-file", true, 
//        "Optional configuration file");;
//    Option indx = new Option("s", "start-index", true, 
//        "The initial value to start the file numbering. Default: 1");
//    Option pfx = new Option("p", "prefix", true, 
//        "The prefix to use when setting the file name. Default: IMG");
//    Option verbosity = new Option("v", "verbosity", true, 
//        "Verbosity level: [SEVERE|WARNING|INFO|FINE|FINER|FINEST]");
//
//
//    MainPhotoMerger.options.addOption(help);
//    MainPhotoMerger.options.addOption(in);
//    MainPhotoMerger.options.addOption(md);
//    MainPhotoMerger.options.addOption(out);
//    MainPhotoMerger.options.addOption(config);
//    MainPhotoMerger.options.addOption(indx);
//    MainPhotoMerger.options.addOption(pfx);
//    MainPhotoMerger.options.addOption(verbosity);
//    
//
//    /**
//     * CLI Parser
//     */
//    CommandLineParser parser = new DefaultParser();
//    try
//    {
//      CommandLine line = parser.parse(options, args);
//      if (line.hasOption("h"))
//      {
//        HelpFormatter formatter = new HelpFormatter();
//        formatter.printHelp("MainPhotoMerger", options);
//      } 
//      else
//      {
//        /**
//         * if there is a config file, it will override
//         * the defaults in this file using it's values.
//         * the command line options will override the
//         * values from the config file
//         */
//        if (line.hasOption("c"))
//        {
//          configFile = line.getOptionValue("c");
//          Properties props = new Properties();
//          FileInputStream fis = null;
//
//          try
//          {
//            fis = new FileInputStream(configFile);
//            props.load(fis);
//            fis.close();
//          } catch (IOException e)
//          {
//            e.printStackTrace();
//          }
//
//          if (props.getProperty("input-dir") != null)
//            inputDir = props.getProperty("input-dir");
//
//          if (props.getProperty("merge-dir") != null)
//            mergeDir = props.getProperty("merge-dir");
//
//          if (props.getProperty("output-dir") != null)
//            outputDir = props.getProperty("output-dir");
//          
//          if (props.getProperty("prefix") != null)
//            prefix = props.getProperty("prefix");
//          
//          if (props.getProperty("start-index") != null)
//            startIndx = Integer.parseInt(props.getProperty("start-index"));
//          
//          if (props.getProperty("verbosity") != null)
//          {
//            String tmp =  props.getProperty("verbosity");
//            switch(tmp)
//            {
//              case "SEVERE":
//              case "WARNING":
//              case "INFO":
//              case "FINE":
//              case "FINER":
//              case "FINEST":
//                lvl = tmp;
//                break;
//              default:
//                System.err.println("Invalid verbosity option: " + tmp);
//                System.exit(-2);
//            }
//          }
//        }
//
//        if (line.hasOption("i"))
//            inputDir = line.getOptionValue("i");
//
//        if (line.hasOption("m"))
//            mergeDir = line.getOptionValue("m");
//
//        if (line.hasOption("o"))
//            outputDir = line.getOptionValue("o");
//        
//        if (line.hasOption("s"))
//            startIndx = Integer.parseInt(line.getOptionValue("s"));
//        
//        if (line.hasOption("p"))
//            prefix = line.getOptionValue("p");
//        
//        
//        if (line.hasOption("v"))
//        {
//          String tmp = line.getOptionValue("v");
//          switch(tmp)
//          {
//            case "SEVERE":
//            case "WARNING":
//            case "INFO":
//            case "FINE":
//            case "FINER":
//            case "FINEST":
//              lvl = tmp;
//              break;
//            default:
//              System.err.println("Invalid verbosity option: " + tmp);
//              System.exit(-2);
//          }
//        }
//      }
//    } catch (ParseException exp)
//    {
//      // oops, something went wrong
//      System.err.println("Parsing failed. Reason: " + exp.getMessage());
//    }
    Map<String, String> map = PhotoMergerUtils.parseCommandLineArgs(args);
    
    new MainPhotoMerger( map.get("input-dir"), map.get("output-dir"), 
                         map.get("merge-dir"), 
                         Integer.parseInt(map.get("start-index")), 
                         map.get("prefix"), map.get("debug-level") );
  }
}
