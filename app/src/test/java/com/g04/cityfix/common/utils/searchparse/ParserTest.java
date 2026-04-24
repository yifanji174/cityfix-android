package com.g04.cityfix.common.utils.searchparse;

import static com.g04.cityfix.common.utils.searchparse.Parser.parse;
import static com.g04.cityfix.common.utils.searchtoken.Tokenizer.tokenize;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Junhao Liu
 */
public class ParserTest {
    @Test
    public void jbokTest() {
        String str1 = "to be or not to be is a question but i dont give a fxxx";
        assertTrue(parse(tokenize(str1)) instanceof AstOr);
        String str2 = "any color you like but all are blue";
        assertTrue(parse(tokenize(str2)) instanceof AstOr);
    }

    @Test
    public void brTest() {
        String str1 = "li AND tang OR ding AND zhen";
        assertTrue(parse(tokenize(str1)) instanceof AstOr);
        String str2 = "Je AND tu AND ( NOT il OR NOT elle )";
        assertTrue(parse(tokenize(str2)) instanceof AstAnd);
    }
}
