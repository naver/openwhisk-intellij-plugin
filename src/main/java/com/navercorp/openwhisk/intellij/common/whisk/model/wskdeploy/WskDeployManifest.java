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

package com.navercorp.openwhisk.intellij.common.whisk.model.wskdeploy;

public class WskDeployManifest {

    private String path;
    private String fullPath;
    private String name;

    public WskDeployManifest() {
    }

    public WskDeployManifest(String filePath, String fullFilePath, String fileName) {
        this.path = filePath;
        this.fullPath = fullFilePath;
        this.name = fileName;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getFullPath() {
        return fullPath;
    }

    public void setFullPath(String fullPath) {
        this.fullPath = fullPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WskDeployManifest manifest = (WskDeployManifest) o;

        if (path != null ? !path.equals(manifest.path) : manifest.path != null) return false;
        if (fullPath != null ? !fullPath.equals(manifest.fullPath) : manifest.fullPath != null) return false;
        return name != null ? name.equals(manifest.name) : manifest.name == null;
    }

    @Override
    public int hashCode() {
        int result = path != null ? path.hashCode() : 0;
        result = 31 * result + (fullPath != null ? fullPath.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
