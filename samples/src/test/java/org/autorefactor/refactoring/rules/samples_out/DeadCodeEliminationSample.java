/*
 * AutoRefactor - Eclipse plugin to automatically refactor Java code bases.
 *
 * Copyright (C) 2013-2016 Jean-Noël Rouvignac - initial API and implementation
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
package org.autorefactor.refactoring.rules.samples_out;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DeadCodeEliminationSample {

    public int removeEmptyElseClause(boolean b) {
        int i = 0;
        if (b) {
            i++;
        }
        return i;
    }

    public int removeEmptyThenClause(boolean b) {
        int i = 0;
        if (!b) {
            i++;
        }
        return i;
    }

    public int removeEmptyIfStatement(boolean b) {
        int i = 0;
        return i;
    }

// FIXME compilation error.
// Following code:
//    private int removeWhileWithoutIterations() {
//        int i = 0;
//        while (false) {
//            i++;
//        }
//        while (false)
//            i++;
//        return i;
//    }
// Should become
//    private int removeWhileWithoutIterations() {
//        int i = 0;
//        return i;
//    }

    public interface MethodDeclarationWithoutBody {
        void aMethod();
    }

    public void doNotRemovePackageAccessedMethodOverride() {
        MyAbstractList<String> l = new MyAbstractList<>();
        l.removeRange(0, l.size());
    }

    public static class MyAbstractList<E> extends AbstractList<E> {
        @Override
        public E get(int index) {
            return null;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        protected void removeRange(int fromIndex, int toIndex) {
            super.removeRange(fromIndex, toIndex);
        }
    }

    public void removeIfButKeepConditionsWithSideEffects(AtomicBoolean b, AtomicInteger i, Collection<Object> col) {
        b.getAndSet(true);
        col.add(1);
        i.getAndIncrement();
        i.getAndIncrement();
        b.getAndSet(false);
        col.add(2);
    }

    public int replaceDeadCodeByEmptyBlock(int i, byte[] bytes, boolean uselessCondition) {
        for (;i * 13 < 100; i++) {
        }

        for (byte oneByte : bytes) {
        }

        while ((i++) * 13 < 100) {
        }

        do {
        }
        while ((i--) * 13 > -200);

        return i;
    }

    public int replaceDeadCodeByAddingBlock(int i, byte[] bytes, boolean uselessCondition) {
        for (;i * 13 < 100; i++) {
        }

        for (byte oneByte : bytes) {
        }

        while ((i++) * 13 < 100) {
        }

        do {
        }
        while ((i--) * 13 > -200);

        return i;
    }

    public int replaceDeadCodeBySideEffects(int i, byte[] bytes, int j) {
        for (;i * 13 < 100; i++) {
            j++;
        }

        for (byte oneByte : bytes) {
            j++;
        }

        while ((i++) * 13 < 100) {
            j++;
        }

        do {
            j++;
        }
        while ((i--) * 13 > -200);

        return i + j;
    }
}
