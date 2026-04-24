package com.g04.cityfix.common.utils.searchtoken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokenizer {
    public static List<Token> tokenize(String str) {
        List<Token> ret = new ArrayList<>();
        Arrays.stream(str.trim().split("\\s+")).forEach(token -> {
            switch (token) {
                case "AND":
                    ret.add(Token.createToken(TokenType.AND));
                    break;
                case "OR":
                    ret.add(Token.createToken(TokenType.OR));
                    break;
                case "NOT":
                    ret.add(Token.createToken(TokenType.NOT));
                    break;
                case "(" :
                    ret.add(Token.createToken(TokenType.LBR));
                    break;
                case ")" :
                    ret.add(Token.createToken(TokenType.RBR));
                    break;
                default:
                    if (!token.isEmpty()) {
                        ret.add(Token.createToken(token.toLowerCase()));
                    }
            }
        });
        return ret;
    }
}
