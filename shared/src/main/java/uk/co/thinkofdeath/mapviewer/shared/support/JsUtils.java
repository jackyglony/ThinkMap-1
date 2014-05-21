/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.mapviewer.shared.support;

import java.util.ArrayList;
import java.util.Comparator;

public class JsUtils {

    public static native <T> void sort(ArrayList<T> list, Comparator<T> comparator)/*-{
        var jsComp = function (a, b) {
            return comparator.@java.util.Comparator::compare(Ljava/lang/Object;Ljava/lang/Object;)(a, b);
        };
        list.@java.util.ArrayList::array.sort(jsComp);
    }-*/;

    public static native <T> void sort(T[] o, Comparator<T> comparator)/*-{
        var jsComp = function (a, b) {
            return comparator.@java.util.Comparator::compare(Ljava/lang/Object;Ljava/lang/Object;)(a, b);
        };
        o.sort(jsComp);
    }-*/;
}
