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

public class Limits {
    private int concurrency;
    private int logs;
    private int memory;
    private int timeout;

    public Limits() {
    }

    public Limits(int concurrency, int logs, int memory, int timeout) {
        this.concurrency = concurrency;
        this.logs = logs;
        this.memory = memory;
        this.timeout = timeout;
    }

    public int getConcurrency() {
        return concurrency;
    }

    public void setConcurrency(int concurrency) {
        this.concurrency = concurrency;
    }

    public int getLogs() {
        return logs;
    }

    public void setLogs(int logs) {
        this.logs = logs;
    }

    public int getMemory() {
        return memory;
    }

    public void setMemory(int memory) {
        this.memory = memory;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Limits limits = (Limits) o;

        if (concurrency != limits.concurrency) return false;
        if (logs != limits.logs) return false;
        if (memory != limits.memory) return false;
        return timeout == limits.timeout;
    }

    @Override
    public int hashCode() {
        int result = concurrency;
        result = 31 * result + logs;
        result = 31 * result + memory;
        result = 31 * result + timeout;
        return result;
    }
}
