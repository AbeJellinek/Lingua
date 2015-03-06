/*
 * Copyright (c) 2015 Abe Jellinek
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package me.abje.lingua.parser.expr;

import me.abje.lingua.interpreter.Environment;
import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.obj.MapObj;
import me.abje.lingua.interpreter.obj.Obj;
import me.abje.lingua.lexer.Token;

import java.util.HashMap;
import java.util.Map;

public class MapExpr extends Expr {
    private final Map<Expr, Expr> items;

    public MapExpr(Token token, Map<Expr, Expr> items) {
        super(token);
        this.items = items;
    }

    @Override
    public Obj evaluate(Interpreter interpreter) {
        Map<Obj, Obj> objMap = new HashMap<>();
        items.forEach((k, v) -> objMap.put(interpreter.next(k), interpreter.next(v)));
        return new MapObj(objMap);
    }

    @Override
    public Obj match(Interpreter interpreter, Environment.Frame frame, Obj obj) {
        if (obj instanceof MapObj) {
            MapObj map = (MapObj) obj;
            for (Expr key : items.keySet()) {
                if (items.get(key).match(interpreter, frame, map.get(interpreter.next(key))) == null) {
                    return null;
                }
            }
            return obj;
        } else {
            return null;
        }
    }
}
