package test;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import oeg.photo.runner.PhotoRunner;
import oeg.photo.runner.PhotoRunnerUtils;
import oeg.photo_merger.utils.PhotoMergerUtils;

public class QuickTest
{

  /**
   * Generates debug print statements based on the verbosity level.
   */
  private Logger logger = null;
  
  public QuickTest()
  {
    this.logger = PhotoMergerUtils.getLogger(Level.DEBUG);
    
    this.logger.debug("Running Quick Test");
    List<List<String>> dups = new ArrayList<>();
    
    List<String> m1 = new ArrayList<>();
    m1.add("/mnt/root/Pictures/2020/2020-02/DSC_0228.JPG");
    m1.add("/mnt/root/Pictures/2020/2020-02/DSC_0229.JPG");
    List<String> m2 = new ArrayList<>();
    m2.add("/mnt/root/Pictures/2020/2020-02/DSC_0228.JPG");
    m2.add("/mnt/root/Pictures/2020/2020-02/DSC_0230.JPG");
    List<String> m3 = new ArrayList<>();
    m3.add("/mnt/root/Pictures/2020/2020-02/DSC_0240.JPG");
    m3.add("/mnt/root/Pictures/2020/2020-02/DSC_0239.JPG");
    List<String> m4 = new ArrayList<>();
    m4.add("/mnt/root/Pictures/2020/2020-02/DSC_0241.JPG");
    m4.add("/mnt/root/Pictures/2020/2020-02/DSC_0242.JPG");
    List<String> m5 = new ArrayList<>();
    m5.add("/mnt/root/Pictures/2020/2020-02/DSC_0248.JPG");
    m5.add("/mnt/root/Pictures/2020/2020-02/DSC_0249.JPG");
    List<String> m6 = new ArrayList<>();
    m6.add("/mnt/root/Pictures/2020/2020-02/DSC_0248.JPG");
    m6.add("/mnt/root/Pictures/2020/2020-02/DSC_0247.JPG");
    List<String> m7 = new ArrayList<>();
    m7.add("/mnt/root/Pictures/2020/2020-02/DSC_0252.JPG");
    m7.add("/mnt/root/Pictures/2020/2020-02/DSC_0253.JPG");
    List<String> m8 = new ArrayList<>();
    m8.add("/mnt/root/Pictures/2020/2020-02/DSC_0274.JPG");
    m8.add("/mnt/root/Pictures/2020/2020-02/DSC_0275.JPG");
    List<String> m9 = new ArrayList<>();
    m9.add("/mnt/root/Pictures/2020/2020-02/DSC_0274.JPG");
    m9.add("/mnt/root/Pictures/2020/2020-02/DSC_0273.JPG");
    List<String> m10 = new ArrayList<>();
    m10.add("/mnt/root/Pictures/2020/2020-02/DSC_0274.JPG");
    m10.add("/mnt/root/Pictures/2020/2020-02/DSC_0276.JPG");
    List<String> m11 = new ArrayList<>();
    m11.add("/mnt/root/Pictures/2020/2020-02/DSC_0289.JPG");
    m11.add("/mnt/root/Pictures/2020/2020-02/DSC_0288.JPG");
    List<String> m12 = new ArrayList<>();
    m12.add("/mnt/root/Pictures/2020/2020-02/DSC_0290.JPG");
    m12.add("/mnt/root/Pictures/2020/2020-02/DSC_0291.JPG");
    List<String> m13 = new ArrayList<>();
    m13.add("/mnt/root/Pictures/2020/2020-02/DSC_0296.JPG");
    m13.add("/mnt/root/Pictures/2020/2020-02/DSC_0294.JPG");
    List<String> m14 = new ArrayList<>();
    m14.add("/mnt/root/Pictures/2020/2020-02/DSC_0299.JPG");
    m14.add("/mnt/root/Pictures/2020/2020-02/DSC_0300.JPG");
    List<String> m15 = new ArrayList<>();
    m15.add("/mnt/root/Pictures/2020/2020-02/DSC_0308.JPG");
    m15.add("/mnt/root/Pictures/2020/2020-02/DSC_0307.JPG");
    List<String> m16 = new ArrayList<>();
    m16.add("/mnt/root/Pictures/2020/2020-02/DSC_0328.JPG");
    m16.add("/mnt/root/Pictures/2020/2020-02/DSC_0329.JPG");
    List<String> m17 = new ArrayList<>();
    m17.add("/mnt/root/Pictures/2020/2020-02/DSC_0339.JPG");
    m17.add("/mnt/root/Pictures/2020/2020-02/DSC_0337.JPG");
    List<String> m18 = new ArrayList<>();
    m18.add("/mnt/root/Pictures/2020/2020-02/DSC_0347.JPG");
    m18.add("/mnt/root/Pictures/2020/2020-02/DSC_0348.JPG");
    List<String> m19 = new ArrayList<>();
    m19.add("/mnt/root/Pictures/2020/2020-02/DSC_0350.JPG");
    m19.add("/mnt/root/Pictures/2020/2020-02/DSC_0349.JPG");
    List<String> m20 = new ArrayList<>();
    m20.add("/mnt/root/Pictures/2020/2020-02/DSC_0356.JPG");
    m20.add("/mnt/root/Pictures/2020/2020-02/DSC_0355.JPG");
    List<String> m21 = new ArrayList<>();
    m21.add("/mnt/root/Pictures/2020/2020-02/DSC_0359.JPG");
    m21.add("/mnt/root/Pictures/2020/2020-02/DSC_0358.JPG");
    
    dups.add(m19);
    dups.add(m1);
    dups.add(m15);
    dups.add(m7);
    dups.add(m4);
    dups.add(m17);
    dups.add(m6);
    dups.add(m20);
    dups.add(m8);
    dups.add(m10);
    dups.add(m21);
    dups.add(m12);
    dups.add(m3);
    dups.add(m13);
    dups.add(m16);
    dups.add(m5);
    dups.add(m18);
    dups.add(m9);
    dups.add(m11);
    dups.add(m2);
    dups.add(m14);
    
    
    List<String> empty = new ArrayList<>();
    dups.add(empty);
    List<String> bad = null;
    dups.add(bad);
//    this.logger.debug("********  Unsorted  *********");
//    for(int i=0; i < dups.size(); i++ )
//      this.logger.debug(dups.get(i));
    
    Collections.sort(dups, new SortByFirstElement() );

//    this.logger.debug("********  Sorted  *********");
//    for(int i=0; i < dups.size(); i++ )
//      this.logger.debug(dups.get(i));

    //    String fname = "/mnt/data/oeg/Pictures/2020";
//    File file = new File(fname);
//    this.logger.debug("The name " + file.getAbsolutePath());
//    String full = file.getAbsolutePath() + File.separator + "new_file";
//    this.logger.debug("The name " + full );
    
    
  }
  
