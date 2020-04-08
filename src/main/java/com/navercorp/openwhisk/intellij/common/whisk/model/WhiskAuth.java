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

import java.util.Base64;

public class WhiskAuth {
    private String auth;
    private String apihost;

    public WhiskAuth() {
    }

    public WhiskAuth(String auth, String apihost) {
        this.auth = auth;
        this.apihost = WhiskUtils.getApihHostWithProtocol(apihost);
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public String getApihost() {
        return apihost;
    }

    public void setApihost(String apihost) {
        this.apihost = WhiskUtils.getApihHostWithProtocol(apihost);
    }

    public String getBasicAuthHeader() {
        return "Basic " + Base64.getEncoder().encodeToString(auth.getBytes());
    }
}
