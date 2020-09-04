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

package com.navercorp.openwhisk.intellij.common.whisk.model.action;

import com.navercorp.openwhisk.intellij.common.Icons;
import com.navercorp.openwhisk.intellij.common.whisk.model.Limits;
import com.navercorp.openwhisk.intellij.common.whisk.model.exec.Exec;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public abstract class WhiskAction<E extends Exec> {
    private String name;
    private String namespace;
    private String version;
    private long updated;
    private boolean publish;
    private List<Map<String, Object>> annotations = new ArrayList<>();
    private Limits limits;
    private E exec;

    public WhiskAction() {
    }

    public WhiskAction(String name, String namespace, String version, long updated, boolean publish,
                       List<Map<String, Object>> annotations,
                       Limits limits,
                       E exec) {
        this.name = name;
        this.namespace = namespace;
        this.version = version;
        this.updated = updated;
        this.publish = publish;
        this.annotations = annotations;
        this.limits = limits;
        this.exec = exec;
    }

    public Optional<String> getWhiskPackage() {
        if (namespace == null) {
            return Optional.empty();
        } else {
            String[] ns = namespace.split("/");
            if (ns.length == 1) {
                return Optional.empty();
            } else {
                return Optional.of(ns[1]);
            }
        }
    }

    public String getNamespacePath() {
        return namespace.split("/")[0];
    }

    public String getFullyQualifiedName() {
        return namespace + "/" + name;
    }

    public String getKind() {
        for (Map<String, Object> a : this.annotations) {
            if ("exec".equals(a.get("key"))) {
                return (String) a.get("value");
            }
        }
        return "";
    }

    public Optional<String> getCodeType() {
        for (Map<String, Object> a : this.annotations) {
            if ("code-type".equals(a.get("key"))) {
                return Optional.ofNullable((String) a.get("value"));
            }
        }
        return Optional.empty();
    }

    public String getKindExtension() {
        String kind = getKind();
        if (kind.contains("java")) {
            return ".java";
        } else if (kind.contains("nodejs")) {
            return ".js";
        } else if (kind.contains("python")) {
            return ".py";
        } else if (kind.contains("swift")) {
            return ".swift";
        } else if (kind.contains("php")) {
            return ".php";
        } else if (kind.contains("go")) {
            return ".go";
        } else if (kind.contains("ruby")) {
            return ".rb";
        } else if (kind.contains("blackbox")) {
            return getCodeType().map(codeType -> {
                if (codeType.contains("java")) {
                    return ".java";
                } else if (codeType.contains("nodejs")) {
                    return ".js";
                } else if (codeType.contains("python")) {
                    return ".py";
                } else if (codeType.contains("swift")) {
                    return ".swift";
                } else if (codeType.contains("php")) {
                    return ".php";
                } else if (codeType.contains("go")) {
                    return ".go";
                } else if (codeType.contains("ruby")) {
                    return ".rb";
                }
                return "";
            }).orElse("");
        }
        return "";
    }

    public Icon getKindIcon() {
        String kind = getKind();
        if (kind.contains("java")) {
            return Icons.KIND_JAVA;
        } else if (kind.contains("nodejs")) {
            return Icons.KIND_JS;
        } else if (kind.contains("python")) {
            return Icons.KIND_PYTHON;
        } else if (kind.contains("swift")) {
            return Icons.KIND_SWIFT;
        } else if (kind.contains("php")) {
            return Icons.KIND_PHP;
        } else if (kind.contains("go")) {
            return Icons.KIND_GO;
        } else if (kind.contains("ruby")) {
            return Icons.KIND_RUBY;
        } else if (kind.contains("sequence")) {
            return Icons.KIND_SEQUENCE;
        } else {
            return Icons.KIND_DOCKER;
        }
    }


    public boolean isSequenceAction() {
        String kind = getKind();
        return kind.equals("sequence");
    }

    public boolean isWebAction() {
        for (Map<String, Object> a : this.annotations) {
            if ("web-export".equals(a.get("key"))) {
                return (Boolean) a.get("value");
            }
        }
        return false;
    }

    public boolean isCustomOptions() {
        for (Map<String, Object> a : this.annotations) {
            if ("web-custom-options".equals(a.get("key"))) {
                return (Boolean) a.get("value");
            }
        }
        return false;
    }

    public boolean isRawHttp() {
        for (Map<String, Object> a : this.annotations) {
            if ("raw-http".equals(a.get("key"))) {
                return (Boolean) a.get("value");
            }
        }
        return false;
    }

    public boolean isFinalDefaultParameter() {
        for (Map<String, Object> a : this.annotations) {
            if ("final".equals(a.get("key"))) {
                return (Boolean) a.get("value");
            }
        }
        return false;
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

    public Limits getLimits() {
        return limits;
    }

    public void setLimits(Limits limits) {
        this.limits = limits;
    }

    public E getExec() {
        return exec;
    }

    public void setExec(E exec) {
        this.exec = exec;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WhiskAction<?> that = (WhiskAction<?>) o;

        if (updated != that.updated) return false;
        if (publish != that.publish) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (namespace != null ? !namespace.equals(that.namespace) : that.namespace != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        if (annotations != null ? !annotations.equals(that.annotations) : that.annotations != null) return false;
        if (limits != null ? !limits.equals(that.limits) : that.limits != null) return false;
        return exec != null ? exec.equals(that.exec) : that.exec == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (namespace != null ? namespace.hashCode() : 0);
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (int) (updated ^ (updated >>> 32));
        result = 31 * result + (publish ? 1 : 0);
        result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
        result = 31 * result + (limits != null ? limits.hashCode() : 0);
        result = 31 * result + (exec != null ? exec.hashCode() : 0);
        return result;
    }
}
