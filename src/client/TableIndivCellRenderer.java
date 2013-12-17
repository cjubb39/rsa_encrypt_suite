package client;

import java.awt.Component;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

public class TableIndivCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -6150333009929762629L;
	public final static transient SimpleDateFormat f = new SimpleDateFormat("MMM dd HH:mm:ss z");
	
	public TableIndivCellRenderer(){
		super();
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
			int row, int column){
		if(value.getClass() == new Date().getClass()){
			value = TableIndivCellRenderer.f.format(value);
		}
		return super.getTableCellRendererComponent
				(table, value, isSelected, hasFocus, row, column);
	}
}
