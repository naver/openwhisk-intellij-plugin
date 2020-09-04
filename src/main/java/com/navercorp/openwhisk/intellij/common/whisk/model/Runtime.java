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

/**
 *  Set the type of runtime based on upstream
 *    - https://github.com/apache/openwhisk/blob/master/ansible/files/runtimes.json
 *
 *  The runtime used for each endpoint may be different, so use it according to the policy of the endpoint users are using.
 */
public enum Runtime {
    NODE_6("nodejs", ".js", "6", false, true),
    NODE_8("nodejs", ".js", "8", false, true),
    NODE_10("nodejs", ".js", "10", true, false),
    NODE_12("nodejs", ".js", "12", false, false),
    NODE_14("nodejs", ".js", "14", false, false),
    PYTHON_2("python", ".py", "2", false, false),
    PYTHON_3("python", ".py", "3", true, false),
    JAVA("java", ".java", "8", true, false),
    SWIFT_3_1_1("swift", ".swift", "3.1.1", false, true),
    SWIFT_4_2("swift", ".swift", "4.2", true, false),
    SWIFT_5_1("swift", ".swift", "5.1", false, false),
    PHP_7_3("php", ".php", "7.3", false, false),
    PHP_7_4("php", ".php", "7.4", true, false),
    GO_1_11("go", ".go", "1.11", true, false),
    RUBY_2_5("ruby", ".rb", "2.5", true, false),
    SEQUENCE("sequence", "", "", true, false),
    DOCKER("blackbox", "", "", true, false);

    private String name;
    private String extension;
    private String version;
    private boolean defaultRuntime;
    private boolean deprecated;

    Runtime(String name, String extension, String version, boolean defaultRuntime, boolean deprecated) {
        this.name = name;
        this.extension = extension;
        this.version = version;
        this.defaultRuntime = defaultRuntime;
        this.deprecated = deprecated;
    }

    @Override
    public String toString() {
        switch (name) {
            case "java":
                return name;
            default:
                String runtime = name;
                if (version.length() > 0) {
                    runtime += ":" + version;
                }

                if (deprecated) {
                    runtime += " (Deprecated)";
                }
                return runtime;
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
            case "nodejs:14":
                return NODE_14;
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
            case "swift:5.1":
                return SWIFT_5_1;
            case "php:7.3":
                return PHP_7_3;
            case "php:7.4":
                return PHP_7_4;
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

    public static Runtime toRuntime(int index) {
        switch (index) {
            case 0:
                return NODE_6;
            case 1:
                return NODE_8;
            case 2:
                return NODE_10;
            case 3:
                return NODE_12;
            case 4:
                return NODE_14;
            case 5:
                return PYTHON_2;
            case 6:
                return PYTHON_3;
            case 7:
                return JAVA;
            case 8:
                return SWIFT_3_1_1;
            case 9:
                return SWIFT_4_2;
            case 10:
                return SWIFT_5_1;
            case 11:
                return PHP_7_3;
            case 12:
                return PHP_7_4;
            case 13:
                return GO_1_11;
            case 14:
                return RUBY_2_5;
            case 15:
                return SEQUENCE;
            default:
                return DOCKER;
        }
    }

    public static Runtime toCodeType(int index) {
        switch (index) {
            case 0:
                return NODE_6;
            case 1:
                return NODE_8;
            case 2:
                return NODE_10;
            case 3:
                return NODE_12;
            case 4:
                return NODE_14;
            case 5:
                return PYTHON_2;
            case 6:
                return PYTHON_3;
            case 7:
                return JAVA;
            case 8:
                return SWIFT_3_1_1;
            case 9:
                return SWIFT_4_2;
            case 10:
                return SWIFT_5_1;
            case 11:
                return PHP_7_3;
            case 12:
                return PHP_7_4;
            case 13:
                return GO_1_11;
            default:
                return RUBY_2_5;
        }
    }
}
