package oeg.photo.runner;

import java.io.File;
import java.io.FileFilter;

import oeg.photo_merger.utils.PhotoMergerUtils;

public class PhotoRunnerUtils
{

  
  public static PhotoMergerArgs parseCommandLineArgs(String[] args)
  {
    return PhotoMergerUtils.parseCommandLineArgs(args);
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
