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

package org.jetbrains.jet.lang.types.expressions;

import com.google.common.collect.Lists;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.jet.lang.descriptors.*;
import org.jetbrains.jet.lang.descriptors.annotations.AnnotationDescriptor;
import org.jetbrains.jet.lang.descriptors.impl.SimpleFunctionDescriptorImpl;
import org.jetbrains.jet.lang.descriptors.impl.TypeParameterDescriptorImpl;
import org.jetbrains.jet.lang.descriptors.impl.ValueParameterDescriptorImpl;
import org.jetbrains.jet.lang.psi.*;
import org.jetbrains.jet.lang.resolve.BindingContextUtils;
import org.jetbrains.jet.lang.resolve.BindingTrace;
import org.jetbrains.jet.lang.resolve.calls.autocasts.DataFlowInfo;
import org.jetbrains.jet.lang.resolve.calls.inference.ConstraintSystem;
import org.jetbrains.jet.lang.resolve.calls.inference.InferenceErrorData;
import org.jetbrains.jet.lang.resolve.calls.model.MutableDataFlowInfoForArguments;
import org.jetbrains.jet.lang.resolve.calls.model.ResolvedCall;
import org.jetbrains.jet.lang.resolve.calls.model.ResolvedCallWithTrace;
import org.jetbrains.jet.lang.resolve.calls.results.OverloadResolutionResults;
import org.jetbrains.jet.lang.resolve.calls.tasks.ResolutionCandidate;
import org.jetbrains.jet.lang.resolve.calls.tasks.TracingStrategy;
import org.jetbrains.jet.lang.resolve.calls.util.CallMaker;
import org.jetbrains.jet.lang.resolve.name.Name;
import org.jetbrains.jet.lang.resolve.scopes.JetScope;
import org.jetbrains.jet.lang.resolve.scopes.receivers.ReceiverValue;
import org.jetbrains.jet.lang.types.*;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static org.jetbrains.jet.lang.resolve.BindingContext.CALL;
import static org.jetbrains.jet.lang.resolve.BindingContext.RESOLVED_CALL;

public class ControlStructureTypingUtils {
    private ControlStructureTypingUtils() {
    }

    /*package*/ static ResolvedCall<FunctionDescriptor> resolveIfAsCall(
            @NotNull Call callForIf,
            @NotNull ExpressionTypingContext context,
            @NotNull DataFlowInfo thenInfo,
            @NotNull DataFlowInfo elseInfo
    ) {
        List<AnnotationDescriptor> noAnnotations = Collections.emptyList();
        Name specialFunctionName = Name.identifierNoValidate("<SPECIAL-FUNCTION-FOR-IF-RESOLVE>");

        SimpleFunctionDescriptorImpl ifFunction = new SimpleFunctionDescriptorImpl(
                ErrorUtils.getErrorModule(),//todo hack to avoid returning true in 'isError(DeclarationDescriptor)'
                noAnnotations, specialFunctionName, CallableMemberDescriptor.Kind.DECLARATION);

        TypeParameterDescriptor typeParameter = TypeParameterDescriptorImpl.createWithDefaultBound(
                ifFunction, noAnnotations, false, Variance.INVARIANT, Name.identifier("T"), 0);

        JetType type = new JetTypeImpl(typeParameter.getTypeConstructor(), JetScope.EMPTY);

        ValueParameterDescriptorImpl thenValueParameter = new ValueParameterDescriptorImpl(
                ifFunction, 0, noAnnotations, Name.identifier("thenBranch"), type, false, null);
        ValueParameterDescriptorImpl elseValueParameter = new ValueParameterDescriptorImpl(
                ifFunction, 1, noAnnotations, Name.identifier("elseBranch"), type, false, null);
        ifFunction.initialize(
                null,
                ReceiverParameterDescriptor.NO_RECEIVER_PARAMETER,
                Lists.newArrayList(typeParameter),
                Lists.<ValueParameterDescriptor>newArrayList(thenValueParameter, elseValueParameter),
                type,
                Modality.FINAL,
                Visibilities.PUBLIC,
                /*isInline = */ false
        );

        JetReferenceExpression ifReference = JetPsiFactory.createSimpleName(context.expressionTypingServices.getProject(), "fakeIfCall");
        TracingStrategy tracingForIf = createTracingForIf(callForIf);
        MutableDataFlowInfoForArguments dataFlowInfoForArguments = createDataFlowInfoForArgumentsForCall(callForIf, thenInfo, elseInfo);
        ResolutionCandidate<CallableDescriptor> resolutionCandidate = ResolutionCandidate.<CallableDescriptor>create(ifFunction, null);
        OverloadResolutionResults<FunctionDescriptor>
                results = context.expressionTypingServices.getCallResolver().resolveCallWithKnownCandidate(
                callForIf, tracingForIf, ifReference, context, resolutionCandidate, dataFlowInfoForArguments);
        assert results.isSingleResult() : "Not single result after resolving one known candidate";
        return results.getResultingCall();
    }

