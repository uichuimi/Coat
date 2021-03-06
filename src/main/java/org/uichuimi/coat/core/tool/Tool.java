/*
 * Copyright (c) UICHUIMI 2017
 *
 * This file is part of Coat.
 *
 * Coat is free software:
 * you can redistribute it and/or modify it under the terms of the GNU
 * General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 *
 * Coat is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with Coat.
 *
 * If not, see <http://www.gnu.org/licenses/>.
 *
 */

package org.uichuimi.coat.core.tool;

import javafx.beans.property.Property;
import javafx.scene.layout.VBox;

/**
 * Defines a tool to show in the main panel of the application. This class extends VBox, so you can place things by
 * using <code>getChildren().addAll(nodes)</code>.
 *
 * @author Lorente Arencibia, Pascual (pasculorente@gmail.com)
 */
public abstract class Tool extends VBox {

    public abstract Property<String> titleProperty();

    /**
     * Implement this method if you want that something happens when user clicks on File->Save as...
     */
    public void saveAs() {

    }

}
