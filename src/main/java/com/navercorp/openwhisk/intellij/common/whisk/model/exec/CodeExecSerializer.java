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

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.Optional;

public class CodeExecSerializer extends JsonSerializer<CodeExec> {
    @Override
    public void serialize(CodeExec codeExec, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeStartObject();

        jsonGenerator.writeFieldName("binary");
        jsonGenerator.writeBoolean(codeExec.isBinary());

        jsonGenerator.writeFieldName("kind");
        jsonGenerator.writeString(codeExec.getKind());

        if (Optional.ofNullable(codeExec.getCode()).isPresent()) {
            jsonGenerator.writeFieldName("code");
            jsonGenerator.writeString(codeExec.getCode());
        }

        if (Optional.ofNullable(codeExec.getMain()).isPresent()) {
            jsonGenerator.writeFieldName("main");
            jsonGenerator.writeString(codeExec.getMain());
        }

        if (Optional.ofNullable(codeExec.getImage()).isPresent()) {
            jsonGenerator.writeFieldName("image");
            jsonGenerator.writeString(codeExec.getImage());
        }

        if (!codeExec.getComponents().isEmpty()) {
            jsonGenerator.writeFieldName("components");
            jsonGenerator.writeStartArray();
            for (String c : codeExec.getComponents()) {
                jsonGenerator.writeString(c);
            }
            jsonGenerator.writeEndArray();
        }

        jsonGenerator.writeEndObject();
    }
}
