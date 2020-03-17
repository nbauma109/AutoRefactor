/*
 * AutoRefactor - Eclipse plugin to automatically refactor Java code bases.
 *
 * Copyright (C) 2014-2016 Jean-Noël Rouvignac - initial API and implementation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program under LICENSE-GNUGPL.  If not, see
 * <http://www.gnu.org/licenses/>.
 *
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution under LICENSE-ECLIPSE, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 */
package org.autorefactor.jdt.internal.ui.fix;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

import org.autorefactor.jdt.core.dom.ASTRewrite;
import org.autorefactor.jdt.internal.corext.dom.ASTNodeFactory;
import org.autorefactor.jdt.internal.corext.dom.ASTNodes;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;

/** See {@link #getDescription()} method. */
public class PushNegationDownCleanUp extends AbstractCleanUpRule {
    /**
     * Get the name.
     *
     * @return the name.
     */
    @Override
    public String getName() {
        return MultiFixMessages.CleanUpRefactoringWizard_PushNegationDownCleanUp_name;
    }

    /**
     * Get the description.
     *
     * @return the description.
     */
    @Override
    public String getDescription() {
        return MultiFixMessages.CleanUpRefactoringWizard_PushNegationDownCleanUp_description;
    }

    /**
     * Get the reason.
     *
     * @return the reason.
     */
    @Override
    public String getReason() {
        return MultiFixMessages.CleanUpRefactoringWizard_PushNegationDownCleanUp_reason;
    }

    @Override
    public boolean visit(final PrefixExpression node) {
        if (!ASTNodes.hasOperator(node, PrefixExpression.Operator.NOT)) {
            return true;
        }

        ASTNodeFactory b= cuRewrite.getASTBuilder();
        Expression replacement= getOppositeExpression(b, node.getOperand());

        if (replacement != null) {
            ASTRewrite rewrite= cuRewrite.getASTRewrite();
            rewrite.replace(node, replacement);
            return false;
        }

        return true;
    }

    private Expression getOppositeExpression(final ASTNodeFactory b, final Expression negativeExpression) {
        Expression operand= ASTNodes.getUnparenthesedExpression(negativeExpression);

        if (operand instanceof PrefixExpression) {
            PrefixExpression pe= (PrefixExpression) operand;

            if (ASTNodes.hasOperator(pe, PrefixExpression.Operator.NOT)) {
                return b.createMoveTarget(pe.getOperand());
            }
        } else if (operand instanceof InfixExpression) {
            InfixExpression ie= (InfixExpression) operand;
            InfixExpression.Operator reverseOp= (InfixExpression.Operator) OperatorEnum.getOperator(ie).getReverseBooleanOperator();

            if (reverseOp != null) {
                List<Expression> allOperands= new ArrayList<>(ASTNodes.allOperands(ie));

                if (ASTNodes.hasOperator(ie, InfixExpression.Operator.CONDITIONAL_AND, InfixExpression.Operator.CONDITIONAL_OR, InfixExpression.Operator.AND, InfixExpression.Operator.OR)) {
                    for (ListIterator<Expression> it= allOperands.listIterator(); it.hasNext();) {
                        Expression anOperand= it.next();
                        Expression oppositeOperand= getOppositeExpression(b, anOperand);

                        it.set(oppositeOperand != null ? oppositeOperand : b.negate(anOperand));
                    }
                } else {
                    allOperands= b.createMoveTarget(allOperands);
                }

                return b.parenthesize(b.infixExpression(reverseOp, allOperands));
            }
        } else {
            Boolean constant= ASTNodes.getBooleanLiteral(operand);

            if (constant != null) {
                return b.boolean0(!constant);
            }
        }

        return null;
    }
}
