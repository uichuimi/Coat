/******************************************************************************
 * Copyright (C) 2015 UICHUIMI                                                *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify it    *
 * under the terms of the GNU General Public License as published by the      *
 * Free Software Foundation, either version 3 of the License, or (at your     *
 * option) any later version.                                                 *
 *                                                                            *
 * This program is distributed in the hope that it will be useful, but        *
 * WITHOUT ANY WARRANTY; without even the implied warranty of                 *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                       *
 * See the GNU General Public License for more details.                       *
 *                                                                            *
 * You should have received a copy of the GNU General Public License          *
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.      *
 ******************************************************************************/

package coat.core.poirot.dataset;

import java.io.PrintStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * A Dataset stores a list of Instances with the same structure. Each Instance stores a list of Objects, its fields.
 * All Instances in the same Dataset have the same number of fields. Fields can be accessed by its index or by its
 * column name, if columnNames have been set.
 *
 * @author Lorente Arencibia, Pascual (pasculorente@gmail.com)
 */
public class Dataset {

    private final List<Instance> instances = new ArrayList<>();
    private final List<String> columnNames = new ArrayList<>();
    /**
     * Indexes allow fast access to fields. To access an index, just take it from <code>indexes.get(column)</code>. As
     * indexes are not automatically created, first check if index is available: <code>hasIndex(column)</code>. If not,
     * create it first: <code>createIndex(column)</code>.
     */
    private final Map<Integer, Map<Object, List<Instance>>> indexes = new HashMap<>();
    private final Map<String, Integer> columnPositions = new HashMap<>();

    public void addInstance(Object[] fields) {
        final Instance instance = new Instance(this, fields);
        instances.add(instance);
        addToIndex(instance);
    }

    private void addToIndex(Instance instance) {
        indexes.forEach((position, index) -> {
            final Object key = instance.getField(position);
            index.putIfAbsent(key, new ArrayList<>());
            final List<Instance> instances = index.get(key);
            if (!instances.contains(instance)) instances.add(instance);
        });
    }

    public void createIndex(int position) {
        indexes.putIfAbsent(position, instances.stream().collect(Collectors.groupingBy(instance -> instance.getField(position))));
    }

    public List<Instance> getInstances() {
        return instances;
    }

    /**
     * OPTIONAL. Set the name of the columns. This allows user to access values by its column name.
     *
     * @param columnNames a list containing the name of each column.
     */
    public void setColumnNames(List<String> columnNames) {
        this.columnNames.clear();
        this.columnNames.addAll(columnNames);
        createColumnNamesIndex(columnNames);
    }

    private void createColumnNamesIndex(List<String> columnNames) {
        for (int i = 0; i < columnNames.size(); i++) columnPositions.put(columnNames.get(i), i);
    }

    public List<String> getColumnNames() {
        return columnNames;
    }

    /**
     * Get all the instances that have the value of the column position equals to id. This method can only be used if
     * an index has been created before with <code>createIndex(position)</code>
     *
     * @param value    the value to query
     * @param position the column
     * @return a list of instances that matches value in the position column
     */
    public List<Instance> getInstances(Object value, int position) {
        return indexes.get(position).getOrDefault(value, Collections.emptyList());
    }

    /**
     * Get the index of  the column in the dataset. If the column does not exist, returns -1.
     *
     * @param column the name of the column
     * @return the index of the column or -1 if it does not exist
     */
    public int indexOf(String column) {
        return columnPositions.getOrDefault(column, -1);
    }

    /**
     * Prints this table to the given PrintStream. Useful to print to System.out or to any file.
     *
     * @param out    the printStream
     */
    public void printTable(PrintStream out) {
        out.println(columnNames);
        for (Instance instance : instances) {
            for (int i = 0; i < columnNames.size(); i++) out.print(instance.getField(i) + " ");
            out.println();
        }
    }

    private boolean hasIndex(int column) {
        return indexes.containsKey(column);
    }

}