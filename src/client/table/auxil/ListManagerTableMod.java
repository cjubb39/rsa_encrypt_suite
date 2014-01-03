package client.table.auxil;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

/**
 * Custom TableModel for ListManager. Based on AbstractTable Model. Extends methods in
 * abstract and dynamic way
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 * @param <T>
 *          Type of data being wrapped. Should match table implementing this.
 */
public class ListManagerTableMod<T> extends AbstractTableModel {

	private static final long serialVersionUID = -1100372395245858473L;
	private ArrayList<T> data;
	private Field[] fields;
	private String[] fieldNames;

	/**
	 * Constructor.
	 * 
	 * @param data
	 *          Data to be displayed
	 */
	public ListManagerTableMod(ArrayList<T> data){
		this.data = data;
		this.updateFields();
	}

	/**
	 * Update field names based on type of data being wrapped. If this.data.size() == 0,
	 * nothing is done.
	 */
	private void updateFields(){
		if (this.data.size() > 0) {
			this.fields = data.get(0).getClass().getFields();
			this.fieldNames = new String[this.fields.length];

			for (int i = 0; i < this.fieldNames.length; i++) {
				this.fieldNames[i] = this.fields[i].getName();
				if (this.getColumnClass(i) == new Boolean(false).getClass()) {
					this.fieldNames[i] += "?";
				}
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnClass(int)
	 */
	@Override
	public Class<?> getColumnClass(int arg0){
		Class<?> toRet = new String().getClass();
		try {
			toRet = this.fields[arg0].get(this.data.get(0)).getClass();
		} catch (IndexOutOfBoundsException e) {} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return toRet;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	@Override
	public int getColumnCount(){
		return (this.fieldNames == null) ? 0 : this.fieldNames.length;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#getColumnName(int)
	 */
	@Override
	public String getColumnName(int arg0){
		return (this.fieldNames == null) ? null : this.fieldNames[arg0];
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	@Override
	public int getRowCount(){
		return this.data.size();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	@Override
	public Object getValueAt(int arg0, int arg1){
		Object toRet = null;
		try {
			toRet = this.fields[arg1].get(this.data.get(arg0));
		} catch (Exception e) {
			e.printStackTrace();
		}

		return toRet;
	}

	/**
	 * Only Boolean cells are editable
	 * 
	 * @see javax.swing.table.AbstractTableModel#isCellEditable(int, int)
	 */
	@Override
	public boolean isCellEditable(int arg0, int arg1){
		return this.getColumnClass(arg1) == Boolean.class;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.swing.table.AbstractTableModel#setValueAt(java.lang.Object, int, int)
	 */
	@Override
	public void setValueAt(Object value, int arg0, int arg1){
		if (isCellEditable(arg0, arg1)) {
			try {
				this.fields[arg1].set(this.data.get(arg0), value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
				return;
			} catch (IllegalAccessException e) {
				e.printStackTrace();
				return;
			}
		}
	}
}
