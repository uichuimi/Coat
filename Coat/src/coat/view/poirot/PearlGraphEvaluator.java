/*
 * Copyright (c) UICHUIMI 2016
 *
 * This file is part of Coat.
 *
 * Coat is free software: you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Coat is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Foobar.
 * If not, see <http://www.gnu.org/licenses/>.
 */

package coat.view.poirot;

import poirot.core.PearlGraph;
import poirot.view.GraphEvaluator;
import poirot.view.PhenotypesGraphEvaluator;

/**
 * Created by uichuimi on 20/05/16.
 */
class PearlGraphEvaluator {

    static void evaluatePhenotypes(PearlGraph pearlGraph) {
        new GraphEvaluator(pearlGraph).run();
    }
}
