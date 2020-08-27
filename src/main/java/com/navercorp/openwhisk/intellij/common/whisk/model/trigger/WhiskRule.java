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

public class WhiskRule extends WhiskRuleMetaData {

    private String status;
    private List<Map<String, Object>> annotations = new ArrayList<>();

    public WhiskRule() {
    }

    public WhiskRule(String name, String namespace, String version, long updated, boolean publish,
                     SimplifiedEntityMetaData action, SimplifiedEntityMetaData trigger) {
        super(name, namespace, version, updated, publish, action, trigger);
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
        if (!super.equals(o)) return false;

        WhiskRule whiskRule = (WhiskRule) o;

        if (status != null ? !status.equals(whiskRule.status) : whiskRule.status != null) return false;
        return annotations != null ? annotations.equals(whiskRule.annotations) : whiskRule.annotations == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (annotations != null ? annotations.hashCode() : 0);
        return result;
    }
}
