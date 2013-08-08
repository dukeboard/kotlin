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

package org.jetbrains.jet.di;

import com.intellij.openapi.project.Project;
import org.jetbrains.jet.lang.resolve.BindingTrace;
import org.jetbrains.jet.lang.resolve.java.JavaClassFinderImpl;
import org.jetbrains.jet.lang.resolve.java.resolver.TraceBasedExternalSignatureResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.TraceBasedJavaResolverCache;
import org.jetbrains.jet.lang.resolve.java.resolver.TraceBasedErrorReporterProvider;
import org.jetbrains.jet.lang.resolve.java.resolver.PsiBasedMethodSignatureChecker;
import org.jetbrains.jet.lang.resolve.java.JavaDescriptorResolver;
import org.jetbrains.jet.lang.resolve.java.PsiClassFinderImpl;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaClassResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaAnnotationResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaAnnotationArgumentResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaFunctionResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaTypeParameterResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaTypeTransformer;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaValueParameterResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.DeserializedDescriptorResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.AnnotationDescriptorDeserializer;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaNamespaceResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaMemberResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaConstructorResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaPropertyResolver;
import org.jetbrains.jet.lang.resolve.java.resolver.JavaSupertypeResolver;
import org.jetbrains.annotations.NotNull;
import javax.annotation.PreDestroy;

/* This file is generated by org.jetbrains.jet.generators.injectors.GenerateInjectors. DO NOT EDIT! */
public class InjectorForJavaDescriptorResolver {
    
    private final Project project;
    private final BindingTrace bindingTrace;
    private JavaClassFinderImpl javaClassFinder;
    private TraceBasedExternalSignatureResolver traceBasedExternalSignatureResolver;
    private TraceBasedJavaResolverCache traceBasedJavaResolverCache;
    private TraceBasedErrorReporterProvider traceBasedErrorReporterProvider;
    private PsiBasedMethodSignatureChecker psiBasedMethodSignatureChecker;
    private JavaDescriptorResolver javaDescriptorResolver;
    private PsiClassFinderImpl psiClassFinder;
    private JavaClassResolver javaClassResolver;
    private JavaAnnotationResolver javaAnnotationResolver;
    private JavaAnnotationArgumentResolver javaAnnotationArgumentResolver;
    private JavaFunctionResolver javaFunctionResolver;
    private JavaTypeParameterResolver javaTypeParameterResolver;
    private JavaTypeTransformer javaTypeTransformer;
    private JavaValueParameterResolver javaValueParameterResolver;
    private DeserializedDescriptorResolver deserializedDescriptorResolver;
    private AnnotationDescriptorDeserializer annotationDescriptorDeserializer;
    private JavaNamespaceResolver javaNamespaceResolver;
    private JavaMemberResolver javaMemberResolver;
    private JavaConstructorResolver javaConstructorResolver;
    private JavaPropertyResolver javaPropertyResolver;
    private JavaSupertypeResolver javaSupertypeResolver;
    
