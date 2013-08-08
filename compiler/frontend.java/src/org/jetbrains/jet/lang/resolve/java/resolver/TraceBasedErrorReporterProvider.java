/*
 * Copyright 2010-2013 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.jet.lang.resolve.java.resolver;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.resolve.BindingTrace;
import org.jetbrains.jet.lang.resolve.java.AbiVersionUtil;
import org.jetbrains.jet.lang.resolve.java.structure.JavaClass;

import javax.inject.Inject;

public class TraceBasedErrorReporterProvider implements ErrorReporterProvider {
    private BindingTrace trace;

    @Inject
    public void setTrace(BindingTrace trace) {
        this.trace = trace;
    }

    @NotNull
    @Override
    public ErrorReporter createForClass(@NotNull final JavaClass javaClass) {
        return new ErrorReporter() {
            @Override
            public void reportIncompatibleAbiVersion(int actualVersion) {
                AbiVersionUtil.reportIncompatibleAbiVersion(javaClass.getPsi(), actualVersion, trace);
            }
        };
    }
}