    private static MutableDataFlowInfoForArguments createDataFlowInfoForArgumentsForCall(
            final Call callForIf, final DataFlowInfo thenInfo, final DataFlowInfo elseInfo
    ) {
        return new MutableDataFlowInfoForArguments() {
            private DataFlowInfo initialDataFlowInfo;

            @Override
            public void setInitialDataFlowInfo(@NotNull DataFlowInfo dataFlowInfo) {
                this.initialDataFlowInfo = dataFlowInfo;
            }

            @Override
            public void updateInfo(@NotNull ValueArgument valueArgument, @NotNull DataFlowInfo dataFlowInfo) {
            }

            @NotNull
            @Override
            public DataFlowInfo getInfo(@NotNull ValueArgument valueArgument) {
                if (valueArgument == callForIf.getValueArguments().get(0)) {
                    return thenInfo;
                }
                if (valueArgument == callForIf.getValueArguments().get(1)) {
                    return elseInfo;
                }
                throw new IllegalArgumentException();
            }

            @NotNull
            @Override
            public DataFlowInfo getResultInfo() {
                return initialDataFlowInfo;
            }
        };
    }

    /*package*/ static Call createCallForIf(
            final JetIfExpression ifExpression,
            @NotNull JetExpression thenBranch,
            @NotNull JetExpression elseBranch
    ) {
        final List<ValueArgument> valueArguments = Lists.newArrayList(
                CallMaker.makeValueArgument(thenBranch, thenBranch),
                CallMaker.makeValueArgument(elseBranch, elseBranch));
        return new Call() {
            @Nullable
            @Override
            public ASTNode getCallOperationNode() {
                return ifExpression.getNode();
            }

            @NotNull
            @Override
            public ReceiverValue getExplicitReceiver() {
                return ReceiverValue.NO_RECEIVER;
            }

            @NotNull
            @Override
            public ReceiverValue getThisObject() {
                return ReceiverValue.NO_RECEIVER;
            }

            @Nullable
            @Override
            public JetExpression getCalleeExpression() {
                return ifExpression;
            }

            @Nullable
            @Override
            public JetValueArgumentList getValueArgumentList() {
                return null;
            }

            @NotNull
            @Override
            public List<? extends ValueArgument> getValueArguments() {
                return valueArguments;
            }

            @NotNull
            @Override
            public List<JetExpression> getFunctionLiteralArguments() {
                return Collections.emptyList();
            }

            @NotNull
            @Override
            public List<JetTypeProjection> getTypeArguments() {
                return Collections.emptyList();
            }

            @Nullable
            @Override
            public JetTypeArgumentList getTypeArgumentList() {
                return null;
            }

            @NotNull
            @Override
            public PsiElement getCallElement() {
                return ifExpression;
            }

            @NotNull
            @Override
            public CallType getCallType() {
                return CallType.DEFAULT;
            }
        };
    }

    /*package*/ static TracingStrategy createTracingForIf(final @NotNull Call callForIf) {
        class CheckTypeContext {
            public BindingTrace trace;
            public JetType expectedType;

            CheckTypeContext(@NotNull BindingTrace trace, @NotNull JetType expectedType) {
                this.trace = trace;
                this.expectedType = expectedType;
            }
        }

        final JetVisitor<Void, CheckTypeContext> checkTypeVisitor = new JetVisitor<Void, CheckTypeContext>() {
            private void checkExpressionType(@Nullable JetExpression expression, CheckTypeContext c) {
                if (expression == null) return;
                expression.accept(this, c);
            }

            @Override
            public Void visitIfExpression(JetIfExpression ifExpression, CheckTypeContext c) {
                checkExpressionType(ifExpression.getThen(), c);
                checkExpressionType(ifExpression.getElse(), c);
                return null;
            }

            @Override
            public Void visitBlockExpression(JetBlockExpression expression, CheckTypeContext c) {
                List<JetElement> statements = expression.getStatements();
                if (!statements.isEmpty()) {
                    JetElement lastElement = statements.get(statements.size() - 1);
                    if (lastElement instanceof JetExpression) {
                        checkExpressionType((JetExpression) lastElement, c);
                    }
                }
                return null;
            }

            @Override
            public Void visitExpression(JetExpression expression, CheckTypeContext c) {
                JetTypeInfo typeInfo = BindingContextUtils.getRecordedTypeInfo(expression, c.trace.getBindingContext());
                if (typeInfo != null) {
                    DataFlowUtils.checkType(typeInfo.getType(), expression, c.expectedType, typeInfo.getDataFlowInfo(), c.trace);
                }
                return null;
            }
        };

        return new ThrowingOnErrorTracingStrategy("resolve 'if' as a call") {
            @Override
            public <D extends CallableDescriptor> void bindReference(
                    @NotNull BindingTrace trace, @NotNull ResolvedCallWithTrace<D> resolvedCall
            ) {
                //do nothing
            }

            @Override
            public <D extends CallableDescriptor> void bindResolvedCall(
                    @NotNull BindingTrace trace, @NotNull ResolvedCallWithTrace<D> resolvedCall
            ) {
                trace.record(RESOLVED_CALL, callForIf.getCalleeExpression(), resolvedCall);
                trace.record(CALL, callForIf.getCalleeExpression(), callForIf);

            }

            @Override
            public void typeInferenceFailed(
                    @NotNull BindingTrace trace, @NotNull InferenceErrorData.ExtendedInferenceErrorData data
            ) {
                ConstraintSystem constraintSystem = data.constraintSystem;
                assert !constraintSystem.isSuccessful() : "Report error only for not successful constraint system";

                if (constraintSystem.hasErrorInConstrainingTypes()) {
                    return;
                }
                if (constraintSystem.hasOnlyExpectedTypeMismatch()) {
                    JetExpression expression = callForIf.getCalleeExpression();
                    if (expression != null) {
                        expression.accept(checkTypeVisitor, new CheckTypeContext(trace, data.expectedType));
                    }
                    return;
                }
                super.typeInferenceFailed(trace, data);
            }
        };
    }
    
