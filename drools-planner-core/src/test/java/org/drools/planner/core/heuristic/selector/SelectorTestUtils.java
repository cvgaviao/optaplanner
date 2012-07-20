/*
 * Copyright 2012 JBoss Inc
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

package org.drools.planner.core.heuristic.selector;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.entity.EntitySelector;
import org.drools.planner.core.heuristic.selector.value.iterator.IteratorToValueIteratorBridge;
import org.drools.planner.core.heuristic.selector.value.iterator.ValueIterator;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import static org.mockito.Mockito.*;

public class SelectorTestUtils {

    public static PlanningEntityDescriptor mockEntityDescriptor(Class entityClass) {
        PlanningEntityDescriptor entityDescriptor = mock(PlanningEntityDescriptor.class);
        when(entityDescriptor.getPlanningEntityClass()).thenReturn(entityClass);
        return entityDescriptor;
    }

    public static PlanningVariableDescriptor mockVariableDescriptor(Class entityClass, String variableName) {
        PlanningVariableDescriptor variableDescriptor = mock(PlanningVariableDescriptor.class);
        PlanningEntityDescriptor entityDescriptor = mockEntityDescriptor(entityClass);
        when(variableDescriptor.getPlanningEntityDescriptor()).thenReturn(entityDescriptor);
        when(variableDescriptor.getVariableName()).thenReturn(variableName);
        return variableDescriptor;
    }

    public static EntitySelector mockEntitySelector(Class entityClass, Object... entities) {
        PlanningEntityDescriptor entityDescriptor = mockEntityDescriptor(entityClass);
        return mockEntitySelector(entityDescriptor, entities);
    }

    public static EntitySelector mockEntitySelector(PlanningEntityDescriptor entityDescriptor,
            Object... entities) {
        EntitySelector entitySelector = mock(EntitySelector.class);
        when(entitySelector.getEntityDescriptor()).thenReturn(entityDescriptor);
        final List<Object> entityList = Arrays.<Object>asList(entities);
        when(entitySelector.iterator()).thenAnswer(new Answer<Object>() {
            public Iterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                return entityList.iterator();
            }
        });
        when(entitySelector.listIterator()).thenAnswer(new Answer<Object>() {
            public ListIterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                return entityList.listIterator();
            }
        });
        for (int i = 0; i < entityList.size(); i++) {
            final int index = i;
            when(entitySelector.listIterator(index)).thenAnswer(new Answer<Object>() {
                public ListIterator<Object> answer(InvocationOnMock invocation) throws Throwable {
                    return entityList.listIterator(index);
                }
            });
        }
        when(entitySelector.isContinuous()).thenReturn(false);
        when(entitySelector.isNeverEnding()).thenReturn(false);
        when(entitySelector.getSize()).thenReturn((long) entityList.size());
        return entitySelector;
    }

    public static ValueSelector mockValueSelector(Class entityClass, String variableName, Object... values) {
        PlanningVariableDescriptor variableDescriptor = mockVariableDescriptor(entityClass, variableName);
        return mockValueSelector(variableDescriptor, values);
    }

    public static ValueSelector mockValueSelector(PlanningVariableDescriptor variableDescriptor, Object... values) {
        ValueSelector valueSelector = mock(ValueSelector.class);
        when(valueSelector.getVariableDescriptor()).thenReturn(variableDescriptor);
        final List<Object> valueList = Arrays.<Object>asList(values);
        when(valueSelector.iterator()).thenAnswer(new Answer<Object>() {
            public ValueIterator answer(InvocationOnMock invocation) throws Throwable {
                return new IteratorToValueIteratorBridge(valueList.iterator());
            }
        });
        when(valueSelector.isContinuous()).thenReturn(false);
        when(valueSelector.isNeverEnding()).thenReturn(false);
        when(valueSelector.getSize()).thenReturn((long) valueList.size());
        return valueSelector;
    }

    private SelectorTestUtils() {
    }

}
