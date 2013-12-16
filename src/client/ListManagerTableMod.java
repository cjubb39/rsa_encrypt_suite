package client;

import java.lang.reflect.Field;
import java.util.ArrayList;

import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;

public class ListManagerTableMod<T> implements TableModel {

	private ArrayList<T> data; 
	private Field[] fields;
	private String[] fieldNames;
	
	public ListManagerTableMod(ArrayList<T> data){
		this.data = data;
		this.updateFields();
	}
	
	public void updateFields(){
		if (this.data.size() > 0) {
System.err.println("Updating fields for " + this.data.get(0).getClass());
			this.fields = data.get(0).getClass().getFields();
			this.fieldNames = new String[this.fields.length];

			for (int i = 0; i < this.fieldNames.length; i++) {
				this.fieldNames[i] = this.fields[i].getName();
			}
		}
	}
	
	@Override
	public void addTableModelListener(TableModelListener arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public Class<?> getColumnClass(int arg0) {
		return (this.data.size() > 0) ? this.data.get(0).getClass() : this.data.getClass();
	}

	@Override
	public int getColumnCount() {
		return (this.fieldNames == null) ? 0 : this.fieldNames.length;
	}

	@Override
	public String getColumnName(int arg0) {
		return (this.fieldNames == null) ? null : this.fieldNames[arg0];
	}

	@Override
	public int getRowCount() {
		return this.data.size();
	}

	@Override
	public Object getValueAt(int arg0, int arg1) {
		Object toRet = null;
		try {
			toRet = this.fields[arg1].get(this.data.get(arg0));
		} catch (Exception e){
			e.printStackTrace();
		}
		
		return toRet;
	}

	@Override
	public boolean isCellEditable(int arg0, int arg1) {
		return false;
	}

	@Override
	public void removeTableModelListener(TableModelListener arg0) {
	}

	@Override
	public void setValueAt(Object arg0, int arg1, int arg2) {
		return;
	}

}
