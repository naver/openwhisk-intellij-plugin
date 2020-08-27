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
import com.navercorp.openwhisk.intellij.common.whisk.model.wskdeploy.WskDeployCmdResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class CommandUtils {

    private static final Logger LOG = Logger.getInstance(CommandUtils.class);

    protected CommandUtils() {
        throw new UnsupportedOperationException("Utility classes should not have a public or default constructor.");
    }

    public static WskDeployCmdResponse runCommand(String[] command)
            throws IOException, InterruptedException {
        ProcessBuilder builder = new ProcessBuilder(command);
        Process process = builder.start();

        // read std output
        BufferedReader reader =
                new BufferedReader(new InputStreamReader(process.getInputStream()));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line + "\n");
        }

        // read error output
        BufferedReader errReader =
                new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errOutput = new StringBuilder();
        String errLine;
        while ((errLine = errReader.readLine()) != null) {
            errOutput.append(errLine + "\n");
        }

        int exitCode = process.waitFor();
        return new WskDeployCmdResponse(exitCode, output.toString(), errOutput.toString());
    }
}
