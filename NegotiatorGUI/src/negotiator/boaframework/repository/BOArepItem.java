package negotiator.decoupledframework.repository;

public class BOArepItem {
	private String classPath;
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
