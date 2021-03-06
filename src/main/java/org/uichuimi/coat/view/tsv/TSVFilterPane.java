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

package org.uichuimi.coat.view.tsv;

import org.uichuimi.coat.core.tsv.TSVFilter;
import org.uichuimi.coat.view.graphic.SizableImageView;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * The Graphical aspect of a VCFFilter. It has a lot of buttons.
 *
 * @author Pascual Lorente Arencibia
 */
class TSVFilterPane extends VBox {

    private final Label staticInfo = new Label();
    private final ComboBox<String> field = new ComboBox<>();
    private final ComboBox<TSVFilter.Connector> connector = new ComboBox<>();
    private final TextField value = new TextField();
    private final Button delete = new Button(null, new SizableImageView("/img/black/delete.png", SizableImageView.SMALL_SIZE));
    private final Button accept = new Button(null, new SizableImageView("/img/black/accept.png", SizableImageView.SMALL_SIZE));
    private final Button cancel = new Button(null, new SizableImageView("/img/black/cancel.png", SizableImageView.SMALL_SIZE));
    private final Button view = new Button(null, new SizableImageView("/img/black/view.png", SizableImageView.SMALL_SIZE));
    private final Button voids = new Button(null, new SizableImageView("/img/black/circle.png", SizableImageView.SMALL_SIZE));
    private EventHandler onAccept, onDelete;
    private final HBox center = new HBox(field, connector, value, accept, cancel);
    private TSVFilter filter;

    /**
     * Creates a new TSVFilterPane.
     *
     * @param fields options for the combobox
     */
    public TSVFilterPane(List<String> fields) {
        filter = new TSVFilter();
        field.getItems().addAll(fields);
        initialize();
    }

    private void initialize() {
        getStyleClass().add("filter-box");
        staticInfo.setText("Click to set the filter");
        setAlignment(Pos.CENTER);
        HBox.setHgrow(value, Priority.SOMETIMES);
        center.setSpacing(3);
        connector.getItems().setAll(TSVFilter.Connector.values());

        accept.setOnAction(e -> accept());
        cancel.setOnAction(e -> toPassive());
        setOnMouseClicked(e -> startEdit());
        value.setOnAction(e -> accept());
        delete.setOnAction(e -> delete());
        value.setOnKeyReleased(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                accept();
            } else if (e.getCode() == KeyCode.ESCAPE) {
                toPassive();
            }
        });
        value.getStyleClass().add("fancy-text-field");
        connector.setOnAction(e -> value.requestFocus());
        field.setOnAction(e -> value.requestFocus());
        view.setOnAction(e -> view());
        voids.setOnAction(e -> voids());
        accept.setTooltip(new Tooltip("Accept"));
        cancel.setTooltip(new Tooltip("Cancel changes"));
        delete.setTooltip(new Tooltip("Delete filter"));
        view.setTooltip(new Tooltip("Enable/disble filter"));
        voids.setTooltip(new Tooltip("Restrict null values"));
        toPassive();
    }

    /**
     * Gets the wrapped TSVFilter.
     *
     * @return the TSVFilter
     */
    public TSVFilter getFilter() {
        return filter;
    }

    /**
     * Changes the wrapped filter.
     *
     * @param filter the new TSVFilter
     */
    public void setFilter(TSVFilter filter) {
        this.filter = filter;
        toPassive();
    }

    private void accept() {
        filter.setSelectedIndex(field.getSelectionModel().getSelectedIndex());
        filter.setSelectedConnector(connector.getValue());
        filter.setValue(value.getText());
        getChildren().clear();
        setStaticInfo();
        toPassive();
        if (onAccept != null) onAccept.handle(new ActionEvent());
    }

    private void toPassive() {
        Separator separator = new Separator(Orientation.HORIZONTAL);
        separator.setVisible(false);
        HBox.setHgrow(separator, Priority.ALWAYS);
        HBox box = new HBox(staticInfo, separator, voids, view, delete);
        box.setAlignment(Pos.CENTER);
        getChildren().setAll(box);
    }

    private void startEdit() {
        if (connector.getSelectionModel().isEmpty()) {
            connector.getSelectionModel().select(TSVFilter.Connector.EQUALS);
        }
        if (field.getSelectionModel().isEmpty()) {
            field.getSelectionModel().select(0);
        }
        getChildren().setAll(center);
    }

    private void setStaticInfo() {
        String f = (filter.getSelectedIndex() < 0)
                ? field.getItems().get(0) : field.getSelectionModel().getSelectedItem();
        String v = filter.getValue() == null || filter.getValue().isEmpty()
                ? "[empty]" : filter.getValue();
        staticInfo.setText(f + " " + filter.getSelectedConnector() + " " + v);
    }

    /**
     * Method when user updates the filter.
     *
     * @param onAccept the method to make somethig when user updates the filter
     */
    public void setOnAccept(EventHandler onAccept) {
        this.onAccept = onAccept;
    }

    /**
     * Method when user deletes a filter.
     *
     * @param onDelete the method to remove the filter
     */
    public void setOnDelete(EventHandler onDelete) {
        this.onDelete = onDelete;
    }

    private void delete() {
        if (onDelete != null) onDelete.handle(new ActionEvent());
    }

    private void view() {
        boolean b = filter.isActive();
        filter.setActive(!b);
        view.setGraphic(b ? new SizableImageView("/img/black/noview.png", SizableImageView.SMALL_SIZE)
                : new SizableImageView("/img/black/view.png", SizableImageView.SMALL_SIZE));
        if (onAccept != null) onAccept.handle(new ActionEvent());
    }

    private void voids() {
        boolean b = filter.isAceptingVoids();
        filter.setAceptingVoids(!b);
        voids.setGraphic(b ? new SizableImageView("/img/black/nocircle.png", SizableImageView.SMALL_SIZE)
                : new SizableImageView("/img/black/circle.png", SizableImageView.SMALL_SIZE));
        if (onAccept != null) onAccept.handle(new ActionEvent());
    }

}
