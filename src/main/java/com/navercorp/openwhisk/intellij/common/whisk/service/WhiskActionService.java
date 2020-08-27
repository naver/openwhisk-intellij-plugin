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
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import org.apache.http.HttpHeaders;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WhiskActionService {
    private static final Logger LOG = Logger.getInstance(WhiskActionService.class);

    private WhiskActionService() {

    }

    private static class LazyHolder {
        private static final WhiskActionService INSTANCE = new WhiskActionService();
    }

    public static WhiskActionService getInstance() {
        return LazyHolder.INSTANCE;
    }

    public List<WhiskActionMetaData> getWhiskActions(WhiskAuth whiskAuth) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/actions?limit=200&skip=0";
        String authorization = whiskAuth.getBasicAuthHeader();

        String result = Request.Get(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskActions(result);
    }

    public Optional<ExecutableWhiskAction> getWhiskAction(WhiskAuth whiskAuth,
                                                          Optional<String> namespaceName,
                                                          Optional<String> pkgName,
                                                          String actionName) throws IOException {
        String namespace = namespaceName.orElse("_");
        String name = pkgName.map(p -> p + "/" + actionName).orElse(actionName);
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/" + namespace + "/actions/" + name + "?code=true";
        String authorization = whiskAuth.getBasicAuthHeader();

        String result = Request.Get(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskAction(result);
    }

    public String invokeWhiskAction(WhiskAuth whiskAuth,
                                    Optional<String> namespaceName,
                                    Optional<String> pkgName,
                                    String actionName,
                                    String params) throws IOException {
        String namespace = namespaceName.orElse("_");
        String name = pkgName.map(p -> p + "/" + actionName).orElse(actionName);
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/" + namespace + "/actions/" + name + "?blocking=true&result=true";
        String authorization = whiskAuth.getBasicAuthHeader();
        String result = Request.Post(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .bodyString(params, ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.beautifyJson(result);
    }

    public Optional<ExecutableWhiskAction> updateWhiskAction(WhiskAuth whiskAuth,
                                                             ExecutableWhiskAction updatedAction,
                                                             Map<String, Object> payload) throws IOException {
        String namespace = updatedAction.getNamespacePath();
        String name = updatedAction.getWhiskPackage().map(p -> p + "/" + updatedAction.getName()).orElse(updatedAction.getName());
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/" + namespace + "/actions/" + name + "?overwrite=true";
        String authorization = whiskAuth.getBasicAuthHeader();
        String body = JsonParserUtils.writeMapToJson(payload);
        LOG.info("Action updated: " + body);
        String result = Request.Put(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .bodyString(body, ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskAction(result);
    }

    public Optional<ExecutableWhiskAction> deleteWhiskActions(WhiskAuth whiskAuth, Optional<String> pkgName, String actionName) throws IOException {
        String name = pkgName.map(p -> p + "/" + actionName).orElse(actionName);
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/actions/" + name;
        String authorization = whiskAuth.getBasicAuthHeader();

        String result = Request.Delete(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskAction(result);
    }

}
