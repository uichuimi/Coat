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

package coat.view.poirot.gene;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import poirot.core.Pearl;
import poirot.core.PearlGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by uichuimi on 18/05/16.
 */
public class GeneListController {

    private final static Image LENS = new Image("coat/img/black/search.png");
    private final static Image CANCEL = new Image("coat/img/black/cancel.png");
    private final List<GeneRow> allGeneRows = new ArrayList<>();
    @FXML
    private TableView<GeneRow> geneTable;
    @FXML
    private TextField searchBox;
    @FXML
    private TableColumn<GeneRow, Boolean> select;
    @FXML
    private ImageView searchImage;
    @FXML
    private TableColumn<GeneRow, String> score;
    @FXML
    private TableColumn<GeneRow, String> gene;
    @FXML
    private TableColumn<GeneRow, Integer> distance;
    private PearlGraph pearlGraph;
    private ObservableList<Pearl> selectedGenes = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        configureColumns();
        configureSearchBox();
    }

    private void configureSearchBox() {
        searchBox.textProperty().addListener((observable, oldValue, newValue) -> {
            searchImage.setImage(newValue.isEmpty() ? LENS : CANCEL);
            filter();
        });
        searchImage.setOnMouseClicked(event -> searchBox.setText(""));
        searchImage.setImage(LENS);
    }

    private void configureColumns() {
        gene.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().gene.getId()));
        score.setCellValueFactory(param -> new SimpleObjectProperty<>(String.format("%.3f", param.getValue().gene.getScore())));
        distance.setCellValueFactory(param -> new SimpleObjectProperty<>(param.getValue().gene.getDistanceToPhenotype()));
        select.setCellValueFactory(param -> param.getValue().selected);
        select.setCellFactory(param -> new CheckBoxTableCell<>());
    }

    public void setPearlGraph(PearlGraph pearlGraph) {
        this.pearlGraph = pearlGraph;
        allGeneRows.clear();
        allGeneRows.addAll(pearlGraph.getPearls(Pearl.Type.GENE).stream()
                .map(pearl -> new GeneRow(pearl, false))
                .collect(Collectors.toList()));
        filter();
    }

    private void filter() {
        final String searchText = searchBox.getText().toLowerCase();
        geneTable.getItems().setAll(allGeneRows.stream()
                .filter(pearl -> pearl.gene.getId().toLowerCase().contains(searchText)).collect(Collectors.toList()));
    }

    public List<Pearl> getSelectedGenes() {
        return selectedGenes;
    }

    private void updateSelectedGenes() {
        selectedGenes.setAll(allGeneRows.stream()
                .filter(phenotype -> phenotype.selected.getValue())
                .map(phenotype -> phenotype.gene)
                .collect(Collectors.toList()));
    }

    public void sort() {
        Collections.sort(geneTable.getItems(), (o1, o2) -> Double.compare(o2.gene.getScore(), o1.gene.getScore()));
    }

    private class GeneRow {

        final Pearl gene;
        final Property<Boolean> selected;

        GeneRow(Pearl gene, boolean selected) {
            this.gene = gene;
            this.selected = new SimpleBooleanProperty(selected);
            this.selected.addListener((observable, oldValue, newValue) -> updateSelectedGenes());
        }

    }
}
