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

public class SimplifiedWhiskRule {

    private SimplifiedEntityMetaData action;
    private String status;

    public SimplifiedWhiskRule() {
    }

    public SimplifiedWhiskRule(SimplifiedEntityMetaData action, String status) {
        this.action = action;
        this.status = status;
    }

    public SimplifiedEntityMetaData getAction() {
        return action;
    }

    public void setAction(SimplifiedEntityMetaData action) {
        this.action = action;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplifiedWhiskRule that = (SimplifiedWhiskRule) o;

        if (action != null ? !action.equals(that.action) : that.action != null) return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = action != null ? action.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }
}
