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

package org.apache.openwhisk.intellij.common.whisk.model;

public enum Runtime {
    @Deprecated NODE_6("nodejs", ".js", "6"),
    @Deprecated NODE_8("nodejs", ".js", "8"),
    NODE_10("nodejs", ".js", "10"),
    NODE_12("nodejs", ".js", "12"),
    PYTHON_2("python", ".py", "2"),
    PYTHON_3("python", ".py", "3"),
    JAVA("java", ".java", "default"),
    SWIFT_3_1_1("swift", ".swift", "3.1.1"),
    SWIFT_4_2("swift", ".swift", "4.2"),
    PHP_7_2("php", ".php", "7.3"),
    GO_1_11("go", ".go", "1.11"),
    RUBY_2_5("ruby", ".rb", "2.5"),
    SEQUENCE("sequence", "", ""),
    DOCKER("docker", "", "");

    private String name;
    private String extension;
    private String version;

    Runtime(String name, String extension, String version) {
        this.name = name;
        this.extension = extension;
        this.version = version;
    }

    public String asString() {
        if (name.equals("java")) {
            return name;
        }

        return name + ":" + version;
    }

    @Override
    public String toString() {
        switch (name) {
            case "java":
                return name;
            case "nodejs":
                switch (version) {
                    case "6":
                    case "8":
                        return name + ":" + version + " (Deprecated)";
                }
        }
        if (version.length() > 0) {
            return name + ":" + version;
        } else {
            return name;
        }
    }

    public static Runtime toRuntime(String kind) {
        switch (kind) {
            case "nodejs:6":
                return NODE_6;
            case "nodejs:8":
                return NODE_8;
            case "nodejs:10":
                return NODE_10;
            case "nodejs:12":
                return NODE_12;
            case "python:2":
                return PYTHON_2;
            case "python:3":
                return PYTHON_3;
            case "java":
                return JAVA;
            case "swift:3.1.1":
                return SWIFT_3_1_1;
            case "swift:4.2":
                return SWIFT_4_2;
            case "php:7.2":
                return PHP_7_2;
            case "go:1.11":
                return GO_1_11;
            case "ruby:2.5":
                return RUBY_2_5;
            case "sequence":
                return SEQUENCE;
            default:
                return DOCKER;
        }
    }
}
