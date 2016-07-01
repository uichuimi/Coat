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

package coat.view.vcfreader.filter;

import coat.core.vcf.VcfFilter;
import coat.view.vcfreader.VariantsTable;

import java.util.Arrays;
import java.util.List;
import java.util.regex.PatternSyntaxException;

/**
 * Use this column to filter text columns.
 */
public class StringFilterTableColumn<S, T> extends ConnectorTextFilterTableColumn<S, T> {

    public StringFilterTableColumn(VariantsTable table, String title) {
        super(table, title);
    }

    @Override
    protected List<VcfFilter.Connector> getConnectors() {
        return Arrays.asList(VcfFilter.Connector.CONTAINS, VcfFilter.Connector.DIFFERS, VcfFilter.Connector.EQUALS,
                VcfFilter.Connector.MATCHES);
    }

    @Override
    protected boolean filter(S item, T value) {
        final String filterText = getFilterText();
        if (filterText == null || filterText.isEmpty()) return true;
        final String val = (String) value;
        if (val== null || val.isEmpty()) return !isStrict();
        switch (getConnector()) {
            case MATCHES:
                try {
                    return val.matches(filterText);
                } catch (PatternSyntaxException ex) {
                    return true;
                }
            case DIFFERS:
                return !val.matches(filterText);
            case EQUALS:
                return val.equals(filterText);
            case CONTAINS:
            default:
                return val.contains(filterText);

        }
    }
}