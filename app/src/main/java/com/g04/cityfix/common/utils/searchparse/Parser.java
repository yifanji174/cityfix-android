package com.g04.cityfix.common.utils.searchparse;

import com.g04.cityfix.common.utils.Peekable;
import com.g04.cityfix.common.utils.searchtoken.Token;
import com.g04.cityfix.common.utils.searchtoken.TokenType;
import com.g04.cityfix.common.utils.searchtoken.Tokenizer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Parser {
    public static IAstNode parse(List<Token> tokens) {
        if (tokens.isEmpty()) {
            return new AstInvalid();
        }
        //  Just a Bunch of Keywords
        boolean isJBOK = tokens.parallelStream()
                .allMatch(token -> (token.getType() == TokenType.KEYWORD));
        if (isJBOK) {
            return new AstOr(tokens.parallelStream()
                    .map(token -> new AstKeyword(token.getStr()))
                    .collect(Collectors.toList())
            );
        }

        Peekable<Token> iter = new Peekable<>(tokens.iterator());
        IAstNode root = expe(iter);
        if (!iter.hasNext()) {
            return root;
        } else {
            return new AstInvalid();
        }
    }
    private static IAstNode expe(Peekable<Token> iter) {
        List<IAstNode> nodes = new ArrayList<>();
        try {
            Token t = iter.peek();
            switch (t.getType()) {
                case LBR:
                case NOT:
                case KEYWORD:
                    nodes.add(expa(iter));
                    break;
                default:
                    return new AstInvalid();
            }
        } catch (Exception e) {
            return new AstInvalid();
        }
        while (iter.hasNext()) {
            Token t = iter.peek();
            if (t.getType() == TokenType.RBR) {
                break;
            }
            if (t.getType() == TokenType.OR) {
                iter.next();
                nodes.add(expa(iter));
            } else {
                return new AstInvalid();
            }
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        } else {
            return new AstOr(nodes);
        }
    }

    private static IAstNode expa(Peekable<Token> iter) {
        List<IAstNode> nodes = new ArrayList<>();
        try {
            Token t = iter.peek();
            switch (t.getType()) {
                case LBR:
                case NOT:
                case KEYWORD:
                    nodes.add(expt(iter));
                    break;
                default:
                    return new AstInvalid();
            }
        } catch (Exception e) {
            return new AstInvalid();
        }
        while (iter.hasNext()) {
            Token t = iter.peek();
            TokenType tty = t.getType();
            if (tty == TokenType.OR || tty == TokenType.RBR) {
                break;
            }
            if (tty == TokenType.AND) {
                iter.next();
                nodes.add(expt(iter));
            } else {
                return new AstInvalid();
            }
        }
        if (nodes.size() == 1) {
            return nodes.get(0);
        } else {
            return new AstAnd(nodes);
        }
    }

    private static IAstNode expt(Peekable<Token> iter) {
        try {
            Token t = iter.next();
            switch (t.getType()) {
                case KEYWORD:
                    return new AstKeyword(t.getStr());
                case NOT:
                    return new AstNot(expt(iter));
                case LBR:
                    IAstNode exp = expe(iter);
                    iter.next();
                    return exp;
                default:
                    return new AstInvalid();
            }
        } catch (Exception e) {
            return new AstInvalid();
        }
    }
}

