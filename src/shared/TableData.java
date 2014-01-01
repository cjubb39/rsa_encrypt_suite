package shared;

/**
 * An object implementing this interface is able to be displayed in a table.
 * 
 * @author Chae Jubb
 * @version 1.0
 * 
 */
public interface TableData {
	/**
	 * Checks if the delete flag is set.
	 * 
	 * @return True if set; false otherwise
	 */
	public boolean getDelete();

	/**
	 * Set delete flag to input boolean flag
	 * 
	 * @param in
	 *          What to set delete flag to
	 */
	public void setDelete(boolean in);
}
