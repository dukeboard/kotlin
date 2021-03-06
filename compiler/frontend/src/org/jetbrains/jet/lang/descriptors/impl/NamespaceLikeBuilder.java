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

package org.jetbrains.jet.lang.descriptors.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jet.lang.descriptors.DeclarationDescriptor;
import org.jetbrains.jet.lang.descriptors.PropertyDescriptor;
import org.jetbrains.jet.lang.descriptors.SimpleFunctionDescriptor;

public interface NamespaceLikeBuilder {

    @NotNull
    DeclarationDescriptor getOwnerForChildren();

    void addClassifierDescriptor(@NotNull MutableClassDescriptorLite classDescriptor);

    void addObjectDescriptor(@NotNull MutableClassDescriptorLite objectDescriptor);

    void addFunctionDescriptor(@NotNull SimpleFunctionDescriptor functionDescriptor);

    void addPropertyDescriptor(@NotNull PropertyDescriptor propertyDescriptor);

    enum ClassObjectStatus {
        OK,
        DUPLICATE,
        NOT_ALLOWED
    }

    ClassObjectStatus setClassObjectDescriptor(@NotNull MutableClassDescriptorLite classObjectDescriptor);
}
