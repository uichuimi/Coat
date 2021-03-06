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

package org.uichuimi.coat.view.graphic;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TableCell;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

/**
 * This Cell set a read-only TextField as editor. So on editing, user can copy text.
 *
 * @author Lorente Arencibia, Pascual (pasculorente@gmail.com)
 */
public class NaturalCell<S, T> extends TableCell<S, T> {

    /**
     * Creates a new NaturalCell, which replaces the cell with a non-editable TextField.
     */
    public NaturalCell() {
        setEditable(true);
        setPadding(new Insets(5));
        setAlignment(Pos.CENTER_LEFT);
        setOnMouseClicked(event -> getTableView().getSelectionModel().select(getTableRow().getIndex()));
    }

    @Override
    protected void updateItem(T item, boolean empty) {
        super.updateItem(item, empty);
        if (empty) {
            setText(null);
        } else {
            String value = String.valueOf(item);
            setText(value);
            setTooltip(new Tooltip(value));
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        final TextField textField = new TextField(getText());
        textField.setPrefWidth(getWidth());
        textField.setEditable(false);
        textField.selectAll();
        setGraphic(textField);
        setText(null);
        textField.setBackground(null);
        textField.requestFocus();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setGraphic(null);
        setText(String.valueOf(getItem()));
    }
}