    public InjectorForJavaDescriptorResolver(
        @NotNull Project project,
        @NotNull BindingTrace bindingTrace
    ) {
        this.project = project;
        this.bindingTrace = bindingTrace;
        this.javaClassFinder = new JavaClassFinderImpl();
        this.traceBasedExternalSignatureResolver = new TraceBasedExternalSignatureResolver();
        this.traceBasedJavaResolverCache = new TraceBasedJavaResolverCache();
        this.traceBasedErrorReporterProvider = new TraceBasedErrorReporterProvider();
        this.psiBasedMethodSignatureChecker = new PsiBasedMethodSignatureChecker();
        this.javaDescriptorResolver = new JavaDescriptorResolver();
        this.psiClassFinder = new PsiClassFinderImpl();
        this.javaClassResolver = new JavaClassResolver();
        this.javaAnnotationResolver = new JavaAnnotationResolver();
        this.javaAnnotationArgumentResolver = new JavaAnnotationArgumentResolver();
        this.javaFunctionResolver = new JavaFunctionResolver();
        this.javaTypeParameterResolver = new JavaTypeParameterResolver();
        this.javaTypeTransformer = new JavaTypeTransformer();
        this.javaValueParameterResolver = new JavaValueParameterResolver();
        this.deserializedDescriptorResolver = new DeserializedDescriptorResolver();
        this.annotationDescriptorDeserializer = new AnnotationDescriptorDeserializer();
        this.javaNamespaceResolver = new JavaNamespaceResolver();
        this.javaMemberResolver = new JavaMemberResolver();
        this.javaConstructorResolver = new JavaConstructorResolver();
        this.javaPropertyResolver = new JavaPropertyResolver();
        this.javaSupertypeResolver = new JavaSupertypeResolver();

        javaClassFinder.setPsiClassFinder(psiClassFinder);

        traceBasedExternalSignatureResolver.setTrace(bindingTrace);

        traceBasedJavaResolverCache.setTrace(bindingTrace);

        traceBasedErrorReporterProvider.setTrace(bindingTrace);

        psiBasedMethodSignatureChecker.setExternalSignatureResolver(traceBasedExternalSignatureResolver);

        this.javaDescriptorResolver.setClassResolver(javaClassResolver);
        this.javaDescriptorResolver.setNamespaceResolver(javaNamespaceResolver);

        this.psiClassFinder.setProject(project);

        javaClassResolver.setAnnotationResolver(javaAnnotationResolver);
        javaClassResolver.setCache(traceBasedJavaResolverCache);
        javaClassResolver.setErrorReporterProvider(traceBasedErrorReporterProvider);
        javaClassResolver.setFunctionResolver(javaFunctionResolver);
        javaClassResolver.setJavaClassFinder(javaClassFinder);
        javaClassResolver.setKotlinDescriptorResolver(deserializedDescriptorResolver);
        javaClassResolver.setMemberResolver(javaMemberResolver);
        javaClassResolver.setNamespaceResolver(javaNamespaceResolver);
        javaClassResolver.setSupertypesResolver(javaSupertypeResolver);
        javaClassResolver.setTypeParameterResolver(javaTypeParameterResolver);

        javaAnnotationResolver.setArgumentResolver(javaAnnotationArgumentResolver);
        javaAnnotationResolver.setClassResolver(javaClassResolver);

        javaAnnotationArgumentResolver.setAnnotationResolver(javaAnnotationResolver);
        javaAnnotationArgumentResolver.setClassResolver(javaClassResolver);

        javaFunctionResolver.setAnnotationResolver(javaAnnotationResolver);
        javaFunctionResolver.setCache(traceBasedJavaResolverCache);
        javaFunctionResolver.setExternalSignatureResolver(traceBasedExternalSignatureResolver);
        javaFunctionResolver.setSignatureChecker(psiBasedMethodSignatureChecker);
        javaFunctionResolver.setTypeParameterResolver(javaTypeParameterResolver);
        javaFunctionResolver.setTypeTransformer(javaTypeTransformer);
        javaFunctionResolver.setValueParameterResolver(javaValueParameterResolver);

        javaTypeParameterResolver.setTypeTransformer(javaTypeTransformer);

        javaTypeTransformer.setClassResolver(javaClassResolver);

        javaValueParameterResolver.setTypeTransformer(javaTypeTransformer);

        deserializedDescriptorResolver.setAnnotationDeserializer(annotationDescriptorDeserializer);
        deserializedDescriptorResolver.setJavaClassResolver(javaClassResolver);
        deserializedDescriptorResolver.setJavaNamespaceResolver(javaNamespaceResolver);

        annotationDescriptorDeserializer.setJavaClassResolver(javaClassResolver);
        annotationDescriptorDeserializer.setPsiClassFinder(psiClassFinder);

        javaNamespaceResolver.setCache(traceBasedJavaResolverCache);
        javaNamespaceResolver.setDeserializedDescriptorResolver(deserializedDescriptorResolver);
        javaNamespaceResolver.setErrorReporterProvider(traceBasedErrorReporterProvider);
        javaNamespaceResolver.setJavaClassFinder(javaClassFinder);
        javaNamespaceResolver.setMemberResolver(javaMemberResolver);

        javaMemberResolver.setClassResolver(javaClassResolver);
        javaMemberResolver.setConstructorResolver(javaConstructorResolver);
        javaMemberResolver.setFunctionResolver(javaFunctionResolver);
        javaMemberResolver.setNamespaceResolver(javaNamespaceResolver);
        javaMemberResolver.setPropertyResolver(javaPropertyResolver);

        javaConstructorResolver.setCache(traceBasedJavaResolverCache);
        javaConstructorResolver.setExternalSignatureResolver(traceBasedExternalSignatureResolver);
        javaConstructorResolver.setTypeTransformer(javaTypeTransformer);
        javaConstructorResolver.setValueParameterResolver(javaValueParameterResolver);

        javaPropertyResolver.setAnnotationResolver(javaAnnotationResolver);
        javaPropertyResolver.setCache(traceBasedJavaResolverCache);
        javaPropertyResolver.setExternalSignatureResolver(traceBasedExternalSignatureResolver);
        javaPropertyResolver.setTypeTransformer(javaTypeTransformer);

        javaSupertypeResolver.setClassResolver(javaClassResolver);
        javaSupertypeResolver.setTypeTransformer(javaTypeTransformer);

        psiClassFinder.initialize();

    }
    
    @PreDestroy
    public void destroy() {
    }
    
    public JavaDescriptorResolver getJavaDescriptorResolver() {
        return this.javaDescriptorResolver;
    }
    
    public PsiClassFinderImpl getPsiClassFinder() {
        return this.psiClassFinder;
    }
    
}
