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

package com.navercorp.openwhisk.intellij.explorer.toolwindow.actiongroup;

import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.navercorp.openwhisk.intellij.common.whisk.model.WhiskAuth;
import com.navercorp.openwhisk.intellij.common.whisk.model.pkg.WhiskPackage;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.action.DeletePackageAction;
import com.navercorp.openwhisk.intellij.explorer.toolwindow.action.EditPackageAction;

public class WhiskPackageGroup extends DefaultActionGroup {

    public WhiskPackageGroup(WhiskAuth whiskAuth, WhiskPackage whiskPackage) {
        add(new EditPackageAction(whiskAuth, whiskPackage));
        add(new DeletePackageAction(whiskAuth, whiskPackage));
    }
}
