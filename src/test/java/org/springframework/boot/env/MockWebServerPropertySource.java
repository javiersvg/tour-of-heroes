/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.env;

import okhttp3.mockwebserver.Dispatcher;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Collectors;

/**
 * @author Rob Winch
 */
public class MockWebServerPropertySource extends PropertySource<MockWebServer> implements
        DisposableBean {

    private static final MockResponse JWKS_RESPONSE = response(jwks("Default"), 200);

    private static final MockResponse NOT_FOUND_RESPONSE = response(
            "{ \"message\" : \"This mock authorization server responds to just one request: GET /.well-known/jwks.json.\" }",
            404
    );

    /**
     * Name of the random {@link PropertySource}.
     */
    public static final String MOCK_WEB_SERVER_PROPERTY_SOURCE_NAME = "mockwebserver";

    private static final String NAME = "mockwebserver.url";

    private static final Log logger = LogFactory.getLog(MockWebServerPropertySource.class);

    private boolean started;

    public MockWebServerPropertySource() {
        super(MOCK_WEB_SERVER_PROPERTY_SOURCE_NAME, new MockWebServer());
    }

    @Override
    public Object getProperty(String name) {
        if (!name.equals(NAME)) {
            return null;
        }
        if (logger.isTraceEnabled()) {
            logger.trace("Looking up the url for '" + name + "'");
        }
        return getUrl();
    }

    @Override
    public void destroy() throws Exception {
        getSource().shutdown();
    }

    /**
     * Get's the URL (i.e. "http://localhost:123456")
     * @return
     */
    private String getUrl() {
        MockWebServer mockWebServer = getSource();
        if (!this.started) {
            intializeMockWebServer(mockWebServer);
        }
        String url = mockWebServer.url("").url().toExternalForm();
        return url.substring(0, url.length() - 1);
    }

    private void intializeMockWebServer(MockWebServer mockWebServer) {
        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public MockResponse dispatch(RecordedRequest request) throws InterruptedException {
                if ("/.well-known/jwks.json".equals(request.getPath())) {
                    return JWKS_RESPONSE;
                }

                return NOT_FOUND_RESPONSE;
            }
        };

        mockWebServer.setDispatcher(dispatcher);
        try {
            mockWebServer.start();
            this.started = true;
        } catch (IOException e) {
            throw new RuntimeException("Could not start " + mockWebServer, e);
        }
    }

    private static MockResponse response(String body, int status) {
        return new MockResponse()
                .setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .setResponseCode(status)
                .setBody(body);
    }

    private static String jwks(String name) {
        try {
            return resource(name + ".jwks");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String resource(String suffix) throws IOException {
        String name = MockWebServerPropertySource.class.getSimpleName() + "-" + suffix;
        ClassPathResource resource = new ClassPathResource(name, MockWebServerPropertySource.class);
        try ( BufferedReader reader = new BufferedReader(new FileReader(resource.getFile())) ) {
            return reader.lines().collect(Collectors.joining());
        }
    }
}