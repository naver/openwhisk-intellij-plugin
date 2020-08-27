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

package com.navercorp.openwhisk.intellij.common.service;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "wskdeploy",
        storages = {
                @Storage("wskdeploy.xml")
        }
)
public class WskDeployService implements PersistentStateComponent<WskDeployService> {
    private static final Logger LOG = Logger.getInstance(WskDeployService.class);

    private String wskdeployPath;
    private String wskdeployName;

    @Nullable
    @Override
    public WskDeployService getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull WskDeployService state) {
        LOG.info("Save wskdeploy");
        XmlSerializerUtil.copyBean(state, this);
    }

    public String getWskdeployPath() {
        return wskdeployPath;
    }

    public void setWskdeployPath(String wskdeployPath) {
        this.wskdeployPath = wskdeployPath;
    }

    public String getWskdeployName() {
        return wskdeployName;
    }

    public void setWskdeployName(String wskdeployName) {
        this.wskdeployName = wskdeployName;
    }
}