  /**
   * Sorts two lists of strings based only on the first element.
   * 
   * @author Oscar E. Ganteaume
   *
   */
  class SortByFirstElement implements Comparator<List<String>>
  {
    /**
     * Compares two lists and returns:
     *   1: if the first element in a is greater than the first element in b
     *   0: if the first element in a is equals to the first element in b
     *  -1: if the first element in a is less than the first element in b
     *  
     * @return an integer determining if whether or not the first element in a
     *         is the same, greater or equal to the first element in b
     */
    public int compare(List<String> a, List<String> b)
    {
      // lets first make sure we account for null lists
      if( a != null && b == null ) {
        System.out.println("A is not null, B is null");
        return 1;
      }
      else if(a == null && b == null ) {
        System.out.println("A and B are null");
        return 0;      
      }
      else if( a == null && b != null ) {
        System.out.println("A is null, B is not null");
        return -1;
      }
      
      // now let's consider empty lists
      if( !a.isEmpty() && b.isEmpty() ) {
        System.out.println("A is not empty, B is empty");
        return 1;
      }
      else if(a.isEmpty() && b.isEmpty() ) {
        System.out.println("Both A and B are empty");
        return 0;      
      }
      else if( a.isEmpty() && !b.isEmpty() ) {
        System.out.println("A is empty, B is not empty");
        return -1;
      }
      
      return a.get(0).compareTo(b.get(0));
    }
  }
 
  public static void main(String[] args)
  {
    new QuickTest();
  }
}


