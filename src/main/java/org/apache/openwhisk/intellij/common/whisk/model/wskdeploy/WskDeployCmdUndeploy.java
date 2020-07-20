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

package org.apache.openwhisk.intellij.common.whisk.model.wskdeploy;

import org.apache.openwhisk.intellij.common.error.NotExistFileException;
import org.apache.openwhisk.intellij.common.whisk.model.WhiskAuth;

import java.util.Optional;

public class WskDeployCmdUndeploy implements WskDeployCmd {

    private Optional<WskDeployBinary> wskDeployBinary;
    private WskDeployManifest manifest;

    private WskDeployCmdUndeploy() {
    }

    public WskDeployCmdUndeploy(Optional<WskDeployBinary> wskDeployBinary, WskDeployManifest manifest) {
        this.wskDeployBinary = wskDeployBinary;
        this.manifest = manifest;
    }

    @Override
    public String[] toCmd(WhiskAuth auth) {
        return wskDeployBinary
                .map(bin -> new String[]{
                        bin.getFullPath(), "undeploy",
                        "--manifest", manifest.getFullPath(),
                        "--auth", auth.getAuth(),
                        "--apihost", auth.getApihost()
                })
                .orElseThrow(() -> new NotExistFileException("The wskdeploy binary file does not exist."));
    }

    @Override
    public String toCmdString() throws NotExistFileException {
        String[] cmd = wskDeployBinary
                .map(bin -> new String[]{
                        bin.getFullPath(), "undeploy",
                        "--manifest", manifest.getFullPath(),
                })
                .orElseThrow(() -> new NotExistFileException("The wskdeploy binary file does not exist."));
        StringBuilder str = new StringBuilder();
        for (String c : cmd) {
            str.append(c + " ");
        }
        return str.toString();
    }

    @Override
    public String getCmdName() {
        return "undeploy";
    }

    @Override
    public WskDeployManifest getManifest() {
        return manifest;
    }

}
