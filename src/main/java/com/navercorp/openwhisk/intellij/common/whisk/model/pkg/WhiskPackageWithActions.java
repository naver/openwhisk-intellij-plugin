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

import com.navercorp.openwhisk.intellij.common.whisk.model.action.CompactWhiskAction;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WhiskPackageWithActions extends WhiskPackage {

    private List<Map<String, Object>> parameters = new ArrayList<>();
    private List<CompactWhiskAction> actions = new ArrayList<>();
    private List<Object> feeds = new ArrayList<>();

    public WhiskPackageWithActions() {
    }

    public WhiskPackageWithActions(String name, String namespace, boolean publish, long updated, String version,
                                   List<Map<String, Object>> annotations,
                                   Object binding,
                                   List<Map<String, Object>> parameters,
                                   List<CompactWhiskAction> actions,
                                   List<Object> feeds) {
        super(name, namespace, publish, updated, version, annotations, binding);
        this.parameters = parameters;
        this.actions = actions;
        this.feeds = feeds;
    }

    public List<CompactWhiskAction> getActions() {
        return actions;
    }

    public void setActions(List<CompactWhiskAction> actions) {
        this.actions = actions;
    }

    public List<Map<String, Object>> getParameters() {
        return parameters;
    }

    public void setParameters(List<Map<String, Object>> parameters) {
        this.parameters = parameters;
    }


    public List<Object> getFeeds() {
        return feeds;
    }

    public void setFeeds(List<Object> feeds) {
        this.feeds = feeds;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        WhiskPackageWithActions that = (WhiskPackageWithActions) o;

        if (parameters != null ? !parameters.equals(that.parameters) : that.parameters != null) return false;
        if (actions != null ? !actions.equals(that.actions) : that.actions != null) return false;
        return feeds != null ? feeds.equals(that.feeds) : that.feeds == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (parameters != null ? parameters.hashCode() : 0);
        result = 31 * result + (actions != null ? actions.hashCode() : 0);
        result = 31 * result + (feeds != null ? feeds.hashCode() : 0);
        return result;
    }
}
