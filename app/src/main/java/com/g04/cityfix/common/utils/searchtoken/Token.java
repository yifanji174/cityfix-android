package com.g04.cityfix.common.utils.searchtoken;

public class Token {
    private final TokenType type;
    private final String str;
    private Token(TokenType type, String str) {
        this.type = type;
        this.str = str;
    }

    public static Token createToken(TokenType type) {
        switch (type) {
            case AND:
                return new Token(type, "AND");
            case OR:
                return new Token(type, "OR");
            case NOT:
                return new Token(type, "NOT");
            case LBR:
                return new Token(type, "(");
            case RBR:
                return new Token(type, ")");
            default:
                return new Token(type, "");
        }
    }

    public static Token createToken(String keyword) {
        return new Token(TokenType.KEYWORD, keyword);
    }

    public TokenType getType() { return this.type; }
    public String getStr() { return this.str; }
}

