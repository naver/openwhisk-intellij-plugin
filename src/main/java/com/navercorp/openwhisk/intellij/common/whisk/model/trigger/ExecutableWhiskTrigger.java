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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ExecutableWhiskTrigger extends WhiskTrigger {

    private List<Map<String, Object>> parameters = new ArrayList<>();
    private Map<String, SimplifiedWhiskRule> rules = new LinkedHashMap<>();

    /**
     * Limits on a specific trigger. None yet.
     */
    private Map<String, Object> limits;

    public ExecutableWhiskTrigger() {
    }

    public ExecutableWhiskTrigger(String name, String namespace, String version, long updated, boolean publish,
                                  List<Map<String, Object>> annotations,
                                  List<Map<String, Object>> parameters,
                                  Map<String, SimplifiedWhiskRule> rules,
                                  Map<String, Object> limits) {
        super(name, namespace, version, updated, publish, annotations);
        this.parameters = parameters;
        this.rules = rules;
        this.limits = limits;
    }

    public List<Map<String, Object>> getParameters() {
        return parameters;
    }

    public void setParameters(List<Map<String, Object>> parameters) {
        this.parameters = parameters;
    }

    public Map<String, SimplifiedWhiskRule> getRules() {
        return rules;
    }

    public void setRules(Map<String, SimplifiedWhiskRule> rules) {
        this.rules = rules;
    }

    public Map<String, Object> getLimits() {
        return limits;
    }

    public void setLimits(Map<String, Object> limits) {
        this.limits = limits;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        ExecutableWhiskTrigger that = (ExecutableWhiskTrigger) o;

        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        if (rules != null ? !rules.equals(that.rules) : that.rules != null) return false;
        return limits != null ? limits.equals(that.limits) : that.limits == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (rules != null ? rules.hashCode() : 0);
        result = 31 * result + (limits != null ? limits.hashCode() : 0);
        return result;
    }
}
