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

import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationWithLogs;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WhiskActivationService {

    private WhiskActivationService() {
    }

    private static class LazyHolder {
        private static final WhiskActivationService INSTANCE = new WhiskActivationService();
    }

    public static WhiskActivationService getInstance() {
        return LazyHolder.INSTANCE;
    }

    public List<WhiskActivationMetaData> getWhiskActivations(WhiskAuth whiskAuth, Optional<String> name, int limit, int skip) throws IOException {
        String endpoint = whiskAuth.getApihost()
                + "/api/v1/namespaces/_/activations"
                + "?limit=" + limit + "&skip=" + skip + "" + name.map(n -> "&name=" + n).orElse("");
        String authorization = whiskAuth.getBasicAuthHeader();

        String result = Request.Get(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskActivations(result);
    }

    public Optional<WhiskActivationWithLogs> getWhiskActivation(WhiskAuth whiskAuth, String activationId) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/activations/" + activationId;
        String authorization = whiskAuth.getBasicAuthHeader();

        String result = Request.Get(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskActivation(result);
    }
}
