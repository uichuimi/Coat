/******************************************************************************
 * Copyright (C) 2015 UICHUIMI                                                *
 * *
 * This program is free software: you can redistribute it and/or modify it    *
 * under the terms of the GNU General Public License as published by the      *
 * Free Software Foundation, either version 3 of the License, or (at your     *
 * option) any later version.                                                 *
 * *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                       *
 * See the GNU General Public License for more details.                       *
 * *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package coat.view.vcfreader;

import coat.CoatView;
import coat.core.reader.Reader;
import coat.core.vcf.LFS;
import coat.core.vcf.TsvSaver;
import coat.core.vcf.VcfStats;
import coat.core.vep.VepAnnotator;
import coat.utils.FileManager;
import coat.utils.OS;
import coat.view.graphic.SizableImageView;
import coat.view.vcfreader.header.HeaderViewController;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import vcf.Variant;
import vcf.VariantSet;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * FXML Controller class
 *
 * @author UICHUIMI
 */
public class VcfReader extends VBox implements Reader {

    private static int NEW_VCF = 0;

    private final InfoTable infoTable = new InfoTable();
    private final VariantsTable variantsTable;
    //    private final FilterList filterList = new FilterList();
    private final TabPane tabs = new TabPane();
    private final SampleTable samplesTableView = new SampleTable();

    private final SplitPane leftPane = new SplitPane();
    private final SplitPane mainPane = new SplitPane();

    private final VariantSet variantSet;
    private final List<Button> actions = new LinkedList<>();
    private final Property<String> titleProperty = new SimpleStringProperty();
    private File file;
    private String baseName;

    public VcfReader(VariantSet variantSet, File file) {
        this.variantSet = variantSet;
        this.file = file;
        this.variantsTable = new VariantsTable(variantSet);
        this.baseName = file != null ? file.getName() : "New vcf " + NEW_VCF++;
        initializeLeftPane();
        initializeThis();
        initializeButtons();
        initializeTabs();
        bindFile();
    }

    private void initializeLeftPane() {
        leftPane.getItems().addAll(variantsTable);
        leftPane.setDividerPositions(0.75);
//        SplitPane.setResizableWithParent(filterList, false);
        leftPane.setOrientation(Orientation.VERTICAL);
    }

