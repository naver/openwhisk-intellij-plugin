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

package com.navercorp.openwhisk.intellij.common.whisk.model.activation;

import java.util.List;
import java.util.Map;

public class WhiskActivationMetaData extends WhiskActivation {

    private int statusCode;

    public WhiskActivationMetaData() {
    }

    public WhiskActivationMetaData(String activationId, String name, String namespace, String version, String cause,
                                   long start, long end, long duration, boolean publish,
                                   List<Map<String, Object>> annotations,
                                   int statusCode) {
        super(activationId, name, namespace, version, cause, start, end, duration, publish, annotations);
        this.statusCode = statusCode;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getStatus() {
        switch (this.statusCode) {
            case 0:
                return "success";
            case 1:
                return "application error";
            case 2:
                return "developer error";
            default:
                return "internal error";
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WhiskActivationMetaData that = (WhiskActivationMetaData) o;

        return statusCode == that.statusCode;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + statusCode;
        return result;
    }
}
