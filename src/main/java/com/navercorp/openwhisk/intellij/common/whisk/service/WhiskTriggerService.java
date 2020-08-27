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
import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.ExecutableWhiskTrigger;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WhiskTriggerService {
    private static final Logger LOG = Logger.getInstance(WhiskTriggerService.class);

    private WhiskTriggerService() {
    }

    private static class LazyHolder {
        private static final WhiskTriggerService INSTANCE = new WhiskTriggerService();
    }

    public static WhiskTriggerService getInstance() {
        return LazyHolder.INSTANCE;
    }

    public List<WhiskTriggerMetaData> getWhiskTriggers(WhiskAuth whiskAuth) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/triggers?limit=50&skip=0";
        String authorization = whiskAuth.getBasicAuthHeader();

        String result = Request.Get(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskTriggers(result);
    }

    public Optional<ExecutableWhiskTrigger> getWhiskTrigger(WhiskAuth whiskAuth, String triggerName) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/triggers/" + triggerName;
        String authorization = whiskAuth.getBasicAuthHeader();

        String result = Request.Get(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskTrigger(result);
    }

    public Optional<String> fireWhiskTrigger(WhiskAuth whiskAuth, String triggerName, String params) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/triggers/" + triggerName;
        String authorization = whiskAuth.getBasicAuthHeader();
        Content content = Request.Post(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .bodyString(params, ContentType.APPLICATION_JSON)
                .execute()
                .returnContent();
        if (content != null) {
            return Optional.ofNullable(JsonParserUtils.beautifyJson(content.asString(UTF_8)));
        } else {
            return Optional.empty();
        }
    }

    public Optional<ExecutableWhiskTrigger> deleteWhiskTrigger(WhiskAuth whiskAuth, String triggerName) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/triggers/" + triggerName;
        String authorization = whiskAuth.getBasicAuthHeader();

        String result = Request.Delete(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskTrigger(result);
    }

    public Optional<ExecutableWhiskTrigger> createWhiskTrigger(WhiskAuth whiskAuth, String triggerName, Map<String, Object> payload) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/triggers/" + triggerName + "?overwrite=false";
        String authorization = whiskAuth.getBasicAuthHeader();
        String body = JsonParserUtils.writeMapToJson(payload);
        LOG.info("Trigger updated: " + body);
        String result = Request.Put(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .bodyString(body, ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskTrigger(result);
    }

    public Optional<ExecutableWhiskTrigger> updateWhiskTrigger(WhiskAuth whiskAuth, String triggerName, Map<String, Object> payload) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/triggers/" + triggerName + "?overwrite=true";
        String authorization = whiskAuth.getBasicAuthHeader();
        String body = JsonParserUtils.writeMapToJson(payload);
        LOG.info("Trigger updated: " + body);
        String result = Request.Put(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .bodyString(body, ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskTrigger(result);
    }

}
