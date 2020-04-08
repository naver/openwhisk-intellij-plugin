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

package com.navercorp.openwhisk.intellij.common.whisk.model.exec;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CodeExec extends Exec {
    private String kind;
    @Nullable
    private String main;
    private String code;
    private String image; // for docker image
    private List<String> components = new ArrayList<>(); // for sequence action

    public CodeExec() {
    }

    public CodeExec(String kind, @Nullable String main, String code, String image, List<String> components) {
        this.kind = kind;
        this.main = main;
        this.code = code;
        this.image = image;
        this.components = components;
    }

    public CodeExec(boolean binary, String kind, @Nullable String main, String code, String image, List<String> components) {
        super(binary);
        this.kind = kind;
        this.main = main;
        this.code = code;
        this.image = image;
        this.components = components;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMain() {
        return main;
    }

    public void setMain(String main) {
        this.main = main;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getComponents() {
        return components;
    }

    public void setComponents(List<String> components) {
        this.components = components;
    }

    public ExecMetaData toExecMetaData() {
        return new ExecMetaData(isBinary());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        CodeExec codeExec = (CodeExec) o;

        if (kind != null ? !kind.equals(codeExec.kind) : codeExec.kind != null) return false;
        if (main != null ? !main.equals(codeExec.main) : codeExec.main != null) return false;
        if (code != null ? !code.equals(codeExec.code) : codeExec.code != null) return false;
        if (image != null ? !image.equals(codeExec.image) : codeExec.image != null) return false;
        return components != null ? components.equals(codeExec.components) : codeExec.components == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (kind != null ? kind.hashCode() : 0);
        result = 31 * result + (main != null ? main.hashCode() : 0);
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (image != null ? image.hashCode() : 0);
        result = 31 * result + (components != null ? components.hashCode() : 0);
        return result;
    }
}
