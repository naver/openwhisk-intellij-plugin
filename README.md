# OpenWhisk Intellij Plugin
**OpenWhisk-intellij-support**  is an open source Intellij Plugin for [Apache OpenWhisk](https://github.com/apache/openwhisk). It assists users to develop/deploy/manage OpenWhisk functions in Intellij.
 
## Prerequisites
Install the dependencies below to use full features:
* Intellij >= 2018.1.8
* [wskdeploy](https://github.com/apache/openwhisk-wskdeploy/releases)

This extension finds the `.wskprops` configuration file located in the home path and connects to the Openwhisk server automatically. Set up your configuration referred to the [cli docs](https://github.com/apache/openwhisk/blob/master/docs/cli.md#openwhisk-cli).

## Feature
### OpenWhisk Explorer
* Explore all entities in your endpoints/namespaces.
    * The `.wskprops` file is automatically registered.
    * You can add the API host manually.
    * You can add namespace manually by API auth key.
* Show the action code with syntax highlighting.
* [Soon] Edit the action code on the remote server.
* Invoke the action remotely and get the activation result.
* Show a list of actions related to the sequence action.
* Show information about the trigger and related rules.
* Show activations related to the action (Same as `wsk activation list <action>`).
* Show detailed information of the activation (Same `as wsk activation get <activation_id>`).
* Update parameters of the action, package, and trigger.

### Manifest View
* List up manifest YAML files in the workspace.
* Deploy/Undeploy OpenWhisk packages with manifest (via wskdeploy).
    * Deploy with the deployment file.
    * Deploy with multiple credentials.

## How to debug in your local
```bash
./gradlew runIde
```

## License

```
Copyright 2020-present NAVER Corp.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
