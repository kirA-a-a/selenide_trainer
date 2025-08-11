package com.safronov.spring.mvc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.web.context.WebServerApplicationContext;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

@Component
public class BrowserLauncher {

    private static final Logger log = LoggerFactory.getLogger(BrowserLauncher.class);

    private final WebServerApplicationContext applicationContext;

    public BrowserLauncher(WebServerApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        int port = applicationContext.getWebServer().getPort();
        String url = "http://localhost:" + port + "/";
        openInBrowser(url);
    }

    private void openInBrowser(String url) {
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop.getDesktop().browse(new URI(url));
                log.info("Opened default browser at {}", url);
                return;
            }
        } catch (IOException | URISyntaxException e) {
            log.warn("Failed to open browser via Desktop API: {}", e.getMessage());
        }

        String os = System.getProperty("os.name").toLowerCase();
        try {
            if (os.contains("mac")) {
                new ProcessBuilder("open", url).start();
            } else if (os.contains("win")) {
                new ProcessBuilder("cmd", "/c", "start", url).start();
            } else {
                new ProcessBuilder("xdg-open", url).start();
            }
            log.info("Opened browser via OS command at {}", url);
        } catch (IOException e) {
            log.error("Unable to launch browser automatically. Please open {} manually. Error: {}", url, e.getMessage());
        }
    }
}


