package com.ing.omsvclyr.component;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.IdTokenCredentials;
import com.google.auth.oauth2.IdTokenProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Service
public class GcpTokenProvider {

    private static final Logger logger = LogManager.getLogger(GcpTokenProvider.class);


    @Cacheable(value = "gcp-id-token", key = "'gcp'")
    public String getIdToken() {
        logger.info("getToken");
        GoogleCredentials credentials;
        try {
            credentials = GoogleCredentials.getApplicationDefault();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (!(credentials instanceof IdTokenProvider)) {
            throw new IllegalArgumentException("Credentials are not an instance of IdTokenProvider.");
        }
        IdTokenCredentials tokenCredential =
                IdTokenCredentials.newBuilder()
                        .setIdTokenProvider((IdTokenProvider) credentials)
                        .setTargetAudience("example")
                        .build();

        //TODO this code needs to deleted once we get IDToken from metadata
        String command = "curl 'http://metadata.google.internal/computeMetadata/v1/instance/service-accounts/default/identity?audience=32555940559.apps.googleusercontent.com' -H 'Metadata-Flavor: Google'";
        String s;
        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));
            while ((s = br.readLine()) != null) {
                logger.info("cURL:" + s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO - END

        try {
            tokenCredential.getRequestMetadata();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return tokenCredential.getIdToken().getTokenValue();
    }

    @CacheEvict(value = "gcp-id-token", allEntries = true)
    @Scheduled(fixedRateString = "${webclient.caching.token.google}")
    public void clearTokenCache() {
        logger.info("emptying gcp token cache");
    }
}
