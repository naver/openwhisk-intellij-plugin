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

import org.codehaus.groovy.util.ListHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhiskActivationWithLogs extends WhiskActivation {
    private String subject;
    private List<String> logs = new ArrayList<>();
    private Map<String, Object> response = new ListHashMap<>();

    public WhiskActivationWithLogs() {
    }

    public WhiskActivationWithLogs(String activationId, String name, String namespace, String version, String cause,
                                   long start, long end, long duration, boolean publish,
                                   List<Map<String, Object>> annotations,
                                   String subject,
                                   List<String> logs,
                                   Map<String, Object> response) {
        super(activationId, name, namespace, version, cause, start, end, duration, publish, annotations);
        this.subject = subject;
        this.logs = logs;
        this.response = response;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }

    public Map<String, Object> getResponse() {
        return response;
    }

    public void setResponse(Map<String, Object> response) {
        this.response = response;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WhiskActivationWithLogs that = (WhiskActivationWithLogs) o;

        if (subject != null ? !subject.equals(that.subject) : that.subject != null) return false;
        if (logs != null ? !logs.equals(that.logs) : that.logs != null) return false;
        return response != null ? response.equals(that.response) : that.response == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (logs != null ? logs.hashCode() : 0);
        result = 31 * result + (response != null ? response.hashCode() : 0);
        return result;
    }
}
