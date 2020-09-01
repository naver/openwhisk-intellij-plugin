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
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.ExecutableWhiskAction;
import com.navercorp.openwhisk.intellij.common.whisk.model.activation.WhiskActivationWithLogs;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Optional;

import static com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils.writeWhiskActivationToJson;

public class FileUtils {

    private static final Logger LOG = Logger.getInstance(FileUtils.class);

    protected FileUtils() {
        throw new UnsupportedOperationException("Utility classes should not have a public or default constructor.");
    }

    /**
     * It accumulates in the .gradle/caches subfolder and files are not automatically cleared unless you delete the cache directory.
     * TODO Delete the cache file globally.
     * TODO Use LightVirtualFile instead of local file.
     */
    public static VirtualFile writeActionToFile(String basePath, ExecutableWhiskAction action) throws IOException {
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        final String path = basePath + "/" + action.getName() + action.getKindExtension();
        File f = new File(path);
        LOG.info("Open action file: " + f.getAbsolutePath());

        FileWriter fw = new FileWriter(f);

        if (action.getExec().isBinary()) {
            fw.write("This action was created as a zip file, and you can't see the code here.");
        } else {
            String code = Optional.ofNullable(action.getExec().getCode()).orElse("This action is empty");
            fw.write(code);
        }
        fw.close();

        return VfsUtil.findFileByIoFile(f, true);
    }

    /**
     * It accumulates in the .gradle/caches subfolder and files are not automatically cleared unless you delete the cache directory.
     * TODO Delete the cache file globally.
     * TODO Use LightVirtualFile instead of local file.
     */
    public static VirtualFile writeActivationToFile(String basePath, WhiskActivationWithLogs activation) throws IOException {
        File dir = new File(basePath);
        if (!dir.exists()) {
            dir.mkdir();
        }

        File f = new File(basePath + "/" + activation.getName() + "-" + activation.getActivationId() + ".json");
        LOG.info("Open activation file: " + f.getAbsolutePath());

        FileWriter fw = new FileWriter(f);
        fw.write(writeWhiskActivationToJson(activation));
        fw.close();

        return VfsUtil.findFileByIoFile(f, true);
    }
}
