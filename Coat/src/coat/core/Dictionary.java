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

package coat.core;

import java.util.*;

/**
 * @author Lorente Arencibia, Pascual (pasculorente@gmail.com)
 */
public class Dictionary {

    private final Map<String, Integer> map = new LinkedHashMap<>();
    private final Map<Integer, String> index = new TreeMap<>();
    private int nextCode = 0;

    public int addWord(String word) {
        final Integer integer = map.get(word);
        if (integer != null) return integer;
        map.put(word, nextCode);
        index.put(nextCode, word);
        return nextCode++;
    }

    public int getCode(String word) {
        final Integer integer = map.get(word);
        return integer == null ? -1 : integer;
    }

    public String getWord(int code) {
        return index.get(code);
    }

    public List<String> getWordList() {
        return new ArrayList<>(map.keySet());
    }
}