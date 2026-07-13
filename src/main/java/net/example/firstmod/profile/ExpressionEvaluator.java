package net.example.firstmod.profile;

import java.util.ArrayList;
import java.util.List;

public class ExpressionEvaluator {

    public static double eval(String expr, double dist) {
        return new Parser(expr, dist).parse();
    }

    private static class Parser {
        private final List<Token> tokens;
        private int pos;

        Parser(String expr, double dist) {
            this.tokens = tokenize(expr, dist);
            this.pos = 0;
        }

        double parse() {
            double result = parseAddSub();
            if (pos < tokens.size()) {
                throw new IllegalArgumentException("Unexpected token: " + tokens.get(pos));
            }
            return result;
        }

        private double parseAddSub() {
            double left = parseMulDiv();
            while (pos < tokens.size()) {
                Token t = tokens.get(pos);
                if (t.type == TokenType.PLUS) { pos++; left += parseMulDiv(); }
                else if (t.type == TokenType.MINUS) { pos++; left -= parseMulDiv(); }
                else break;
            }
            return left;
        }

        private double parseMulDiv() {
            double left = parseUnary();
            while (pos < tokens.size()) {
                Token t = tokens.get(pos);
                if (t.type == TokenType.MUL) { pos++; left *= parseUnary(); }
                else if (t.type == TokenType.DIV) { pos++; double r = parseUnary(); if (r == 0) throw new ArithmeticException("Division by zero"); left /= r; }
                else if (t.type == TokenType.POW) { pos++; left = Math.pow(left, parseUnary()); }
                else break;
            }
            return left;
        }

        private double parseUnary() {
            if (pos < tokens.size() && tokens.get(pos).type == TokenType.MINUS) {
                pos++;
                return -parseUnary();
            }
            return parsePrimary();
        }

        private double parsePrimary() {
            if (pos >= tokens.size()) throw new IllegalArgumentException("Unexpected end of expression");
            Token t = tokens.get(pos++);
            return switch (t.type) {
                case NUMBER -> t.value;
                case DIST -> t.value;
                case LPAREN -> {
                    double val = parseAddSub();
                    expect(TokenType.RPAREN, "Missing closing parenthesis");
                    yield val;
                }
                case FUNC -> {
                    String name = t.text;
                    expect(TokenType.LPAREN, "Expected '(' after function '" + name + "'");
                    double arg1 = parseAddSub();
                    if (name.equals("min") || name.equals("max") || name.equals("pow")) {
                        expect(TokenType.COMMA, "Expected ',' in function '" + name + "'");
                        double arg2 = parseAddSub();
                        expect(TokenType.RPAREN, "Missing ')' in function '" + name + "'");
                        yield switch (name) {
                            case "min" -> Math.min(arg1, arg2);
                            case "max" -> Math.max(arg1, arg2);
                            case "pow" -> Math.pow(arg1, arg2);
                            default -> 0;
                        };
                    } else {
                        expect(TokenType.RPAREN, "Missing ')' in function '" + name + "'");
                        yield switch (name) {
                            case "sqrt" -> Math.sqrt(Math.max(0, arg1));
                            case "abs" -> Math.abs(arg1);
                            case "exp" -> Math.exp(arg1);
                            default -> throw new IllegalArgumentException("Unknown function: " + name);
                        };
                    }
                }
                default -> throw new IllegalArgumentException("Unexpected token: " + t);
            };
        }

        private void expect(TokenType type, String message) {
            if (pos >= tokens.size() || tokens.get(pos).type != type) {
                throw new IllegalArgumentException(message);
            }
            pos++;
        }
    }

    private enum TokenType {
        NUMBER, PLUS, MINUS, MUL, DIV, POW, LPAREN, RPAREN, COMMA, FUNC, DIST
    }

    private static class Token {
        final TokenType type;
        final double value;
        final String text;

        Token(TokenType type) { this(type, 0, null); }
        Token(TokenType type, double value) { this(type, value, null); }
        Token(TokenType type, double value, String text) {
            this.type = type;
            this.value = value;
            this.text = text;
        }

        @Override
        public String toString() {
            return type + (text != null ? "(" + text + ")" : "") + (type == TokenType.NUMBER ? "=" + value : "");
        }
    }

    private static List<Token> tokenize(String expr, double dist) {
        List<Token> result = new ArrayList<>();
        int i = 0;
        while (i < expr.length()) {
            char c = expr.charAt(i);
            if (Character.isWhitespace(c)) { i++; continue; }
            if (c == '+') { result.add(new Token(TokenType.PLUS)); i++; continue; }
            if (c == '-') { result.add(new Token(TokenType.MINUS)); i++; continue; }
            if (c == ',') { result.add(new Token(TokenType.COMMA)); i++; continue; }
            if (c == '*') {
                if (i + 1 < expr.length() && expr.charAt(i + 1) == '*') {
                    result.add(new Token(TokenType.POW)); i += 2;
                } else { result.add(new Token(TokenType.MUL)); i++; }
                continue;
            }
            if (c == '/') { result.add(new Token(TokenType.DIV)); i++; continue; }
            if (c == '^') { result.add(new Token(TokenType.POW)); i++; continue; }
            if (c == '(') { result.add(new Token(TokenType.LPAREN)); i++; continue; }
            if (c == ')') { result.add(new Token(TokenType.RPAREN)); i++; continue; }

            if (Character.isDigit(c) || c == '.') {
                int start = i;
                while (i < expr.length() && (Character.isDigit(expr.charAt(i)) || expr.charAt(i) == '.')) i++;
                result.add(new Token(TokenType.NUMBER, Double.parseDouble(expr.substring(start, i))));
                continue;
            }

            if (Character.isLetter(c) || c == '_') {
                int start = i;
                while (i < expr.length() && (Character.isLetterOrDigit(expr.charAt(i)) || expr.charAt(i) == '_')) i++;
                String word = expr.substring(start, i);
                if (word.equals("dist")) {
                    result.add(new Token(TokenType.DIST, dist));
                } else if (word.equals("min") || word.equals("max") || word.equals("sqrt") || word.equals("abs") || word.equals("exp") || word.equals("pow")) {
                    result.add(new Token(TokenType.FUNC, 0, word));
                } else {
                    throw new IllegalArgumentException("Unknown identifier: " + word);
                }
                continue;
            }

            throw new IllegalArgumentException("Unexpected character: '" + c + "' at position " + i);
        }
        return result;
    }
}
