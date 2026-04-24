package com.g04.cityfix.common.utils.searchtoken;

import static com.g04.cityfix.common.utils.searchtoken.Tokenizer.tokenize;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.util.List;

/**
 * @author Junhao Liu
 */
public class TokenizerTest {
    @Test
    public void tokenizerTest() {
        String testStr = "AND OR NOT ( ) str and or not";
        List<Token> tokens = tokenize(testStr);
        assertEquals(9, tokens.size());
        assertEquals(TokenType.AND, tokens.get(0).getType());
        assertEquals(TokenType.OR, tokens.get(1).getType());
        assertEquals(TokenType.NOT, tokens.get(2).getType());
        assertEquals(TokenType.LBR, tokens.get(3).getType());
        assertEquals(TokenType.RBR, tokens.get(4).getType());
        assertEquals(TokenType.KEYWORD, tokens.get(5).getType());
        assertEquals(TokenType.KEYWORD, tokens.get(6).getType());
        assertEquals(TokenType.KEYWORD, tokens.get(7).getType());
        assertEquals(TokenType.KEYWORD, tokens.get(8).getType());
    }
}
