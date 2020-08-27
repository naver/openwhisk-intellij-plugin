/**
 * Copyright 2020-present NAVER Corp.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.navercorp.openwhisk.intellij.common.whisk.service;

import com.intellij.openapi.diagnostic.Logger;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskNamespace;

import java.io.IOException;
import java.util.Optional;

public class WhiskNamespaceService {
    private static final Logger LOG = Logger.getInstance(WhiskNamespaceService.class);

    private WhiskNamespaceService() {

    }

    private static class LazyHolder {
        private static final WhiskNamespaceService INSTANCE = new WhiskNamespaceService();
    }

    public static WhiskNamespaceService getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Optional<WhiskNamespace> validateNamespace(WhiskAuth whiskAuth) {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces";
        String authorization = whiskAuth.getBasicAuthHeader();
        try {
            String result = Request.Get(endpoint)
                    .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                    .execute()
                    .returnContent()
                    .asString();

            String[] namespaces = JsonParserUtils.parseWhiskNamespace(result);
            if (namespaces.length > 0) {
                return Optional.of(new WhiskNamespace(whiskAuth.getAuth(), namespaces[0]));
            } else {
                return Optional.empty();
            }
        } catch (IOException e) {
            LOG.warn("Invalid namespace", e);
            return Optional.empty();
        }
    }
}
