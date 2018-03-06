package genius;

import java.io.IOException;

import genius.domains.DomainInstaller;
import negotiator.gui.DeprecatedOldMain;

public class Application {
	public static void main(String[] args) throws IOException {
		ProtocolsInstaller.run();
		DomainInstaller.run();
		AgentsInstaller.run();
		DeprecatedOldMain.run(args);
	}
}
