package client.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
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
import client.table.aux.ListManagerTableMod;

/**
 * Manage a list of data allowing it to be displayed as a dynamically updating table.
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 * @param <T>
 *          Type of TableData to be managed
 */
public abstract class ListManager<T extends TableData> implements ActionListener,
		ListSelectionListener, Serializable {

	private static final long serialVersionUID = -4760443860661592550L;
	private ArrayList<T> data;
	protected transient ListManagerTableMod<T> tableModel;

	private transient JButton viewAddButton, viewExitButton, deleteButton;
	private transient JFrame mainGUI;
	protected transient JTable dataTable;
	protected transient JScrollPane scrollpane;
	protected transient TableCellRenderer cellRenderer;

	/**
	 * Supports property change action communication
	 */
	protected PropertyChangeSupport changes = new PropertyChangeSupport(this);

	/**
	 * Constructor. Creates Manager for given List
	 * 
	 * @param data
	 */
	public ListManager(ArrayList<T> data){
		this.data = data;
		this.tableModel = new ListManagerTableMod<T>(data);
		this.cellRenderer = new client.table.aux.TableCellRenderer();
	}

	/**
	 * Constructor. Creates manager for blank list of type T.
	 */
	public ListManager(){
		this(new ArrayList<T>());
	}

	/**
	 * Add data to managed list via GUI
	 * 
	 * @return Whole list being managed
	 */
	public abstract ArrayList<T> addOne();

	/**
	 * Add specified data to managed list
	 * 
	 * @param toAdd
	 *          Data to add to list
	 * @return Whole list being managed
	 */
	public abstract ArrayList<T> addOne(T toAdd);

	/*
	 * (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public abstract void actionPerformed(ActionEvent e);

	/*
	 * (non-Javadoc)
	 * @see
	 * javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent
	 * )
	 */
	public abstract void valueChanged(ListSelectionEvent arg0);

	/**
	 * Deletes data points with delete boolean marked true
	 * 
	 * @return Whole list being managed
	 */
	public ArrayList<T> deleteSelected(){
		for (int i = 0; i < this.data.size();) {
			if (this.data.get(i).getDelete()) {
				this.data.remove(i);
			} else {
				++i;
			}
		}
		this.resetTable();
		return this.data;
	}

	/**
	 * Delete given data point
	 * 
	 * @param toDelete
	 *          Data point to delete
	 * @return Whole list being managed
	 */
	public ArrayList<T> delete(T toDelete){
		this.data.remove(toDelete);
		this.resetTable();
		return this.data;
	}

	/**
	 * View all data points in formatted, dynamic table. Should have Add, Exit, Delete
	 * buttons if overridden.
	 * 
	 * @return Whole list being managed
	 */
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

		this.scrollpane = new JScrollPane(this.dataTable, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		this.mainGUI.add(this.scrollpane, BorderLayout.CENTER);

		this.resetColumnWidths();

		this.resetTable();

		this.mainGUI.setVisible(true);
		return this.data;
	}

	/**
	 * Reset the table: resets the TableModel and ColumnWidths
	 */
	public void resetTable(){
		this.dataTable.setModel(this.tableModel = new ListManagerTableMod<T>(this.data));
		this.resetColumnWidths();
	}

	/**
	 * Resets column widths. The last column has priority on space, though should not
	 * dominate other columns
	 */
	public void resetColumnWidths(){
		if (this.dataTable.getColumnCount() == 0) return;
		int colCount = this.dataTable.getColumnCount(), maxWidth = 0;
		for (int i = 0; i < colCount; i++) {
			TableColumn tc = this.dataTable.getColumnModel().getColumn(i);
			int width = 0;

			TableCellRenderer renderer = tc.getHeaderRenderer();
			if (renderer == null) renderer = this.dataTable.getTableHeader().getDefaultRenderer();
			Component comp = renderer.getTableCellRendererComponent(this.dataTable, tc.getHeaderValue(),
					false, false, 0, 0);
			width = comp.getPreferredSize().width;

			for (int row = 0; row < this.dataTable.getRowCount(); row++) {
				renderer = this.dataTable.getCellRenderer(row, i);
				comp = this.dataTable.prepareRenderer(renderer, row, i);
				width = Math.max(comp.getPreferredSize().width, width);
			}
			if (i != colCount - 1) maxWidth = (width > maxWidth) ? width : maxWidth;

			// set width
			tc.setPreferredWidth(width);
			tc.setMinWidth(width + 2);
			tc.setMaxWidth(width * 2);
		}
		// set last column specially
		this.dataTable.getColumnModel().getColumn(colCount - 1).setMaxWidth(7 * maxWidth);
	}

	/**
	 * Return data being managed as string array (using their toString method)
	 * 
	 * @return String array representation of data
	 */
	public String[] getDataStringArray(){
		String[] toRet = new String[this.getData().size()];

		for (int i = 0; i < toRet.length; i++) {
			toRet[i] = this.getData().get(i).toString();
		}

		return toRet;
	}

	/**
	 * Adds PropertyChangeListener to this.changes
	 * 
	 * @param l
	 *          Listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener l){
		this.changes.addPropertyChangeListener(l);
	}

	/**
	 * Removes PropertyChangeListener from this.changes
	 * 
	 * @param l
	 *          Listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener l){
		this.changes.removePropertyChangeListener(l);
	}

	/**
	 * @return Size of data
	 */
	public int size(){
		return this.data.size();
	}

	/**
	 * @return Data being managed
	 */
	public ArrayList<T> getData(){
		return this.data;
	}

	/**
	 * @return Exit Button of viewAll GUI
	 */
	public JButton getViewExitButton(){
		return this.viewExitButton;
	}

	/**
	 * @return Add Button of viewAll GUI
	 */
	public JButton getViewAddButton(){
		return this.viewAddButton;
	}

	/**
	 * @return MainGUI
	 */
	public JFrame getGUI(){
		return this.mainGUI;
	}

	/**
	 * @return Delete Button of viewAll GUI
	 */
	public JButton getDeleteButton(){
		return this.deleteButton;
	}
}
