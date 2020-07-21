package oeg.photo_merger.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;

import org.apache.logging.log4j.Logger;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import javax.swing.JSplitPane;

import oeg.photo_merger.utils.PhotoMergerUtils;

public class PropsViewer extends JFrame
{

  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private JPanel contentPane;
  private JPanel filesPanel;
  private FilesTableModel fileModel = null;
  private PropertiesTableModel propsModel = null;
  private JTable filesTable = null;
  private JTable propsTable = null;
  /* The object used to print messages to the screen */
  private Logger logger = null; 

  /**
   * Launch the application.
   */
  public static void main(String[] args)
  {
    EventQueue.invokeLater(new Runnable()
    {
      public void run()
      {
        try
        {
          PropsViewer frame = new PropsViewer(".");
          frame.setVisible(true);
        }
        catch (Exception e)
        {
          e.printStackTrace();
        }
      }
    });
  }

 
  
      
  /**
   * Create the frame.
   */
  public PropsViewer(String path)
  {
    this.logger = PhotoMergerUtils.getLogger(); 
    this.setMinimumSize(new Dimension(1000, 900));
    Vector<String> fHeader = new Vector<String>();
    fHeader.add("Files");
    Vector<Vector<String>> fData = new Vector<Vector<String>>();
    fData.add(new Vector<String>());
    
    this.fileModel = new FilesTableModel(fData, fHeader );
    this.filesTable = new JTable(this.fileModel);
    
    Vector<String> pHeader = new Vector<String>();
    pHeader.add("Property");
    pHeader.add("Value");
    Vector<Vector<String>> pData = new Vector<Vector<String>>();
    Vector<String> row = new Vector<String>();
    row.add("Prop");
    row.add("Val");
    pData.add(row);
    
    this.propsModel = new PropertiesTableModel(pData, pHeader);
    this.propsTable = new JTable(this.propsModel);
    
    SelectionListener listener = new SelectionListener(filesTable, propsModel);
    filesTable.getSelectionModel().addListSelectionListener(listener);
    filesTable.getColumnModel().getSelectionModel().addListSelectionListener(listener);
    
    
    setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    setBounds(100, 100, 894, 588);
    contentPane = new JPanel();
    contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
    contentPane.setLayout(new BorderLayout(0, 0));
    setContentPane(contentPane);
    
    JSplitPane splitPane = new JSplitPane();
    splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
    contentPane.add(splitPane, BorderLayout.CENTER);
    
    this.filesPanel = new JPanel();
    splitPane.setLeftComponent(this.filesPanel);
    this.filesPanel.setLayout(new BorderLayout(0, 0));
    
    JFileChooser fc = PhotoMergerUtils.getCustomFileChooser(path);
    
    this.filesPanel.add(fc, BorderLayout.WEST);
    
    JScrollPane filesScrollPane = new JScrollPane(this.filesTable);
    this.filesPanel.add(filesScrollPane, BorderLayout.EAST);
    
    
    fc.addPropertyChangeListener(new PropertyChangeListener() 
    {
      
      public void propertyChange(PropertyChangeEvent evt) 
      {
        if (JFileChooser.SELECTED_FILE_CHANGED_PROPERTY
                .equals(evt.getPropertyName())) 
        {
          File newFile = (File)evt.getNewValue();
          if( newFile != null )
            setFiles(newFile.getAbsolutePath());
          else
            logger.debug("The Selected path is null");

        } 
      }
    }) ;
    
    
  // Create a table with 10 rows and 5 columns
  JScrollPane scrollPane = new JScrollPane(this.propsTable);
  splitPane.setRightComponent(scrollPane);
  }
  
  
  public Vector<String> setFiles( String path )
  {
    Vector<String> files = new Vector<String>();
    
    int rowCount = this.fileModel.getRowCount();
    logger.trace("Removing " + rowCount);
    //Remove rows one by one from the end of the table
    for (int i = rowCount - 1; i >= 0; i--) 
    {
      ((DefaultTableModel)this.filesTable.getModel()).removeRow(i);
    }
  
    try
    {
      File root = new File(path);
      if( root != null && root.isDirectory() )
      {
        File[] fnames = root.listFiles();
        for( File file: fnames )
        {
          String fname = file.getAbsolutePath();
          logger.trace("Testing file: " + fname);
          int end = fname.lastIndexOf('.');
          if ( end > 0 )
          {
            String ext = fname.substring(end + 1, fname.length() ).toUpperCase();
            logger.trace("Looking for extension: " + ext);
            // if the extension is part of the approved list, add it
            if( PhotoMergerUtils.EXTS.contains(ext) )
            {
              logger.debug("Adding file to list");
              files.add(fname);
            }
            else
              logger.trace("Skipping file: " + fname);
          }
        }
      }
    }
    catch( Exception e )
    {
      e.printStackTrace();
    }
    
    Object[] items = files.toArray();
    
    logger.info("Found " + files.size() + " Image files");
    
    for( Object name : items )
    {
      this.fileModel.add((String)name);
    }

    return files;
  }
  
  
  /**
   * Class used to populate the Properties Table with the selected image.  The
   * TableModel is the model associated with the Properties table, but the 
   * actual table is the files table
   * 
   * @author Oscar E. Ganteaume
   *
   */
  class SelectionListener implements ListSelectionListener 
  {
    public static final long serialVersionUID = 1L;
    /** The files table containing all the image files **/
    private JTable table = null;
    /** The model associated with the properties table **/
    private PropertiesTableModel model = null;
    
//    private int selected = -1;
    
    /**
     * Creates a customized SelectionListener object where based on the changes
     * on the given Files Table, it changes the contents of the Properties
     * Model using the given PropertiesTableModel
     * 
     * @param table The Files table used in the PropertiesViewer
     * @param model The Properties Model where the properties of the file is to
     *        be shown
     */
    SelectionListener(JTable table, PropertiesTableModel model) 
    {
      this.table = table;
      this.model = model;
      this.table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    /**
     * Reacts to a new file selection.  If a valid file has been selected it
     * loads all the properties into the table from the image file.
     * 
     * @param e contains all the properties of the new selection
     */
    public void valueChanged(ListSelectionEvent e) 
    {
      
      int selected = this.table.getSelectedRow();
      if( selected < 0 )
        return;
      
      String name = (String)this.table.getValueAt( selected, 0);
      logger.info("The Filename " + name);
      
      try
      {
        // First need to remove old ones
        int rowCount = this.model.getRowCount();
        logger.trace("Removing " + rowCount);
        //Remove rows one by one from the end of the table
        for (int i = rowCount - 1; i >= 0; i--) 
        {
          ((DefaultTableModel)this.model).removeRow(i);
        }
        
        
        File file = new File(name);
        if( file != null && file.isFile() )
        {
          logger.trace("Reading File " + name);
          Metadata metadata = ImageMetadataReader.readMetadata(file);
          
          for (Directory directory : metadata.getDirectories()) 
          {
            String dName = directory.getName();
            logger.trace("Adding Directory " + dName);
            this.model.add(dName, "");
            for (Tag tag : directory.getTags()) 
            {
              String tName = tag.getTagName();
              String val = tag.getDescription();
              logger.trace("Adding Tag: "+ tName + " = " + val);
              
              this.model.add(tName, val);
                
            }
          }
          this.model.fireTableDataChanged();
        }
      }
      catch( Exception ex)
      {
        ex.printStackTrace();
      }
    }
      
  }
}
