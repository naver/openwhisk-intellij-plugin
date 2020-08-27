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

import com.intellij.openapi.Disposable;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.intellij.util.messages.MessageBusConnection;
import com.intellij.util.messages.Topic;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EventUtils {

    protected EventUtils() {
        throw new UnsupportedOperationException("Utility classes should not have a public or default constructor.");
    }

    @NotNull
    private static MessageBusConnection connect(@NotNull Project project, @Nullable Disposable parentDisposable) {
        MessageBus messageBus = project.getMessageBus();
        return parentDisposable == null ? messageBus.connect(project) : messageBus.connect(parentDisposable);
    }

    public static <T> void subscribe(Project project, @Nullable Disposable parentDisposable, Topic<T> topic, T handler) {
        MessageBusConnection messageBusConnection = connect(project, parentDisposable);
        messageBusConnection.subscribe(topic, handler);

    }

    public static <T> void publish(@Nullable Project project, Topic<T> topic, ParametricRunnable.Basic<T> callback) {
        if (project != null) {
            try {
                MessageBus messageBus = project.getMessageBus();
                T publisher = messageBus.syncPublisher(topic);
                callback.run(publisher);
            } catch (ProcessCanceledException ignore) {
            }
        }

    }
}
