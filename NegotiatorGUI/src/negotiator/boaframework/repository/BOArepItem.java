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
	
	public BOArepItem(String classPath, String tooltip) {
		this.classPath = classPath;
		this.tooltip = tooltip;
	}
	
	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public String getTooltip() {
		return tooltip;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}
}