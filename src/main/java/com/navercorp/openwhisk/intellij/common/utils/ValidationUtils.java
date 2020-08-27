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

package com.navercorp.openwhisk.intellij.common.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.vfs.VirtualFile;
import com.navercorp.openwhisk.intellij.common.whisk.model.wskdeploy.WskDeployCmdResponse;

import java.io.IOException;
import java.util.Optional;

import static com.navercorp.openwhisk.intellij.common.utils.CommandUtils.runCommand;

public class ValidationUtils {
    private static final Logger LOG = Logger.getInstance(ValidationUtils.class);

    protected ValidationUtils() {
        throw new UnsupportedOperationException("Utility classes should not have a public or default constructor.");
    }

    public static Optional<VirtualFile> validateWskDeploy(Optional<VirtualFile> wskdeploy) {
        return wskdeploy.flatMap(wskdeployFile -> {
            String[] cmd = new String[]{wskdeployFile.getPath(), "version"};
            try {
                WskDeployCmdResponse response = runCommand(cmd);
                if (response.getExistCode() == 0) {
                    return Optional.of(wskdeployFile);
                } else {
                    LOG.error(response.getErrorOutput());
                    return Optional.empty();
                }
            } catch (IOException | InterruptedException e) {
                LOG.error(e);
                return Optional.empty();
            }
        });
    }
}
