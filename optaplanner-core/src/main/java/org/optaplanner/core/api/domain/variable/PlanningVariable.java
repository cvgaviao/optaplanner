/*
 * Copyright 2011 JBoss Inc
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

package org.optaplanner.core.api.domain.variable;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.util.Comparator;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import org.optaplanner.core.impl.score.director.ScoreDirector;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

/**
 * Specifies that a bean property can be changed and should be optimized by the optimization algorithms.
 * <p/>
 * It is specified on a getter of a java bean property of a {@link PlanningEntity} class.
 */
@Target({METHOD})
@Retention(RUNTIME)
public @interface PlanningVariable {

    /**
     * Any {@link ValueRangeProvider} annotation on a {@link PlanningSolution} or {@link PlanningEntity}
     * will automatically be registered with it's {@link ValueRangeProvider#id()}.
     * <p/>
     * There should be at least 1 valueRangeRef.
     * @return 1 (or more) registered {@link ValueRangeProvider#id()}
     */
    String[] valueRangeProviderRefs() default {};

    /**
     * A nullable planning variable will automatically add the planning value null
     * to the {@link ValueRangeProvider}'s range.
     * <p/>
     * In repeated planning use cases, it's recommended to specify a {@link #reinitializeVariableEntityFilter()}
     * for every nullable planning variable too.
     * <p/>
     * {@link #nullable()} true is not compatible with {@link PlanningVariableGraphType#CHAINED} true.
     * {@link #nullable()} true is not compatible with a primitive property type.
     * @return true if null is a valid value for this planning variable
     */
    boolean nullable() default false;

    /**
     * Construction heuristics only change reinitializable planning variables.
     * Non reinitializable planning variable is ignored by construction heuristics.
     * This is especially useful in repeated planning use cases,
     * in which starting from scratch would waste previous results and time.
     * <p/>
     * If no {@link #reinitializeVariableEntityFilter} is specified,
     * the default considers an entity uninitialized for a variable if its value is null
     * (even if {@link #nullable()} is true).
     * <p/>
     * The method {@link SelectionFilter#accept(ScoreDirector, Object)}
     * returns false if the selection entity should be reinitialized for this variable
     * and it returns true if the selection entity should not be reinitialized for this variable
     * @return {@link NullReinitializeVariableEntityFilter} when it is null (workaround for annotation limitation)
     */
    Class<? extends SelectionFilter> reinitializeVariableEntityFilter()
            default NullReinitializeVariableEntityFilter.class;

    interface NullReinitializeVariableEntityFilter extends SelectionFilter {}

    /**
     * In some use cases, such as Vehicle Routing, planning entities form a specific graph type,
     * as specified by {@link PlanningVariableGraphType}.
     * @return never null, defaults to {@link PlanningVariableGraphType#NONE}
     */
    PlanningVariableGraphType graphType() default PlanningVariableGraphType.NONE;

    /**
     * Allows a collection of planning values for this variable to be sorted by strength.
     * A strengthWeight estimates how strong a planning value is.
     * Some algorithms benefit from planning on weaker planning values first or from focusing on them.
     * <p/>
     * The {@link Comparator} should sort in ascending strength.
     * For example: sorting 3 computers on strength based on their RAM capacity:
     * Computer B (1GB RAM), Computer A (2GB RAM), Computer C (7GB RAM),
     * <p/>
     * Do not use together with {@link #strengthWeightFactoryClass()}.
     * @return {@link NullStrengthComparator} when it is null (workaround for annotation limitation)
     * @see #strengthWeightFactoryClass()
     */
    Class<? extends Comparator> strengthComparatorClass()
            default NullStrengthComparator.class;

    interface NullStrengthComparator extends Comparator {}

    /**
     * The {@link SelectionSorterWeightFactory} alternative for {@link #strengthComparatorClass()}.
     * <p/>
     * Do not use together with {@link #strengthComparatorClass()}.
     * @return {@link NullStrengthWeightFactory} when it is null (workaround for annotation limitation)
     * @see #strengthComparatorClass()
     */
    Class<? extends SelectionSorterWeightFactory> strengthWeightFactoryClass()
            default NullStrengthWeightFactory.class;

    interface NullStrengthWeightFactory extends SelectionSorterWeightFactory {}

}
