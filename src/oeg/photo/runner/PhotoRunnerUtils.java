package oeg.photo.runner;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import oeg.photo_merger.utils.PhotoMergerUtils;

public class PhotoRunnerUtils
{

  /**
   * Stores all the options that can be used by this application
   */
  private static Options options = new Options();
  
  
  public static Map<String, String> parseCommandLineArgs(String[] args)
  {
    Map<String, String> map = new HashMap<>();
    map = new HashMap<>();
    map.put("input-dir", null); 
    map.put("output-dir", null); 
    map.put("merge-dir", null);
    map.put("start-index", Integer.toString(1) ); 
    map.put("prefix", PhotoMergerUtils.PREFIX);
    map.put("use-last-mod-date", Boolean.toString(false));
    map.put("rem-dups", Boolean.toString(true));
    map.put("make-dirs", Boolean.toString(false));
    map.put("debug-level", "INFO");
    
    String configFile = "";
    
    /**
     * CLI Options
     */
    Option help = new Option("h", "help", false, "prints this message");
    
    Option in = new Option( "i", "input-dir", true, 
        "The path to the folder with the pictures");
    
    Option md = new Option("m", "merge-dir", true, 
        "The path to the directory containing the pictures to merge");
    Option out = new Option("o", "output-dir", true, 
        "The path of the directory to store merged files");
    Option config = new Option("c", "config-file", true, 
        "Optional configuration file");;
    Option indx = new Option("s", "start-index", true, 
        "The initial value to start the file numbering. Default: 1");
    Option pfx = new Option("p", "prefix", true, 
        "The prefix to use when setting the file name. Default: IMG");
    Option verbosity = new Option("v", "verbosity", true, 
        "Verbosity level: [SEVERE|WARNING|INFO|FINE|FINER|FINEST]");
    Option useLastMod = new Option("l", "use-last-mod-date", false, 
        "If set, uses the last date the file was modififed if the create time cannnot be found");
    Option remDups = new Option("r", "remove-dups", false, 
        "If set, removes duplicates found");
    Option makeDirs = new Option("M", "make-dirs", false, 
        "If set, creates directories as needed using yyyy-mm as a pattern");
    
  
    PhotoRunnerUtils.options.addOption(help);
    PhotoRunnerUtils.options.addOption(in);
    PhotoRunnerUtils.options.addOption(md);
    PhotoRunnerUtils.options.addOption(out);
    PhotoRunnerUtils.options.addOption(config);
    PhotoRunnerUtils.options.addOption(indx);
    PhotoRunnerUtils.options.addOption(pfx);
    PhotoRunnerUtils.options.addOption(useLastMod);
    PhotoRunnerUtils.options.addOption(remDups);
    PhotoRunnerUtils.options.addOption(makeDirs);
    PhotoRunnerUtils.options.addOption(verbosity);
    
  
    /**
     * CLI Parser
     */
    CommandLineParser parser = new DefaultParser();
    try
    {
      CommandLine line = parser.parse(options, args);
      if (line.hasOption("h"))
      {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("MainPhotoMerger", options);
      } 
      else
      {
        /**
         * if there is a config file, it will override
         * the defaults in this file using it's values.
         * the command line options will override the
         * values from the config file
         */
        if (line.hasOption("c"))
        {
          configFile = line.getOptionValue("c");
          Properties props = new Properties();
          FileInputStream fis = null;
  
          try
          {
            fis = new FileInputStream(configFile);
            props.load(fis);
            fis.close();
          } catch (IOException e)
          {
            e.printStackTrace();
          }
  
          if (props.getProperty("input-dir") != null)
            map.put("input-dir", props.getProperty("input-dir") );
  
          if (props.getProperty("merge-dir") != null)
            map.put("merge-dir", props.getProperty("merge-dir") );
  
          if (props.getProperty("output-dir") != null)
            map.put("output-dir", props.getProperty("output-dir") );
          
          if (props.getProperty("prefix") != null)
            map.put("prefix", props.getProperty("prefix") );
          
          if (props.getProperty("start-index") != null)
            map.put("start-index", props.getProperty("start-index") );
          
          if (props.getProperty("use-last-mod-date") != null)
            map.put("use-last-mod-date", props.getProperty("use-last-mod-date") );
          
          if (props.getProperty("remove-dups") != null)
            map.put("remove-dups", props.getProperty("remove-dups") );
          
          if (props.getProperty("make-dirs") != null)
            map.put("make-dirs", props.getProperty("make-dirs") );
          
          
          if (props.getProperty("verbosity") != null)
          {
            String tmp =  props.getProperty("verbosity");
            switch(tmp)
            {
              case "TRACE":
              case "DEBUG":
              case "INFO":
              case "WARNING":
              case "ERROR":
              case "FATAL":
                map.put("debug-level", tmp);
                break;
              default:
                System.err.println("Invalid verbosity option: " + tmp);
                System.exit(-2);
            }
          }
        }
  
        if (line.hasOption("i"))
          map.put("input-dir", line.getOptionValue("i") );
  
        if (line.hasOption("m"))
          map.put("merge-dir", line.getOptionValue("m") );
  
        if (line.hasOption("o"))
          map.put("output-dir", line.getOptionValue("o") );
        
        if (line.hasOption("s"))
          map.put("start-index", line.getOptionValue("s") );
        
        if (line.hasOption("p"))
          map.put("prefix", line.getOptionValue("p") );
        
        if (line.hasOption("l"))
          map.put("use-last-mod-date", line.getOptionValue("l") );
        
        if (line.hasOption("r"))
          map.put("remove-dups", line.getOptionValue("r") );
        
        if (line.hasOption("M"))
          map.put("make-dirs", line.getOptionValue("M") );
        
        if (line.hasOption("v"))
        {
          String tmp = line.getOptionValue("v");
          switch(tmp)
          {
            case "TRACE":
            case "DEBUG":
            case "INFO":
            case "WARNING":
            case "ERROR":
            case "FATAL":
              map.put("debug-level", tmp);
              break;
            default:
              System.err.println("Invalid verbosity option: " + tmp);
              System.exit(-2);
          }
        }
      }
    } 
    catch (ParseException exp)
    {
      // oops, something went wrong
      System.err.println("Parsing failed. Reason: " + exp.getMessage());
    }
    
    return map;
  }
  
  
  public static FileFilter getFileFilter() {
    
    class FilenameFilter implements FileFilter {
      @Override
      public boolean accept(File dir) {
        String name = dir.getName();
        if( name.equals("@eaDir") || name.startsWith(".") ) {
          return false;
        }
        return true;
      }
      
    };
    
    return new FilenameFilter();
  }
}
