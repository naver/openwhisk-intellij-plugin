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

package org.apache.openwhisk.intellij.common.utils;

import com.intellij.openapi.diagnostic.Logger;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskAuth;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskNamespace;

import java.util.List;
import java.util.Optional;

public class WhiskUtils {
    private final static Logger LOG = Logger.getInstance(WhiskUtils.class);

    public static Optional<WhiskAuth> findWhiskAuth(List<WhiskEndpoint> endpoints, WhiskNamespace namespace) {
        for (WhiskEndpoint ep: endpoints) {
            for (WhiskNamespace np: ep.getNamespaces()) {
                if(np.getAuth().equals(namespace.getAuth()) && np.getPath().equals(namespace.getPath())) {
                    return Optional.of(new WhiskAuth(np.getAuth(), ep.getApihost()));
                }
            }
        }
        return Optional.empty();
    }

    public static Optional<WhiskNamespace> findWhiskNamespace(List<WhiskEndpoint> endpoints, String auth) {
        for (WhiskEndpoint ep: endpoints) {
            for (WhiskNamespace np: ep.getNamespaces()) {
                if(np.getAuth().equals(auth)) {
                    return Optional.of(np);
                }
            }
        }
        return Optional.empty();
    }

    public static String getApihHostWithProtocol(String apiHost) {
        if (!apiHost.toLowerCase().matches("^\\w+://.*")) {
            apiHost = "https://" + apiHost;
        }
        return apiHost;
    }
}
