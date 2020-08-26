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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class WhiskActivation {

    private String activationId;
    private String name;
    private String namespace;
    private String version;
    private String cause;
    private long start;
    private long end;
    private long duration;
    private boolean publish;
    private List<Map<String, Object>> annotations = new ArrayList<>();

    public WhiskActivation() {
    }

    public WhiskActivation(String activationId, String name, String namespace, String version, String cause,
                           long start, long end, long duration, boolean publish,
                           List<Map<String, Object>> annotations) {
        this.activationId = activationId;
        this.name = name;
        this.namespace = namespace;
        this.version = version;
        this.cause = cause;
        this.start = start;
        this.end = end;
        this.duration = duration;
        this.publish = publish;
        this.annotations = annotations;
    }

    public String getActivationId() {
        return activationId;
    }

    public void setActivationId(String activationId) {
        this.activationId = activationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getVersion() {
        return version;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public long getEnd() {
        return end;
    }

    public void setEnd(long end) {
        this.end = end;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public List<Map<String, Object>> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Map<String, Object>> annotations) {
        this.annotations = annotations;
    }

    public String getKind() {
        for (Map<String, Object> a : annotations) {
            if (a.get("key").equals("kind")) {
                return (String) a.get("value");
            }
        }
        return "unknown";
    }

    public String getStartType() {
        for (Map<String, Object> a : annotations) {
            if (a.get("key").equals("initTime")) {
                return "cold";
            }
        }
        return "warm";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WhiskActivation that = (WhiskActivation) o;

        if (start != that.start) return false;
        if (end != that.end) return false;
        if (duration != that.duration) return false;
        if (publish != that.publish) return false;
        if (activationId != null ? !activationId.equals(that.activationId) : that.activationId != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        return annotations != null ? annotations.equals(that.annotations) : that.annotations == null;
    }

    @Override
    public int hashCode() {
        int result = activationId != null ? activationId.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (int) (start ^ (start >>> 32));
        result = 31 * result + (int) (end ^ (end >>> 32));
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (publish ? 1 : 0);
        result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
        return result;
    }
}
