package client.table.auxil;

import java.awt.Component;
import java.text.SimpleDateFormat;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * Custom table cell renderer. Allows for custom tweaking of default table cell renderer.
 * Currently: Formats cells containing java.util.Date objects differently
 * 
 * @author Chae Jubb
 * @version 1.0
 */
public class TableCellRenderer extends DefaultTableCellRenderer {

	private static final long serialVersionUID = -6150333009929762629L;

	/**
	 * Specifies formatting of date in table layout
	 */
	public final static transient SimpleDateFormat f = new SimpleDateFormat("MMM dd HH:mm");

	/**
	 * Constructor is sipmly a call to super-constructor
	 */
	public TableCellRenderer(){
		super();
	}

	/**
	 * Functions as call to super-method followed by tweaking if cell has type of
	 * java.util.Date
	 * 
	 * @see javax.swing.table.DefaultTableCellRenderer#getTableCellRendererComponent(javax.swing.JTable,
	 *      java.lang.Object, boolean, boolean, int, int)
	 */
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
			boolean hasFocus, int row, int column){
		if (value instanceof java.util.Date) {
			value = TableCellRenderer.f.format(value);
		}
		return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
	}
}
