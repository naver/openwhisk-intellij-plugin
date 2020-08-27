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
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackageWithActions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WhiskPackageService {
    private static final Logger LOG = Logger.getInstance(WhiskPackageService.class);

    private WhiskPackageService() {

    }

    private static class LazyHolder {
        private static final WhiskPackageService INSTANCE = new WhiskPackageService();
    }

    public static WhiskPackageService getInstance() {
        return WhiskPackageService.LazyHolder.INSTANCE;
    }

    public List<WhiskPackage> getWhiskPackages(WhiskAuth whiskAuth) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/packages?limit=200&skip=0";
        String authorization = whiskAuth.getBasicAuthHeader();

        String result = Request.Get(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskPackages(result);
    }

    public Optional<WhiskPackageWithActions> getWhiskPackage(WhiskAuth whiskAuth, String namespace, String name) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/" + namespace + "/packages/" + name;
        String authorization = whiskAuth.getBasicAuthHeader();

        String result = Request.Get(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskPackage(result);
    }

    public Optional<WhiskPackageWithActions> deleteWhiskPackage(WhiskAuth whiskAuth, String name) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/packages/" + name;
        String authorization = whiskAuth.getBasicAuthHeader();
        HttpResponse response = Request.Delete(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .execute()
                .returnResponse();

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CONFLICT) {
            return Optional.empty();
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            String inputLine;
            StringBuffer result = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                result.append(inputLine);
            }
            reader.close();
            return JsonParserUtils.parseWhiskPackage(result.toString());
        }
    }

    public Optional<WhiskPackageWithActions> updateWhiskPackage(WhiskAuth whiskAuth, String name, Map<String, Object> payload) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/packages/" + name + "?overwrite=true";
        String authorization = whiskAuth.getBasicAuthHeader();
        String body = JsonParserUtils.writeMapToJson(payload);
        LOG.info("Package updated: " + body);
        String result = Request.Put(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .bodyString(body, ContentType.APPLICATION_JSON)
                .execute()
                .returnContent()
                .asString(UTF_8);
        return JsonParserUtils.parseWhiskPackage(result);
    }

    public Optional<WhiskPackageWithActions> createWhiskPackage(WhiskAuth whiskAuth, String name, Map<String, Object> payload) throws IOException {
        String endpoint = whiskAuth.getApihost() + "/api/v1/namespaces/_/packages/" + name + "?overwrite=false";
        String authorization = whiskAuth.getBasicAuthHeader();
        String body = JsonParserUtils.writeMapToJson(payload);
        LOG.info("Package craeted: " + body);
        HttpResponse response = Request.Put(endpoint)
                .setHeader(HttpHeaders.AUTHORIZATION, authorization)
                .bodyString(body, ContentType.APPLICATION_JSON)
                .execute()
                .returnResponse();

        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_CONFLICT) {
            return Optional.empty();
        } else {
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    response.getEntity().getContent()));
            String inputLine;
            StringBuffer result = new StringBuffer();
            while ((inputLine = reader.readLine()) != null) {
                result.append(inputLine);
            }
            reader.close();
            return JsonParserUtils.parseWhiskPackage(result.toString());
        }
    }
}
