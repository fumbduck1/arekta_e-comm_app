package com.arektaecomm.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

public class FirebaseInit {
    public static void initialize() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) return; // Prevent re-initialization

        Properties props = new Properties();
        try (InputStream input = FirebaseInit.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) throw new IOException("config.properties not found");
            props.load(input);
        }

        String dbUrl = props.getProperty("firebase.databaseURL", "https://YOUR_PROJECT_ID.firebaseio.com");
        String bucket = props.getProperty("firebase.storageBucket", "YOUR_PROJECT_ID.appspot.com");

        GoogleCredentials credentials = resolveCredentials();

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setDatabaseUrl(dbUrl)
                .setStorageBucket(bucket)
                .build();
        FirebaseApp.initializeApp(options);
    }

    /**
     * Resolve GoogleCredentials in a packaging-friendly way:
     * 1) FIREBASE_CREDENTIALS_JSON env var with raw JSON content.
     * 2) GOOGLE_APPLICATION_CREDENTIALS or FIREBASE_CREDENTIALS env var with file path.
     * 3) Classpath resource: /serviceAccountKey.json or /serviceAccountkey.json (case variants).
     * 4) Application Default Credentials.
     */
    private static GoogleCredentials resolveCredentials() throws IOException {
        // 1) Raw JSON in env var (useful for CI/CD or container secrets)
        String json = System.getenv("FIREBASE_CREDENTIALS_JSON");
        if (json != null && !json.isBlank()) {
            try (InputStream in = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8))) {
                return GoogleCredentials.fromStream(in);
            }
        }

        // 2) File path in env
        String path = firstNonBlank(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"), System.getenv("FIREBASE_CREDENTIALS"));
        if (path != null) {
            File f = new File(path);
            if (f.isFile()) {
                try (InputStream in = new FileInputStream(f)) {
                    return GoogleCredentials.fromStream(in);
                }
            }
        }

        // 3) Classpath resource (works when packaged)
        String[] candidates = new String[] {
                "/serviceAccountKey.json",  // preferred/correct casing
                "/serviceAccountkey.json"   // fallback for existing file name
        };
        for (String res : candidates) {
            InputStream in = FirebaseInit.class.getResourceAsStream(res);
            if (in != null) {
                try (in) {
                    return GoogleCredentials.fromStream(in);
                }
            }
        }

        // 4) Application Default Credentials (ADC)
        return GoogleCredentials.getApplicationDefault();
    }

    private static String firstNonBlank(String a, String b) {
        if (a != null && !a.isBlank()) return a;
        if (b != null && !b.isBlank()) return b;
        return null;
    }
}