    private abstract static class ThrowingOnErrorTracingStrategy implements TracingStrategy {
        private final String debugName;

        protected ThrowingOnErrorTracingStrategy(String debugName) {
            this.debugName = debugName;
        }

        private void throwError() {
            throw new IllegalStateException("Resolution error of this type shouldn't occur for " + debugName);
        }

        @Override
        public void unresolvedReference(@NotNull BindingTrace trace) {
            throwError();
        }

        @Override
        public <D extends CallableDescriptor> void unresolvedReferenceWrongReceiver(
                @NotNull BindingTrace trace, @NotNull Collection<ResolvedCallWithTrace<D>> candidates
        ) {
            throwError();
        }

        @Override
        public <D extends CallableDescriptor> void recordAmbiguity(
                @NotNull BindingTrace trace, @NotNull Collection<ResolvedCallWithTrace<D>> candidates
        ) {
            throwError();
        }

        @Override
        public void missingReceiver(
                @NotNull BindingTrace trace, @NotNull ReceiverParameterDescriptor expectedReceiver
        ) {
            throwError();
        }

        @Override
        public void wrongReceiverType(
                @NotNull BindingTrace trace, @NotNull ReceiverParameterDescriptor receiverParameter, @NotNull ReceiverValue receiverArgument
        ) {
            throwError();
        }

        @Override
        public void noReceiverAllowed(@NotNull BindingTrace trace) {
            throwError();
        }

        @Override
        public void noValueForParameter(
                @NotNull BindingTrace trace, @NotNull ValueParameterDescriptor valueParameter
        ) {
            throwError();
        }

        @Override
        public void wrongNumberOfTypeArguments(@NotNull BindingTrace trace, int expectedTypeArgumentCount) {
            throwError();
        }

        @Override
        public <D extends CallableDescriptor> void ambiguity(
                @NotNull BindingTrace trace, @NotNull Collection<ResolvedCallWithTrace<D>> descriptors
        ) {
            throwError();
        }

        @Override
        public <D extends CallableDescriptor> void noneApplicable(
                @NotNull BindingTrace trace, @NotNull Collection<ResolvedCallWithTrace<D>> descriptors
        ) {
            throwError();
        }

        @Override
        public <D extends CallableDescriptor> void cannotCompleteResolve(
                @NotNull BindingTrace trace, @NotNull Collection<ResolvedCallWithTrace<D>> descriptors
        ) {
            throwError();
        }

        @Override
        public void instantiationOfAbstractClass(@NotNull BindingTrace trace) {
            throwError();
        }

        @Override
        public void unsafeCall(
                @NotNull BindingTrace trace, @NotNull JetType type, boolean isCallForImplicitInvoke
        ) {
            throwError();
        }

        @Override
        public void unnecessarySafeCall(
                @NotNull BindingTrace trace, @NotNull JetType type
        ) {
            throwError();
        }

        @Override
        public void danglingFunctionLiteralArgumentSuspected(
                @NotNull BindingTrace trace, @NotNull List<JetExpression> functionLiteralArguments
        ) {
            throwError();
        }

        @Override
        public void invisibleMember(
                @NotNull BindingTrace trace, @NotNull DeclarationDescriptorWithVisibility descriptor
        ) {
            throwError();
        }

        @Override
        public void typeInferenceFailed(
                @NotNull BindingTrace trace, @NotNull InferenceErrorData.ExtendedInferenceErrorData inferenceErrorData
        ) {
            throwError();
        }

        @Override
        public void upperBoundViolated(
                @NotNull BindingTrace trace, @NotNull InferenceErrorData inferenceErrorData
        ) {
            throwError();
        }
    }
}
