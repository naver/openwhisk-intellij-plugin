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

import com.navercorp.openwhisk.intellij.common.whisk.model.Limits;
import com.navercorp.openwhisk.intellij.common.whisk.model.exec.CodeExec;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ExecutableWhiskAction extends WhiskAction<CodeExec> {
    private List<Map<String, Object>> parameters = new ArrayList<>();

    public ExecutableWhiskAction() {
    }

    public ExecutableWhiskAction(List<Map<String, Object>> parameters) {
        this.parameters = parameters;
    }

    public ExecutableWhiskAction(String name, String namespace, String version, long updated, boolean publish,
                                 List<Map<String, Object>> annotations,
                                 Limits limits,
                                 CodeExec exec,
                                 List<Map<String, Object>> parameters) {
        super(name, namespace, version, updated, publish, annotations, limits, exec);
        this.parameters = parameters;
    }

    public List<Map<String, Object>> getParameters() {
        return parameters;
    }

    public void setParameters(List<Map<String, Object>> parameters) {
        this.parameters = parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        ExecutableWhiskAction that = (ExecutableWhiskAction) o;
        return Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), parameters);
    }
}
