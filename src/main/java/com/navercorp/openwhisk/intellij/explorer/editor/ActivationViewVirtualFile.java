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

package com.navercorp.openwhisk.intellij.explorer.editor;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileSystem;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.action.WhiskActionMetaData;
import com.navercorp.openwhisk.intellij.common.whisk.model.trigger.WhiskTriggerMetaData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;

public class ActivationViewVirtualFile extends VirtualFile {

    private List<WhiskEndpoint> endpoints;
    private Optional<WhiskAuth> whiskAuth;
    private Optional<WhiskActionMetaData> action;
    private Optional<WhiskTriggerMetaData> trigger;

    private ActivationViewVirtualFileSystem fileSystem;

    public ActivationViewVirtualFile(@NotNull Project project,
                                     List<WhiskEndpoint> endpoints,
                                     Optional<WhiskAuth> whiskAuth,
                                     Optional<WhiskActionMetaData> action,
                                     Optional<WhiskTriggerMetaData> trigger) {
        this.endpoints = endpoints;
        this.whiskAuth = whiskAuth;
        this.action = action;
        this.trigger = trigger;
        this.fileSystem = ActivationViewVirtualFileSystem.getInstance();
    }

    public List<WhiskEndpoint> getEndpoints() {
        return endpoints;
    }

    public Optional<WhiskAuth> getWhiskAuth() {
        return whiskAuth;
    }

    public Optional<WhiskActionMetaData> getAction() {
        return action;
    }

    public Optional<WhiskTriggerMetaData> getTrigger() {
        return trigger;
    }

    @NotNull
    @Override
    public String getName() {
        return "Activation View";
    }

    @NotNull
    @Override
    public VirtualFileSystem getFileSystem() {
        return fileSystem;
    }

    @NotNull
    @Override
    public String getPath() {
        return "";
    }

    @Override
    public boolean isWritable() {
        return false;
    }

    @Override
    public boolean isDirectory() {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    public VirtualFile getParent() {
        return null;
    }

    @Override
    public VirtualFile[] getChildren() {
        return new VirtualFile[0];
    }

    @NotNull
    @Override
    public OutputStream getOutputStream(Object requestor, long newModificationStamp, long newTimeStamp) throws IOException {
        return new ByteArrayOutputStream();
    }

    @NotNull
    @Override
    public byte[] contentsToByteArray() throws IOException {
        return null;
    }

    @Override
    public long getTimeStamp() {
        return 0;
    }

    @Override
    public long getLength() {
        return 0;
    }

    @Override
    public void refresh(boolean asynchronous, boolean recursive, @Nullable Runnable postRunnable) {

    }

    @Override
    public InputStream getInputStream() throws IOException {
        return null;
    }
}
