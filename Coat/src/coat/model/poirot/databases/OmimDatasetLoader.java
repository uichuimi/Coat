/******************************************************************************
 * Copyright (C) 2015 UICHUIMI                                                *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package coat.model.poirot.databases;

import javafx.concurrent.Task;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

/**
 * OMIM provides information about phenotypes and the genes related to them. Each Instance contains one relationship.
 * Fields are: (0) gene_symbol, (1) gene_name, (2) gene_mimNumber, (3) confidence, (4) phenotype_name,
 * (5) phenotype_mimNumber, (6) phenotype_mappingKey. Omim Dataset is indexed by column 0.
 *
 * @author Lorente Arencibia, Pascual (pasculorente@gmail.com)
 */
public class OmimDatasetLoader extends Task<Dataset> {

    private Dataset dataset = new Dataset();

    @Override
    protected Dataset call() throws Exception {
        loadEntries();
        setColumnNames();
        return dataset;
    }

    private void setColumnNames() {
        dataset.setColumnNames(Arrays.asList("gene_symbol", "gene_name", "gene_mimNumber", "confidence",
                "phenotype_name", "phenotype_mimNumber", "phenotype_mappingKey"));
    }

    private void loadEntries() {
        final List<Instance> collect = readFileInstances();
        if (collect != null) {
            dataset.getInstances().addAll(collect);
            dataset.createIndex(0);
        }
    }

    private List<Instance> readFileInstances() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(new GZIPInputStream(OmimDatasetLoader.class.getResourceAsStream("omim-phenotypes.tsv.gz"))))) {
            return reader.lines()
                    .map(line -> line.split("\t"))
                    .map(this::getFields)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Instance getFields(String[] line) {
        final Object[] fields = {
                line[0],
                line[1],
                getIntegerOrNull(line[2]),
                line[3],
                line[4],
                getIntegerOrNull(line[5]),
                getIntegerOrNull(line[6])
        };
        return new Instance(dataset, fields);
    }

    private Integer getIntegerOrNull(String value) {
        try {
            return Integer.valueOf(value);
        } catch (Exception ignored) {
            return null;
        }
    }

}
