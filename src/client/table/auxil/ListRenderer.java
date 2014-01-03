package client.table.auxil;

import java.awt.Component;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;

/**
 * Custom Cell Renderer. Allows minor tweaking of default cell rendering Currently: Adds
 * border to cell to allow for easy custom spacing
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public class ListRenderer extends DefaultListCellRenderer {

	private static final long serialVersionUID = -1947920765547274746L;
	private final int topPad, leftPad, bottomPad, rightPad;

	/**
	 * Constructor. Requires padding for border it will add to cell.
	 * 
	 * @param topPad
	 *          Amount to pad on top
	 * @param leftPad
	 *          Amount to pad on left
	 * @param bottomPad
	 *          Amount to pad on bottom
	 * @param rightPad
	 *          Amount to pad on right
	 */
	public ListRenderer(int topPad, int leftPad, int bottomPad, int rightPad){
		this.topPad = topPad;
		this.leftPad = leftPad;
		this.bottomPad = bottomPad;
		this.rightPad = rightPad;
	}

	/**
	 * Functions as a call to super-method followed by the addition of a border
	 * 
	 * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList,
	 *      java.lang.Object, int, boolean, boolean)
	 */
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index,
			boolean isSelected, boolean hasFocus){

		// sketchy cast but it works
		JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index,
				isSelected, hasFocus);

		comp.setBorder(BorderFactory.createEmptyBorder(this.topPad, this.leftPad, this.bottomPad,
				this.rightPad));

		return comp;
	}

}
