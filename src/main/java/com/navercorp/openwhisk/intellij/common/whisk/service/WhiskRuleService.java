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
import org.apache.http.entity.ContentType;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskRule;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WhiskRuleService {
    private static final Logger LOG = Logger.getInstance(WhiskRuleService.class);

    private WhiskRuleService() {
    }

    private static class LazyHolder {
        private static final WhiskRuleService INSTANCE = new WhiskRuleService();
    }

    public static WhiskRuleService getInstance() {
        return LazyHolder.INSTANCE;
    }

    public Optional<WhiskRule> updateWhiskRule(WhiskAuth whiskAuth, String ruleName, Map<String, Object> payload) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/rules/" + ruleName + "?overwrite=true";
        String authorization = whiskAuth.getBasicAuthHeader();
        String body = JsonParserUtils.writeMapToJson(payload);
        LOG.info("Rule updated: " + body);
        String result = Request.Put(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .bodyString(body, ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskRule(result);
    }

    public Optional<WhiskRule> deleteWhiskRule(WhiskAuth whiskAuth, String ruleName) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/rules/" + ruleName;
        String authorization = whiskAuth.getBasicAuthHeader();
        String result = Request.Delete(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskRule(result);
    }

}
