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

package com.navercorp.openwhisk.intellij.common.whisk.model;

import com.navercorp.openwhisk.intellij.common.utils.WhiskUtils;

import java.util.List;

public class WhiskEndpoint {
    private String alias;
    private String apihost;
    private List<WhiskNamespace> namespaces;

    public WhiskEndpoint() {
    }

    public WhiskEndpoint(String alias, String apihost, List<WhiskNamespace> namespaces) {
        this.alias = alias;
        this.apihost = WhiskUtils.getApihHostWithProtocol(apihost);
        this.namespaces = namespaces;
    }

    public String getAlias() {
        return alias;
    }

    public void setAlias(String alias) {
        this.alias = alias;
    }

    public String getApihost() {
        return apihost;
    }

    public void setApihost(String apihost) {
        this.apihost = WhiskUtils.getApihHostWithProtocol(apihost);
    }

    public List<WhiskNamespace> getNamespaces() {
        return namespaces;
    }

    public void setNamespaces(List<WhiskNamespace> namespaces) {
        this.namespaces = namespaces;
    }

    public void addNamespaces(WhiskNamespace namespace) {
        this.namespaces.add(namespace);
    }

    public String displayName() {
        return this.alias + " (" + this.apihost + ")";
    }

}
