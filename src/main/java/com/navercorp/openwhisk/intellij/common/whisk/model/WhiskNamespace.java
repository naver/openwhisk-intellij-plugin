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

package com.navercorp.openwhisk.intellij.common.whisk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(value = {"packages", "actions"})
public class WhiskNamespace {
    private String auth;
    private String path;
    private List<WhiskPackage> packages = new ArrayList<>();
    private List<WhiskActionMetaData> actions = new ArrayList<>();
    private List<WhiskTriggerMetaData> triggers = new ArrayList<>();

    public WhiskNamespace() {
    }

    public WhiskNamespace(String auth, String path) {
        this.auth = auth;
        this.path = path;
    }

    public WhiskNamespace(String auth, String path, List<WhiskPackage> packages, List<WhiskActionMetaData> actions, List<WhiskTriggerMetaData> triggers) {
        this.auth = auth;
        this.path = path;
        this.packages = packages;
        this.actions = actions;
        this.triggers = triggers;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getAuth() {
        return auth;
    }

    public void setAuth(String auth) {
        this.auth = auth;
    }

    public List<WhiskPackage> getPackages() {
        return packages;
    }

    public void setPackages(List<WhiskPackage> packages) {
        this.packages = packages;
    }

    public List<WhiskActionMetaData> getActions() {
        return actions;
    }

    public void setActions(List<WhiskActionMetaData> actions) {
        this.actions = actions;
    }

    public List<WhiskTriggerMetaData> getTriggers() {
        return triggers;
    }

    public void setTriggers(List<WhiskTriggerMetaData> triggers) {
        this.triggers = triggers;
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WhiskNamespace that = (WhiskNamespace) o;
        return Objects.equals(auth, that.auth)
                && Objects.equals(path, that.path)
                && Objects.equals(packages, that.packages)
                && Objects.equals(actions, that.actions)
                && Objects.equals(triggers, that.triggers);
    }

    @Override
    public int hashCode() {
        return Objects.hash(auth, path, packages, actions, triggers);
    }
}
