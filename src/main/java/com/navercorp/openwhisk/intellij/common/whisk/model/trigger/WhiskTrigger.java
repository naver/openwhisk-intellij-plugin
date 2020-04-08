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

package com.navercorp.openwhisk.intellij.common.whisk.model.trigger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public abstract class WhiskTrigger {

    private String name;
    private String namespace;
    private String version;
    private long updated;
    private boolean publish;
    private List<Map<String, Object>> annotations = new ArrayList<>();

    public WhiskTrigger() {
    }

    public WhiskTrigger(String name, String namespace, String version, long updated, boolean publish, List<Map<String, Object>> annotations) {
        this.name = name;
        this.namespace = namespace;
        this.version = version;
        this.updated = updated;
        this.publish = publish;
        this.annotations = annotations;
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

    public void setVersion(String version) {
        this.version = version;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WhiskTrigger that = (WhiskTrigger) o;

        if (updated != that.updated) return false;
        if (publish != that.publish) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        return annotations != null ? annotations.equals(that.annotations) : that.annotations == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (int) (updated ^ (updated >>> 32));
        result = 31 * result + (publish ? 1 : 0);
        result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
        return result;
    }
}
