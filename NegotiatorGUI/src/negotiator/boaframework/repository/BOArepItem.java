package negotiator.boaframework.repository;

/**
 * Class used to represent an item in the BOArepository.
 * An item in the BOA repository has a classPath and may have a tooltip.
 * 
 * @author Mark Hendrikx
 */
public class BOArepItem {
	/** Classpath of the item in the repository */
	private String classPath;
	/** Optional tooltip of the item in the repository */
	private String tooltip;
	
	/**
	 * Captures the path and tooltip of a BOA component.
	 * @param classPath
	 * @param tooltip
	 */
	public BOArepItem(String classPath, String tooltip) {
		this.classPath = classPath;
		this.tooltip = tooltip;
	}
	
	/**
	 * @return classpath of the BOA component.
	 */
	public String getClassPath() {
		return classPath;
	}

	/**
	 * Sets the classpath of the BOA component.
	 * @param classPath of the BOA component.
	 */
	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	/**
	 * @return tooltip associated with the BOA component.
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * Set the tooltip of the BOA component.
	 * @param tooltip of the BOA component.
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
}