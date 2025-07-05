package net.thevpc.ndoc.app.backend;

import net.thevpc.nuts.NAppDefinition;
import net.thevpc.nuts.NAppRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@NAppDefinition
@SpringBootApplication
public class NDocBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NDocBackendApplication.class, args);
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

	@NAppRunner
	public void run() {
		//
	}
}
