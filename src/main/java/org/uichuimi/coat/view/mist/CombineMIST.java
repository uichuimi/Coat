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

package org.uichuimi.coat.view.mist;

import org.uichuimi.coat.core.mist.MistCombiner;
import org.uichuimi.coat.core.tool.Tool;
import org.uichuimi.coat.utils.FileManager;
import org.uichuimi.coat.utils.OS;
import org.uichuimi.coat.view.graphic.FileList;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.io.File;
import java.io.IOException;

/**
 * Controller for the Combine MIST Window.
 *
 * @author Pascual Lorente Arencibia (pasculorente@gmail.com)
 */
public class CombineMIST extends Tool {

    @FXML
    private Label messageLabel;
    @FXML
    private FileList files;
    @FXML
    private Button startButton;

    private Property<String> title = new SimpleObjectProperty<>(OS.getString("combine.mist"));

    public CombineMIST() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/CombineMIST.fxml"), OS.getResources());
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void initialize() {
        startButton.setOnAction(e -> combine());
        files.setFilters(FileManager.MIST_FILTER);
    }

    private void combine() {
        final File f = getOutput();
        if (f != null) {
            final MistCombiner mistCombinator = new MistCombiner(files.getFiles(), f);
            messageLabel.textProperty().bind(mistCombinator.messageProperty());
            startButton.setDisable(true);
            mistCombinator.setOnSucceeded(this::combinerFinished);
            Platform.runLater(mistCombinator);
        }
    }

    private void combinerFinished(WorkerStateEvent event) {
        messageLabel.textProperty().unbind();
        startButton.setDisable(false);
    }

    private File getOutput() {
        String message = OS.getFormattedString("select.file", "MIST");
        return FileManager.saveFile(message, FileManager.MIST_FILTER);
    }

    @Override
    public Property<String> titleProperty() {
        return title;
    }

    @Override
    public void saveAs() {

    }
}
