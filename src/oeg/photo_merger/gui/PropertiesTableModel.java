package oeg.photo_merger.gui;

import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.swing.table.DefaultTableModel;

import oeg.photo_merger.utils.PhotoMergerUtils;

public class PropertiesTableModel extends DefaultTableModel
{

  private static final long serialVersionUID = 1L;

    // to store our elements it will be great to avoid parallel array and use 
    // an ArrayList<Animal> but for simplicity and not to have to add a new 
    // class with will use an ArrayList<Object> for each row
    private Vector<Vector<String>> names = new Vector<Vector<String>>();
    
    // the headers
    private Vector<String> header;
    
    private static Logger logger = null;
    
    // constructor 
    public PropertiesTableModel(Handler handler, Vector<Vector<String>> data, Vector<String> header) 
    {
      super(data, header);
      logger = PhotoMergerUtils.getLogger(handler);
      
      // save the header
      this.header = header; 
      this.names = data;
    }
    
    // method that needs to be overload. The row count is the size of the ArrayList
    public int getRowCount() 
    {
      if( names == null )
        return 0;
      else
        return names.size();
    }

    // method that needs to be overload. The column count is the size of our header
    public int getColumnCount() {
      return header.size();
    }

    // method that needs to be overload. The object is in the arrayList at rowIndex
    public Object getValueAt(int rowIndex, int colIndex) 
    {
      //String val = names.get(rowIndex).get(colIndex);
      //logger.finest("Value returned: " + val);
      return names.get(rowIndex).get(colIndex);
    }
    
    // a method to return the column name 
    public String getColumnName(int index) 
    {
      return header.get(index);
    }
    
    // a method to add a new line to the table
    void add(String key, String value) 
    {
      Vector<String> v = new Vector<String>();
      v.add(key);
      v.add(value);
      
      names.add(v);
      // inform the GUI that I have change
      //fireTableDataChanged();
    }
    
    public void fireTableDataChanged()
    {
      super.fireTableDataChanged();
      logger.info("Table changed");
    }
  }
