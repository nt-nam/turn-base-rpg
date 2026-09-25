package com.pxworld.android;

import android.os.Bundle;
import android.util.Log;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.pxworld.automation.AutomationProtocol;
import com.pxworld.automation.AutomationServer;
import com.pxworld.client.GameApp;
import com.pxworld.client.automation.StageAutomationDriver;
import com.pxworld.client.core.BuildFlavor;
import com.pxworld.client.core.GameClock;
import com.pxworld.client.core.GameServices;
import com.pxworld.content.ContentBundle;
import com.pxworld.content.ContentLoader;
import com.pxworld.infrastructure.replay.FileReplayStore;
import com.pxworld.infrastructure.save.FileSaveStore;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;

import kotlin.Unit;

public class AndroidLauncher extends AndroidApplication {

    private static final String CONTENT_PACK = "content-pack.json";
    private static final String LOG_TAG = "PXWORLD";
    private static final int AUTOMATION_STOP_MILLIS = 500;

    private volatile StageAutomationDriver automation;
    private AutomationServer automationServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BuildFlavor flavor = BuildFlavor.valueOf(BuildConfig.PXWORLD_FLAVOR);
        if (flavor.getAutomation()) startAutomation();
        File saves = new File(getFilesDir(), "saves");
        GameServices services = new GameServices(
                loadContent(),
                new FileSaveStore(saves, 3),
                new SystemClock(),
                flavor,
                new FileReplayStore(new File(saves, "replays"), 30),
                api -> {
                    automation = ((GameApp) api).getAutomation();
                    return Unit.INSTANCE;
                },
                BuildConfig.PXWORLD_API_URL.isEmpty() ? null : BuildConfig.PXWORLD_API_URL,
                "android-" + BuildConfig.VERSION_NAME
        );
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = true;
        configuration.useAccelerometer = false;
        configuration.useCompass = false;
        initialize(new GameApp(services), configuration);
    }

    @Override
    protected void onDestroy() {
        stopAutomation();
        super.onDestroy();
    }

    private void startAutomation() {
        automationServer = new AutomationServer(AutomationServer.DEFAULT_PORT, new AutomationProtocol(() -> automation));
        automationServer.start();
        Log.i(LOG_TAG, "automation server starting on 127.0.0.1:" + AutomationServer.DEFAULT_PORT);
    }

    private void stopAutomation() {
        if (automationServer == null) return;
        try {
            automationServer.stop(AUTOMATION_STOP_MILLIS);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
        automationServer = null;
    }

    private ContentBundle loadContent() {
        StringBuilder text = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open(CONTENT_PACK), StandardCharsets.UTF_8))) {
            char[] buffer = new char[8192];
            int read;
            while ((read = reader.read(buffer)) != -1) text.append(buffer, 0, read);
        } catch (IOException failure) {
            throw new IllegalStateException(CONTENT_PACK + " is missing from the APK", failure);
        }
        return ContentLoader.INSTANCE.decodePack(text.toString());
    }

    private static final class SystemClock implements GameClock {
        @Override
        public long epochDay() {
            return LocalDate.now().toEpochDay();
        }

        @Override
        public long nowMillis() {
            return System.currentTimeMillis();
        }
    }
}
