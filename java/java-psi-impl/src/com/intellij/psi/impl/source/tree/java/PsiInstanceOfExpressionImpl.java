// Copyright 2000-2019 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package com.intellij.psi.impl.source.tree.java;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.Constants;
import com.intellij.psi.impl.source.tree.ChildRole;
import com.intellij.psi.tree.ChildRoleBase;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PsiInstanceOfExpressionImpl extends ExpressionPsiElement implements PsiInstanceOfExpression, Constants {
  private static final Logger LOG = Logger.getInstance(PsiInstanceOfExpressionImpl.class);

  public PsiInstanceOfExpressionImpl() {
    super(INSTANCE_OF_EXPRESSION);
  }

  @Override
  @NotNull
  public PsiExpression getOperand() {
    return (PsiExpression)findChildByRoleAsPsiElement(ChildRole.OPERAND);
  }

  @Override
  public PsiTypeElement getCheckType() {
    PsiPattern pattern = getPattern();
    if (!(pattern instanceof PsiTypeTestPattern)) return null;
    return ((PsiTypeTestPattern)pattern).getCheckType();
  }

  @Override
  public PsiType getType() {
    return PsiType.BOOLEAN;
  }

  @Override
  public ASTNode findChildByRole(int role) {
    LOG.assertTrue(ChildRole.isUnique(role));
    switch(role){
      default:
        return null;

      case ChildRole.OPERAND:
        return findChildByType(EXPRESSION_BIT_SET);

      case ChildRole.INSTANCEOF_KEYWORD:
        return findChildByType(INSTANCEOF_KEYWORD);
    }
  }

  @Override
  public int getChildRole(@NotNull ASTNode child) {
    LOG.assertTrue(child.getTreeParent() == this);
    IElementType i = child.getElementType();
    if (i == INSTANCEOF_KEYWORD) {
      return ChildRole.INSTANCEOF_KEYWORD;
    }
    if (EXPRESSION_BIT_SET.contains(child.getElementType())) {
      return ChildRole.OPERAND;
    }
    return ChildRoleBase.NONE;
  }

  @Nullable
  @Override
  public PsiPattern getPattern() {
    return PsiTreeUtil.getChildOfType(this, PsiPattern.class);
  }

  @Override
  public void accept(@NotNull PsiElementVisitor visitor) {
    if (visitor instanceof JavaElementVisitor) {
      ((JavaElementVisitor)visitor).visitInstanceOfExpression(this);
    }
    else {
      visitor.visitElement(this);
    }
  }

  @Override
  public String toString() {
    return "PsiInstanceofExpression";
  }
}

