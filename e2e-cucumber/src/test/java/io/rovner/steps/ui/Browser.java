package io.rovner.steps.ui;

import io.qameta.allure.Attachment;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.lifecycle.TestDescription;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.Optional;
import java.util.UUID;

import static io.rovner.Config.getBrowser;
import static org.openqa.selenium.OutputType.BYTES;
import static org.testcontainers.containers.BrowserWebDriverContainer.VncRecordingMode.RECORD_ALL;
import static org.testcontainers.containers.VncRecordingContainer.VncRecordingFormat.MP4;

public class Browser {

    public static final Path RECORDINGS = Paths.get("build/recordings");
    private BrowserWebDriverContainer<?> browserContainer;
    private final String id = UUID.randomUUID().toString();

    public WebDriver getWebDriver() {
        if (browserContainer == null) {
            init();
        }
        return browserContainer.getWebDriver();
    }

    public WebDriverWait newWait(Duration duration) {
        return new WebDriverWait(getWebDriver(), duration);
    }

    public void close(boolean failed) {
        if (browserContainer != null) {
            if (failed) {
                attachScreenshot();
                attachVideo();
            }
            browserContainer.stop();
        }
        browserContainer = null;
    }

    @SneakyThrows
    @Attachment(value = "video", type = "video/mp4")
    private byte[] attachVideo() {
        browserContainer.afterTest(new TestDescription() {
            @Override
            public String getTestId() {
                return id;
            }

            @Override
            public String getFilesystemFriendlyName() {
                return "";
            }
        }, Optional.empty());
        return Files.readAllBytes(getRecordingFile());
    }

    @Attachment(value = "screenshot", type = "image/png")
    public byte[] attachScreenshot() {
        return ((TakesScreenshot) getWebDriver()).getScreenshotAs(BYTES);
    }

    private void init() {
        Capabilities capabilities;
        switch (getBrowser()) {
            case "chrome":
                capabilities = new ChromeOptions();
                break;
            case "firefox":
                capabilities = new FirefoxOptions();
                break;
            default:
                throw new RuntimeException(String.format("Browser %s is not supported", getBrowser()));

        }
        //noinspection resource
        browserContainer = new BrowserWebDriverContainer<>()
                .withCapabilities(capabilities)
                .withRecordingMode(RECORD_ALL, RECORDINGS.toFile(), MP4)
                .withRecordingFileFactory((dir, prefix, succeeded) -> getRecordingFile().toFile());
        browserContainer.start();
    }

    @NotNull
    private Path getRecordingFile() {
        return RECORDINGS.resolve(id + "." + MP4.getFilenameExtension());
    }
}
