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

package com.navercorp.openwhisk.intellij.explorer.toolwindow;

import com.intellij.notification.NotificationType;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.navercorp.openwhisk.intellij.common.service.WhiskService;
import com.navercorp.openwhisk.intellij.common.utils.JsonParserUtils;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskEndpoint;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskNamespace;
import com.navercorp.openwhisk.intellij.common.whisk.service.WhiskNamespaceService;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.ui.WhiskExplorerWindowForm;
import org.apache.commons.lang.StringUtils;
import com.navercorp.openwhisk.intellij.common.notification.SimpleNotifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class WhiskExplorerWindowFactory implements ToolWindowFactory {
    private static final Logger LOG = Logger.getInstance(WhiskExplorerWindowFactory.class);
    private static final SimpleNotifier NOTIFIER = SimpleNotifier.getInstance();

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        try {
            // Load endpoint from ~/.wskprops
            WhiskService service = ServiceManager.getService(project, WhiskService.class);

            String filterEndpoints = filterEndpoints(service.getEndpoints());
            LOG.info(filterEndpoints);
            List<WhiskEndpoint> endpoints = new ArrayList<>(JsonParserUtils.parseWhiskEndpoints(filterEndpoints));
            WhiskNamespaceService whiskNamespaceService = WhiskNamespaceService.getInstance();

            readWskProps().ifPresent(auth -> {
                Optional<WhiskNamespace> validNamespace = whiskNamespaceService.validateNamespace(auth);
                if (validNamespace.isPresent()) {
                    WhiskNamespace ns = validNamespace.get();
                    Optional<WhiskEndpoint> existEndpoint = hasWhiskEndpoint(endpoints, auth);
                    if (existEndpoint.isPresent()) {
                        Optional<WhiskNamespace> existNamespace = hasWhiskNamespace(existEndpoint.get().getNamespaces(), auth);
                        if (!existNamespace.isPresent()) {
                            int epIndex = endpoints.indexOf(existEndpoint.get());
                            WhiskEndpoint ep = endpoints.get(epIndex);
                            ep.addNamespaces(ns);
                            endpoints.set(epIndex, ep);
                        }
                    } else {
                        endpoints.add(new WhiskEndpoint("endpoint(" + endpoints.size() + ")", auth.getApihost(), Arrays.asList(ns)));
                    }
                } else {
                    final String msg = "Cannot read the " + System.getProperty("user.home") + "/.wskprops file Please check the file.";
                    NOTIFIER.notify(project, msg, NotificationType.WARNING);
                }
            });
            service.setEndpoints(JsonParserUtils.writeEndpointsToJson(endpoints));
            service.loadState(service);
        } catch (IOException e) {
            final String msg = "Endpoints cannot be loaded.";
            LOG.error(msg, e);
            NOTIFIER.notify(project, msg, NotificationType.ERROR);
        }

        // Tree View
        WhiskExplorerWindowForm whiskExplorerWindowForm = new WhiskExplorerWindowForm(project, toolWindow);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(whiskExplorerWindowForm.getContent(), null, false);
        toolWindow.getContentManager().addContent(content);
    }

    private String filterEndpoints(String endpoints) {
        if (StringUtils.isEmpty(endpoints)) {
            return "[]";
        }

        try {
            List<Map<String, Object>> eps = JsonParserUtils.parseListMap(endpoints)
                    .stream()
                    .filter(ep -> {
                        String apihost = (String) ep.get("apihost");
                        return StringUtils.isNotEmpty(apihost);
                    }).collect(Collectors.toList());
            return JsonParserUtils.writeListMapToJson(eps);
        } catch (IOException e) {
            LOG.error("Failed to parse json endpoints to map: ", endpoints);
            return "[]";
        }
    }

    private Optional<WhiskEndpoint> hasWhiskEndpoint(List<WhiskEndpoint> endpoints, WhiskAuth auth) {
        for (WhiskEndpoint ep : endpoints) {
            if (ep.getApihost().equals(auth.getApihost())) {
                return Optional.of(ep);
            }
        }
        return Optional.empty();
    }

    private Optional<WhiskNamespace> hasWhiskNamespace(List<WhiskNamespace> namespaces, WhiskAuth auth) {
        for (WhiskNamespace ns : namespaces) {
            if (ns.getAuth().equals(auth.getAuth())) {
                return Optional.of(ns);
            }
        }
        return Optional.empty();
    }

    private Optional<WhiskAuth> readWskProps() {
        Path path = Paths.get(System.getProperty("user.home") + "/.wskprops");
        Charset cs = StandardCharsets.UTF_8;
        try {
            Map<String, String> wskprops = new HashMap<>();
            for (String prop : Files.readAllLines(path, cs)) {
                String[] p = prop.split("=");
                wskprops.put(p[0], p[1]);
            }
            return Optional.of(new WhiskAuth(wskprops.get("AUTH"), wskprops.get("APIHOST")));
        } catch (IOException e) {
            LOG.error(e);
            return Optional.empty();
        } catch (Exception e) {
            LOG.error(e);
            return Optional.empty();
        }
    }
}
