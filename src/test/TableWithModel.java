package test;

import java.awt.*;
import java.util.*;

import javax.swing.*;
import javax.swing.table.*;

public class TableWithModel extends JFrame {

  // the Model in instance variable so we can access it
  MyModel model;
  
  // constructor that will display a JTable based on elements received as arguments
  TableWithModel(Object[][] obj, String[] header) {
    super("Static JTable example");
    
    // JPanel to horl the JTable
    JPanel panel = new JPanel(new BorderLayout());
    // constructor of JTable model
    model = new MyModel(obj, header);
    // the table from that model
    JTable table = new JTable(model);
    panel.add(new JScrollPane(table));
    add(panel);    // adding panel to frame
    // and display it
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    setVisible(true);
    pack();
  }
  
  // to run the whole thing
  public static void main(String[] args) {
    // defines rows and column of the JTable
    String[][] rowAndColumn = {
        {"Dog", "Mammal"},
        {"Cat", "Mammal"},
        {"Shark", "Fish"},
        {"Parrots", "Bird"}
    };
    // defines the header
    String[] header = {"Animal", "Family"};
    // build the GUI
    TableWithModel twm = new TableWithModel(rowAndColumn, header);
    // now we will add elements.  We could imageie deleting elements or modifying
    // them. But for simplicity lets just add
    Scanner in = new Scanner(System.in);
    // prompt for animal and family until empty line
    while(true) {
      System.out.print("Enter animal name and family: ");
      String line = in.nextLine();
      // test if empty line
      if(line.length() == 0)
        break;
      String[] token = line.split(" ");
      // if not 2 strings exit
      if(token.length != 2)
        continue;
      // add the 2 Strings based throw the model
      twm.model.add(token[0], token[1]);
      twm.repaint();   // this is the laziest way but it works
    }
  }
  
  // class that extends the AbstractTableModel
  class MyModel extends AbstractTableModel {

    // to store our elements it will be great to avoid parallel array and use 
    // an ArrayList<Animal> but for simplicity and not to have to add a new 
    // class with will use an ArrayList<Object> for each row
    ArrayList<Object[]> al;
    // the headers
    String[] header;
    
    // constructor 
    MyModel(Object[][] obj, String[] header) {
      // save the header
      this.header = header; 
      // and the rows
      al = new ArrayList<Object[]>();
      // copy the rows into the ArrayList
      for(int i = 0; i < obj.length; ++i)
        al.add(obj[i]);
    }
    // method that needs to be overload. The row count is the size of the ArrayList
    public int getRowCount() {
      return al.size();
    }

    // method that needs to be overload. The column count is the size of our header
    public int getColumnCount() {
      return header.length;
    }

    // method that needs to be overload. The object is in the arrayList at rowIndex
    public Object getValueAt(int rowIndex, int columnIndex) {
      return al.get(rowIndex)[columnIndex];
    }
    
    // a method to return the column name 
    public String getColumnName(int index) {
      return header[index];
    }
    
    // a method to add a new line to the table
    void add(String animal, String family) {
      // make it an array[2] as this is the way it is stored in the ArrayList
      // (not best design but we want simplicity)
      String[] str = new String[2];
      str[0] = animal;
      str[1] = family;
      al.add(str);
      // inform the GUI that I have change
      fireTableDataChanged();
    }
  }
}

