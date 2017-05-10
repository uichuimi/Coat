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

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

/**
 * @author Lorente Arencibia, Pascual (pasculorente@gmail.com)
 */
public class DictionaryTest {



    @Test
    public void testOneWord() {
        final Dictionary dictionary = new Dictionary();
        final int code = dictionary.addWord("one");
        Assert.assertEquals(code, dictionary.getCode("one"));
        Assert.assertNotEquals(code, dictionary.getCode("two"));
    }

    @Test
    public void testWithFourWords() {
        final Dictionary dictionary = new Dictionary();
        final String[] words = {"one", "two", "three"};
        for (String word : words) dictionary.addWord(word);
        final int code = dictionary.addWord("four");
        Assert.assertEquals(code, dictionary.getCode("four"));
        Assert.assertNotEquals(code, dictionary.getCode("one"));
    }

    @Test
    public void testNotInDictionary() {
        final Dictionary dictionary = new Dictionary();
        Assert.assertTrue(dictionary.getCode("one") < 0);
        Assert.assertNull(dictionary.getWord(0));
    }

    @Test
    public void testCodes() {
        final Dictionary dictionary = new Dictionary();
        final String[] words = {"one", "two", "three", "one"};
        for (String word : words) {
            int code = dictionary.addWord(word);
            Assert.assertEquals(code, dictionary.getCode(word));
            Assert.assertEquals(word, dictionary.getWord(code));
        }
    }

    @Test
    public void testWordList() {
        final Dictionary dictionary = new Dictionary();
        Assert.assertEquals(Collections.emptyList(), dictionary.getWordList());
        final String[] words = {"one", "two", "three", "one"};
        for (String word : words) dictionary.addWord(word);
        Assert.assertEquals(Arrays.asList("one", "two", "three"), dictionary.getWordList());
    }
}
