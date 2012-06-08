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

package org.drools.planner.config.heuristic.selector.value;

import java.util.Collection;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.drools.planner.config.EnvironmentMode;
import org.drools.planner.config.heuristic.selector.SelectorConfig;
import org.drools.planner.config.heuristic.selector.common.SelectionOrder;
import org.drools.planner.core.domain.entity.PlanningEntityDescriptor;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.domain.variable.PlanningVariableDescriptor;
import org.drools.planner.core.heuristic.selector.common.SelectionCacheType;
import org.drools.planner.core.heuristic.selector.value.cached.ProbabilityValueSelector;
import org.drools.planner.core.heuristic.selector.value.FromSolutionPropertyValueSelector;
import org.drools.planner.core.heuristic.selector.value.ValueSelector;
import org.drools.planner.core.heuristic.selector.value.cached.PlanningValueSelectionProbabilityWeightFactory;

@XStreamAlias("valueSelector")
public class ValueSelectorConfig extends SelectorConfig {

    private String planningVariableName = null;
    private SelectionOrder selectionOrder = null;
    private SelectionCacheType cacheType = null;
    // TODO filterClass
    private Class<? extends PlanningValueSelectionProbabilityWeightFactory> selectionProbabilityWeightFactoryClass
            = null;
    // TODO sorterClass, increasingStrength

    public String getPlanningVariableName() {
        return planningVariableName;
    }

    public void setPlanningVariableName(String planningVariableName) {
        this.planningVariableName = planningVariableName;
    }

    public SelectionOrder getSelectionOrder() {
        return selectionOrder;
    }

    public void setSelectionOrder(SelectionOrder selectionOrder) {
        this.selectionOrder = selectionOrder;
    }

    public SelectionCacheType getCacheType() {
        return cacheType;
    }

    public void setCacheType(SelectionCacheType cacheType) {
        this.cacheType = cacheType;
    }

    public Class<? extends PlanningValueSelectionProbabilityWeightFactory> getSelectionProbabilityWeightFactoryClass() {
        return selectionProbabilityWeightFactoryClass;
    }

    public void setSelectionProbabilityWeightFactoryClass(Class<? extends PlanningValueSelectionProbabilityWeightFactory> selectionProbabilityWeightFactoryClass) {
        this.selectionProbabilityWeightFactoryClass = selectionProbabilityWeightFactoryClass;
    }

    // ************************************************************************
    // Builder methods
    // ************************************************************************

    public ValueSelector buildValueSelector(EnvironmentMode environmentMode, SolutionDescriptor solutionDescriptor,
            SelectionOrder inheritedResolvedSelectionOrder, PlanningEntityDescriptor entityDescriptor) {
        PlanningVariableDescriptor variableDescriptor;
        if (planningVariableName != null) {
            variableDescriptor = entityDescriptor.getPlanningVariableDescriptor(planningVariableName);
            if (variableDescriptor == null) {
                throw new IllegalArgumentException("The variableSelector has a planningVariableName ("
                        + planningVariableName + ") for planningEntityClass ("
                        + entityDescriptor.getPlanningEntityClass()
                        + ") that is not annotated as a planningVariable.\n" +
                        "Check your planningEntity implementation's annotated methods.");
            }
        } else {
            Collection<PlanningVariableDescriptor> planningVariableDescriptors = entityDescriptor
                    .getPlanningVariableDescriptors();
            if (planningVariableDescriptors.size() != 1) {
                throw new IllegalArgumentException("The variableSelector has no configured planningVariableName ("
                        + planningVariableName + ") for planningEntityClass ("
                        + entityDescriptor.getPlanningEntityClass()
                        + ") and because there are multiple in the planningVariableNameSet ("
                        + entityDescriptor.getPlanningVariableNameSet()
                        + "), it can not be deducted automatically.");
            }
            variableDescriptor = planningVariableDescriptors.iterator().next();
        }
        SelectionOrder resolvedSelectionOrder = SelectionOrder.resolveSelectionOrder(selectionOrder,
                inheritedResolvedSelectionOrder);
        boolean randomSelection = resolvedSelectionOrder == SelectionOrder.RANDOM
                && selectionProbabilityWeightFactoryClass == null;
        SelectionCacheType resolvedCacheType = cacheType == null ? SelectionCacheType.JUST_IN_TIME : cacheType;
        ValueSelector valueSelector = new FromSolutionPropertyValueSelector(variableDescriptor, randomSelection,
                resolvedCacheType);

        // TODO filterclass

        if (selectionProbabilityWeightFactoryClass != null) {
            if (resolvedSelectionOrder != SelectionOrder.RANDOM) {
                throw new IllegalArgumentException("The valueSelector with selectionProbabilityWeightFactoryClass ("
                        + selectionProbabilityWeightFactoryClass + ") has a non-random resolvedSelectionOrder ("
                        + resolvedSelectionOrder + ").");
            }
            PlanningValueSelectionProbabilityWeightFactory selectionProbabilityWeightFactory;
            try {
                selectionProbabilityWeightFactory = selectionProbabilityWeightFactoryClass.newInstance();
            } catch (InstantiationException e) {
                throw new IllegalArgumentException("selectionProbabilityWeightFactoryClass ("
                        + selectionProbabilityWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            } catch (IllegalAccessException e) {
                throw new IllegalArgumentException("selectionProbabilityWeightFactoryClass ("
                        + selectionProbabilityWeightFactoryClass.getName()
                        + ") does not have a public no-arg constructor", e);
            }
            ProbabilityValueSelector probabilityValueSelector = new ProbabilityValueSelector(resolvedCacheType,
                    selectionProbabilityWeightFactory);
            probabilityValueSelector.setChildValueSelector(valueSelector);
            valueSelector = probabilityValueSelector;
        }
        return valueSelector;
    }

    public void inherit(ValueSelectorConfig inheritedConfig) {
        if (planningVariableName == null) {
            planningVariableName = inheritedConfig.getPlanningVariableName();
        }
        if (selectionOrder == null) {
            selectionOrder = inheritedConfig.getSelectionOrder();
        }
        if (cacheType == null) {
            cacheType = inheritedConfig.getCacheType();
        }
        if (selectionProbabilityWeightFactoryClass == null) {
            selectionProbabilityWeightFactoryClass = inheritedConfig.getSelectionProbabilityWeightFactoryClass();
        }
    }

}