    private void initializeThis() {
        mainPane.getItems().addAll(leftPane, tabs);
        mainPane.setDividerPositions(0.75);
        SplitPane.setResizableWithParent(tabs, false);
        mainPane.setOrientation(Orientation.HORIZONTAL);
        getChildren().setAll(mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    private void initializeTabs() {
        final Tab infoTab = new Tab(OS.getResources().getString("properties"), infoTable);
        infoTab.setClosable(false);
        final Tab sampleTab = new Tab(OS.getResources().getString("samples"), samplesTableView);
        sampleTab.setClosable(false);
        variantsTable.getVariantProperty().addListener((observable, oldValue, newValue) -> samplesTableView.setVariant(newValue));
        final List<String> formats = variantSet.getHeader().getIdList("FORMAT");
        samplesTableView.setColumns(formats);
        tabs.getTabs().addAll(infoTab, sampleTab);
    }

    @Override
    public Property<String> titleProperty() {
        return titleProperty;
    }

    @Override
    public void saveAs() {
        File output = file == null
                ? FileManager.saveFile("Select output file", FileManager.VCF_FILTER, FileManager.TSV_FILTER)
                : FileManager.saveFile("Select output file", file.getParentFile(), file.getName(),
                FileManager.VCF_FILTER, FileManager.TSV_FILTER);
        final TreeSet<Variant> toSaveVariants = new TreeSet<>(variantsTable.getFilteredVariants());
        if (output != null) {
            if (output.getName().endsWith(".vcf")) variantSet.save(output, toSaveVariants);
            else new TsvSaver(variantSet, output, toSaveVariants).invoke();
            this.file = output;
            baseName = output.getName();
            titleProperty.setValue(baseName);
        }
    }

    @Override
    public List<Button> getActions() {
        return actions;
    }

    @Override
    public String getActionsName() {
        return "VCF";
    }

    private void initializeButtons() {
        Button viewheaders = getViewHeadersButton();
        Button lfs = getLfsButton();
        Button vep = getVepButton();
        Button statsButton = getStatsButton();
        actions.addAll(Arrays.asList(viewheaders, lfs, statsButton, vep));
    }

    private Button getViewHeadersButton() {
        Button viewheaders = new Button(OS.getResources().getString("headers"));
        viewheaders.setOnAction(event -> viewHeaders());
        viewheaders.setGraphic(new SizableImageView("coat/img/black/headers.png", SizableImageView.SMALL_SIZE));
        return viewheaders;
    }

    private void viewHeaders() {
        try {
            final FXMLLoader loader = new FXMLLoader(HeaderViewController.class.getResource("header-view.fxml"));
            final Parent root = loader.load();
            HeaderViewController controller = loader.getController();
            controller.setHeader(variantSet.getHeader());
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setWidth(600);
            stage.setHeight(600);
            stage.setTitle(baseName);
            stage.centerOnScreen();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Button getLfsButton() {
        final Button lfs = new Button("LFS");
        lfs.setOnAction(event -> addLFS());
        lfs.setGraphic(new SizableImageView("coat/img/black/lfs.png", SizableImageView.SMALL_SIZE));
        return lfs;
    }

    private void addLFS() {
        injectLFSHeader();
        variantSet.getVariants().parallelStream().forEach(LFS::addLFS);
        CoatView.printMessage("LFS tag added", "success");
    }

    /**
     * Inserts LFS header alphabetically.
     */
    private void injectLFSHeader() {
        if (!variantSet.getHeader().getIdList("INFO").contains("LFS")) {
            final Map<String, String> map = new TreeMap<>();
            map.put("ID", "LFS");
            map.put("Number", "1");
            map.put("Type", "Float");
            map.put("Description", "Low frequency codon substitution");
            variantSet.getHeader().addComplexHeader("INFO", map);
        }
//        final boolean match = variantSet.getHeader().getComplexHeaders().getProperty("INFO").stream().anyMatch(map -> map.getProperty("ID").equals("LFS"));
//        final String lfsInfo = "##INFO=<ID=LFS,Number=1,Type=Integer,Description=\"Low frequency codon substitution\">";
//        if (!match) variantSet.getHeader().addHeader(lfsInfo);
    }

    private Button getVepButton() {
        Button vep = new Button("VEP");
        vep.setGraphic(new SizableImageView("coat/img/black/vep_logo.png", SizableImageView.SMALL_SIZE));
        vep.setOnAction(e -> addVep());
        return vep;
    }

    private void addVep() {
        final Task annotator = new VepAnnotator(variantSet);
        annotator.setOnSucceeded(event -> CoatView.printMessage(variantSet.getVariants().size() + " variants annotated", "success"));
        annotator.setOnFailed(event -> CoatView.printMessage("something wrong", "error"));
        annotator.messageProperty().addListener((obs, old, current) -> CoatView.printMessage(current, "info"));
        CoatView.printMessage("Annotating variants...", "info");
        new Thread(annotator).start();
    }

    private Button getStatsButton() {
        Button statsButton = new Button("View stats");
        statsButton.setGraphic(new SizableImageView("coat/img/black/stats.png", SizableImageView.SMALL_SIZE));
        statsButton.setOnAction(event -> showStats());
        return statsButton;
    }

    private void showStats() {
        VcfStats vcfStats = new VcfStats(variantSet);
        StatsReader statsReader = new StatsReader(vcfStats);
        Scene scene = new Scene(statsReader);
        Stage stage = new Stage();
        stage.setWidth(600);
        stage.setHeight(600);
        stage.setTitle(baseName);
        stage.centerOnScreen();
        stage.setScene(scene);
        stage.show();
    }

    private void bindFile() {
        titleProperty.setValue(baseName);
        infoTable.getVariantProperty().bind(variantsTable.getVariantProperty());
//        variantsTable.setVariantSet(variantSet);
        variantSet.changedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) Platform.runLater(() -> titleProperty.setValue(baseName + "*"));
            else titleProperty.setValue(baseName);
        });
    }
}
