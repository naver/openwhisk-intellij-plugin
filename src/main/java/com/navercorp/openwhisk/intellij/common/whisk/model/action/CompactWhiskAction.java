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

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CompactWhiskAction {
    private String name;
    private String version;
    private List<Map<String, Object>> annotations = new ArrayList<>();

    public CompactWhiskAction() {
    }

    public CompactWhiskAction(String name, String version, List<Map<String, Object>> annotations) {
        this.name = name;
        this.version = version;
        this.annotations = annotations;
    }

    public String getKind() {
        for (Map<String, Object> a : this.annotations) {
            if ("exec".equals(a.get("key"))) {
                return (String) a.get("value");
            }
        }
        return "";
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
        } else {
            return "";
        }
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CompactWhiskAction that = (CompactWhiskAction) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (version != null ? !version.equals(that.version) : that.version != null) return false;
        return annotations != null ? annotations.equals(that.annotations) : that.annotations == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (version != null ? version.hashCode() : 0);
        result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
        return result;
    }
}
