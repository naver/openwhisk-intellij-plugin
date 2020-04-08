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

package com.navercorp.openwhisk.intellij.common.whisk.model.pkg;

import com.navercorp.openwhisk.intellij.common.whisk.model.Binding;

import java.util.*;

public class WhiskPackage {
    private String name;
    private String namespace;
    private boolean publish;
    private long updated;
    private String version;
    private List<Map<String, Object>> annotations = new ArrayList<>();
    private Object binding;

    public WhiskPackage() {
    }

    public WhiskPackage(String name, String namespace, boolean publish, long updated, String version, List<Map<String, Object>> annotations, Object binding) {
        this.name = name;
        this.namespace = namespace;
        this.publish = publish;
        this.updated = updated;
        this.version = version;
        this.annotations = annotations;
        this.binding = binding;
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

    public boolean isPublish() {
        return publish;
    }

    public void setPublish(boolean publish) {
        this.publish = publish;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<Map<String, Object>> getAnnotations() {
        return annotations;
    }

    public void setAnnotations(List<Map<String, Object>> annotations) {
        this.annotations = annotations;
    }

    public Optional<Binding> getBinding() {
        if (binding instanceof Map) {
            Map<String, String> b = (LinkedHashMap) binding;
            if (b.containsKey("namespace") && b.containsKey("name")) {
                return Optional.of(new Binding(b.get("namespace"), b.get("name")));
            } else {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
    }

    public void setBinding(Object binding) {
        this.binding = binding;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WhiskPackage that = (WhiskPackage) o;

        if (publish != that.publish) return false;
        if (updated != that.updated) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (annotations != null ? !annotations.equals(that.annotations) : that.annotations != null) return false;
        return binding != null ? binding.equals(that.binding) : that.binding == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (publish ? 1 : 0);
        result = 31 * result + (int) (updated ^ (updated >>> 32));
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
        result = 31 * result + (binding != null ? binding.hashCode() : 0);
        return result;
    }
}
