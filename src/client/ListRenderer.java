package client;

import java.awt.Component;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;

public class ListRenderer extends DefaultListCellRenderer{

	private static final long serialVersionUID = -1947920765547274746L;
	private final int topPad, leftPad, bottomPad, rightPad;
	
	public ListRenderer(int topPad, int leftPad, int bottomPad, int rightPad){
		this.topPad = topPad;
		this.leftPad = leftPad;
		this.bottomPad = bottomPad;
		this.rightPad = rightPad;
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean hasFocus) {
		
		// sketchy cast but it works
		JComponent comp = (JComponent) super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);
		
		comp.setBorder(BorderFactory.createEmptyBorder(this.topPad, this.leftPad, this.bottomPad, this.rightPad));
		
		return comp;
	}

}
