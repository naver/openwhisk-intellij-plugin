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

package com.navercorp.openwhisk.intellij.common.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationDisplayType;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class SimpleNotifier {

    private SimpleNotifier() {
    }

    private static class LazyHolder {
        private static final SimpleNotifier INSTANCE = new SimpleNotifier();
    }

    public static SimpleNotifier getInstance() {
        return LazyHolder.INSTANCE;
    }

    private static final NotificationGroup NOTIFICATION_GROUP =
            new NotificationGroup("Simple Notification Group", NotificationDisplayType.STICKY_BALLOON, true);

    public Notification notify(@NotNull String content, @NotNull final NotificationType type) {
        return notify(null, content, type);
    }

    public Notification notify(Project project, @NotNull String content, @NotNull final NotificationType type) {
        final Notification notification = NOTIFICATION_GROUP.createNotification(content, type);
        notification.notify(project);
        return notification;
    }
}
