package client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import shared.TableData;

public abstract class ListManager<T extends TableData> implements ActionListener, ListSelectionListener{
	
	private ArrayList<T> data;
	protected ListManagerTableMod<T> tableModel;
	
	private JButton viewAddButton, viewExitButton, deleteButton;
	private JFrame mainGUI;
	protected JTable dataTable;
	protected JScrollPane scrollpane;
	protected TableCellRenderer cellRenderer;
	
	public ListManager(ArrayList<T> data){
		this.data = data;
		this.tableModel = new ListManagerTableMod<T>(data);
		this.cellRenderer = new client.TableCellRenderer();
	}
	
	public abstract ArrayList<T> addOne();
	public abstract ArrayList<T> addOne(T toAdd);
	public abstract void actionPerformed(ActionEvent e);
	public abstract void valueChanged(ListSelectionEvent arg0);
	
	public ArrayList<T> deleteSelected(){
		for(int i = 0; i < this.data.size();){
			if (this.data.get(i).getDelete()){
				this.data.remove(i);
			} else {
				++i;
			}
		}
		this.resetTable();
		return this.data;
	}
	public ArrayList<T> delete(T toDelete){
		this.data.remove(toDelete);
		this.resetTable();
		return this.data;
	}
	
	public ArrayList<T> viewAll(){
		this.mainGUI = new JFrame("Address Book");
		this.mainGUI.setPreferredSize(new Dimension(350, 250));
		this.mainGUI.setSize(new Dimension(350, 250));
		this.mainGUI.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		this.mainGUI.getContentPane().setLayout(new BorderLayout(0, 0));
		
		JPanel controlButtons = new JPanel();
		controlButtons.setLayout(new FlowLayout());
		
		this.viewAddButton = new JButton("Add");
		this.viewAddButton.addActionListener(this);
		controlButtons.add(this.viewAddButton);
		
		this.deleteButton = new JButton("Delete Selected");
		this.deleteButton.addActionListener(this);
		controlButtons.add(this.deleteButton);
		
		this.viewExitButton = new JButton("Close");
		this.viewExitButton.addActionListener(this);
		controlButtons.add(this.viewExitButton);
		
		this.mainGUI.add(controlButtons, BorderLayout.SOUTH);
		
		this.dataTable = new JTable(this.tableModel);
		this.dataTable.setAutoCreateRowSorter(true);
		this.dataTable.getTableHeader().setReorderingAllowed(false);
		this.dataTable.setDefaultRenderer(new Date().getClass(), this.cellRenderer);
		//this.resetCellRenderers();
		
		this.scrollpane = new JScrollPane(this.dataTable, 
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		
		this.mainGUI.add(this.scrollpane, BorderLayout.CENTER);
		
		this.resetColumnWidths();
		
		this.resetTable();
		
		this.mainGUI.setVisible(true);
		return this.data;
	}

	public void resetTable(){
		this.dataTable.setModel(this.tableModel = new ListManagerTableMod<T>(this.data));
		//this.resetCellRenderers();
		this.resetColumnWidths();
	}
	
	public void resetCellRenderers(){
		/*Enumeration<TableColumn> en = this.dataTable.getColumnModel().getColumns();
		
		while(en.hasMoreElements()){
			en.nextElement().setCellRenderer(this.cellRenderer);
		}*/
	}
	
	public void resetColumnWidths(){
		if (this.dataTable.getColumnCount() == 0) return;
		int colCount = this.dataTable.getColumnCount(), maxWidth = 0;
		for (int i = 0; i < colCount; i++){
			TableColumn tc = this.dataTable.getColumnModel().getColumn(i);
			int width = 0;
			
			TableCellRenderer renderer = tc.getHeaderRenderer();
			if (renderer == null) renderer = this.dataTable.getTableHeader().getDefaultRenderer();
			Component comp = renderer.getTableCellRendererComponent(this.dataTable, 
					tc.getHeaderValue(), false, false, 0, 0);
			width = comp.getPreferredSize().width;

			for (int row = 0; row < this.dataTable.getRowCount(); row++){
				renderer = this.dataTable.getCellRenderer(row, i);
				comp = this.dataTable.prepareRenderer(renderer, row, i);
				width = Math.max(comp.getPreferredSize().width, width);
			}
			if (i != colCount - 1)
				maxWidth = (width > maxWidth) ? width : maxWidth;
			
			// set width
			tc.setPreferredWidth(width);
			tc.setMinWidth(width + 2);
			tc.setMaxWidth(width*2);
		}
		// set last column specially
		this.dataTable.getColumnModel().getColumn(colCount - 1).setMaxWidth(7*maxWidth);
	}
	
	public String[] getDataStringArray(){
		String[] toRet = new String[this.getData().size()];
		
		for(int i = 0; i < toRet.length; i++){
			toRet[i] = this.getData().get(i).toString();
		}
		
		return toRet;
	}
	
	public ArrayList<T> getData(){
		return this.data;
	}
	
	public JButton getViewExitButton(){
		return this.viewExitButton;
	}
	
	public JButton getViewAddButton(){
		return this.viewAddButton;
	}
	
	public JFrame getGUI(){
		return this.mainGUI;
	}
	
	public JButton getDeleteButton(){
		return this.deleteButton;
	}
}
