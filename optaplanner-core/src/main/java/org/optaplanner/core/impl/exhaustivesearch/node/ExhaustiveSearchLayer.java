/*
 * Copyright 2014 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.impl.exhaustivesearch.node;

public class ExhaustiveSearchLayer {

    private final int depth;
    private final Object entity;
    private final int uninitializedVariableCount;

    private long nextBreadth;

    public ExhaustiveSearchLayer(int depth, Object entity, int uninitializedVariableCount) {
        this.depth = depth;
        this.entity = entity;
        this.uninitializedVariableCount = uninitializedVariableCount;
        nextBreadth = 0L;
    }

    public int getDepth() {
        return depth;
    }

    public Object getEntity() {
        return entity;
    }

    public int getUninitializedVariableCount() {
        return uninitializedVariableCount;
    }

    // ************************************************************************
    // Calculated methods
    // ************************************************************************

    public long assignBreadth() {
        long breadth = nextBreadth;
        nextBreadth++;
        return breadth;
    }

    @Override
    public String toString() {
        return Integer.toString(depth);
    }

}
