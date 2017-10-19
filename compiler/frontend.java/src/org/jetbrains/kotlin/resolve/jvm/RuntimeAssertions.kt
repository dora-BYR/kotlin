/*
 * Copyright 2010-2016 JetBrains s.r.o.
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

package org.jetbrains.kotlin.resolve.jvm

import com.intellij.openapi.util.text.StringUtil
import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.config.LanguageVersionSettings
import org.jetbrains.kotlin.descriptors.*
import org.jetbrains.kotlin.load.java.typeEnhancement.hasEnhancedNullability
import org.jetbrains.kotlin.psi.*
import org.jetbrains.kotlin.resolve.BindingTrace
import org.jetbrains.kotlin.resolve.calls.callUtil.isSafeCall
import org.jetbrains.kotlin.resolve.calls.checkers.AdditionalTypeChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallChecker
import org.jetbrains.kotlin.resolve.calls.checkers.CallCheckerContext
import org.jetbrains.kotlin.resolve.calls.context.ResolutionContext
import org.jetbrains.kotlin.resolve.calls.model.ResolvedCall
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValue
import org.jetbrains.kotlin.resolve.calls.smartcasts.DataFlowValueFactory
import org.jetbrains.kotlin.resolve.scopes.receivers.ExpressionReceiver
import org.jetbrains.kotlin.resolve.scopes.receivers.ReceiverValue
import org.jetbrains.kotlin.types.KotlinType
import org.jetbrains.kotlin.types.TypeUtils
import org.jetbrains.kotlin.types.isError
import org.jetbrains.kotlin.types.typeUtil.isEffectivelyNullable
import org.jetbrains.kotlin.utils.addToStdlib.safeAs

class RuntimeAssertionInfo(val needNotNullAssertion: Boolean, val message: String) {
    interface DataFlowExtras {
        class OnlyMessage(message: String) : DataFlowExtras {
            override val canBeNull: Boolean get() = true
            override val possibleTypes: Set<KotlinType> get() = setOf()
            override val presentableText: String = message
        }

        val canBeNull: Boolean
        val possibleTypes: Set<KotlinType>
        val presentableText: String
    }

    companion object {
        @JvmStatic fun create(
                expectedType: KotlinType,
                expressionType: KotlinType,
                dataFlowExtras: DataFlowExtras
        ): RuntimeAssertionInfo? {
            fun assertNotNull(): Boolean {
                if (expectedType.isError || expressionType.isError) return false

                // T : Any, T! = T..T?
                // Let T$ will be copy of T! with enhanced nullability.
                // Cases when nullability assertion needed: T! -> T, T$ -> T

                // Expected type either T?, T! or T$
                if (TypeUtils.isNullableType(expectedType) || expectedType.hasEnhancedNullability()) return false

                // Expression type is not nullable and not enhanced (neither T?, T! or T$)
                val isExpressionTypeNullable = TypeUtils.isNullableType(expressionType)
                if (!isExpressionTypeNullable && !expressionType.hasEnhancedNullability()) return false

                // Smart-cast T! or T?
                if (!dataFlowExtras.canBeNull && isExpressionTypeNullable) return false

                return true
            }

            return if (assertNotNull())
                RuntimeAssertionInfo(needNotNullAssertion = true, message = dataFlowExtras.presentableText)
            else
                null
        }
    }
}

private val KtExpression.textForRuntimeAssertionInfo
    get() = StringUtil.trimMiddle(text, 50)

class RuntimeAssertionsDataFlowExtras(
        private val c: ResolutionContext<*>,
        private val dataFlowValue: DataFlowValue,
        private val expression: KtExpression
) : RuntimeAssertionInfo.DataFlowExtras {
    override val canBeNull: Boolean
        get() = c.dataFlowInfo.getStableNullability(dataFlowValue).canBeNull()
    override val possibleTypes: Set<KotlinType>
        get() = c.dataFlowInfo.getCollectedTypes(dataFlowValue)
    override val presentableText: String
        get() = expression.textForRuntimeAssertionInfo
}

object RuntimeAssertionsTypeChecker : AdditionalTypeChecker {
    override fun checkType(expression: KtExpression, expressionType: KotlinType, expressionTypeWithSmartCast: KotlinType, c: ResolutionContext<*>) {
        if (TypeUtils.noExpectedType(c.expectedType)) return

        val assertionInfo = RuntimeAssertionInfo.create(
                c.expectedType,
                expressionType,
                RuntimeAssertionsDataFlowExtras(c, DataFlowValueFactory.createDataFlowValue(expression, expressionType, c), expression)
        )

        if (assertionInfo != null) {
            c.trace.record(JvmBindingContextSlices.RUNTIME_ASSERTION_INFO, expression, assertionInfo)
        }
    }

}

object RuntimeAssertionsOnExtensionReceiverCallChecker : CallChecker {
    override fun check(resolvedCall: ResolvedCall<*>, reportOn: PsiElement, context: CallCheckerContext) {
        if (resolvedCall.call.isSafeCall()) return

        val callee = resolvedCall.resultingDescriptor
        checkReceiver(callee.extensionReceiverParameter, resolvedCall.extensionReceiver, context)
    }

    private fun checkReceiver(receiverParameter: ReceiverParameterDescriptor?, receiverValue: ReceiverValue?, context: CallCheckerContext) {
        if (receiverParameter == null || receiverValue == null) return
        val expressionReceiverValue = receiverValue.safeAs<ExpressionReceiver>() ?: return
        val receiverExpression = expressionReceiverValue.expression
        val c = context.resolutionContext
        val dataFlowValue = DataFlowValueFactory.createDataFlowValue(receiverExpression, receiverValue.type, c)

        val assertionInfo = RuntimeAssertionInfo.create(
                receiverParameter.type,
                receiverValue.type,
                RuntimeAssertionsDataFlowExtras(c, dataFlowValue, receiverExpression)
        )

        if (assertionInfo != null) {
            c.trace.record(JvmBindingContextSlices.RECEIVER_RUNTIME_ASSERTION_INFO, expressionReceiverValue, assertionInfo)
        }
    }
}

object RuntimeAssertionsOnDeclarationBodyChecker {
    @JvmStatic
    fun check(
            declaration: KtDeclaration,
            descriptor: DeclarationDescriptor,
            bindingTrace: BindingTrace,
            languageVersionSettings: LanguageVersionSettings
    ) {
        when {
            declaration is KtProperty && descriptor is VariableDescriptor ->
                checkLocalVariable(declaration, descriptor, bindingTrace, languageVersionSettings)
            declaration is KtFunction && descriptor is FunctionDescriptor ->
                checkFunction(declaration, descriptor, bindingTrace, languageVersionSettings)
            declaration is KtProperty && descriptor is PropertyDescriptor ->
                checkProperty(declaration, descriptor, bindingTrace, languageVersionSettings)
            declaration is KtPropertyAccessor && descriptor is PropertyAccessorDescriptor ->
                checkPropertyAccessor(declaration, descriptor, bindingTrace, languageVersionSettings)
        }
    }

    private fun checkLocalVariable(
            declaration: KtProperty,
            descriptor: VariableDescriptor,
            bindingTrace: BindingTrace,
            languageVersionSettings: LanguageVersionSettings
    ) {
        if (declaration.typeReference != null) return

        checkNullabilityAssertion(declaration.initializer ?: return, descriptor.type, bindingTrace, languageVersionSettings)
    }

    private fun checkFunction(
            declaration: KtFunction,
            descriptor: FunctionDescriptor,
            bindingTrace: BindingTrace,
            languageVersionSettings: LanguageVersionSettings
    ) {
        if (declaration.typeReference != null || declaration.hasBlockBody()) return

        checkNullabilityAssertion(declaration.bodyExpression ?: return, descriptor.returnType ?: return,
                                  bindingTrace, languageVersionSettings)
    }

    private fun checkProperty(
            declaration: KtProperty,
            descriptor: PropertyDescriptor,
            bindingTrace: BindingTrace,
            languageVersionSettings: LanguageVersionSettings
    ) {
        if (declaration.typeReference != null) return

        // TODO nullability assertion on delegate initialization expression, see KT-20823
        if (declaration.hasDelegateExpression()) return

        checkNullabilityAssertion(declaration.initializer ?: return, descriptor.type, bindingTrace, languageVersionSettings)
    }

    private fun checkPropertyAccessor(
            declaration: KtPropertyAccessor,
            descriptor: PropertyAccessorDescriptor,
            bindingTrace: BindingTrace,
            languageVersionSettings: LanguageVersionSettings
    ) {
        if (declaration.property.typeReference != null || declaration.hasBlockBody()) return

        checkNullabilityAssertion(declaration.bodyExpression ?: return, descriptor.correspondingProperty.type,
                                  bindingTrace, languageVersionSettings)
    }


    private fun checkNullabilityAssertion(
            expression: KtExpression,
            declarationType: KotlinType,
            bindingTrace: BindingTrace,
            languageVersionSettings: LanguageVersionSettings
    ) {
        if (declarationType.isEffectivelyNullable()) return

        val expressionType = bindingTrace.getType(expression) ?: return
        if (expressionType.isError) return

        if (!expressionType.hasEnhancedNullability()) return

        bindingTrace.record(
                JvmBindingContextSlices.BODY_RUNTIME_ASSERTION_INFO,
                expression,
                RuntimeAssertionInfo(true, expression.textForRuntimeAssertionInfo)
        )
    }
}