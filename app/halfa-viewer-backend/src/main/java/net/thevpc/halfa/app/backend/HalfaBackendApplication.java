package net.thevpc.halfa.app.backend;

import net.thevpc.nuts.NApplication;
import net.thevpc.nuts.NSession;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HalfaBackendApplication implements NApplication {

	public static void main(String[] args) {
		SpringApplication.run(HalfaBackendApplication.class, args);
	}

//	@Override
//	public void onInstallApplication(NSession session) {
//		session.out().println("write your business logic that will be processed when the application is being installed here...");
//	}
//
//	@Override
//	public void onUpdateApplication(NSession session) {
//		session.out().println("write your business logic that will be processed when the application is being updated/upgraded here...");
//	}
//
//	@Override
//	public void onUninstallApplication(NSession session) {
//		session.out().println("write your business logic that will be processed when the application is being uninstalled/removed here...");
//	}

	@Override
	public void run(NSession session) {
		//
	}
}
