import negotiator.gui.NegoGUIApp;

public class Application {
	public static void main(String[] args ) {
		DomainInstaller.run();
		NegoGUIApp.run(args);
	}
}
