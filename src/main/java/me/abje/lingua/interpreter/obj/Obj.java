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

package me.abje.lingua.interpreter.obj;

import me.abje.lingua.interpreter.Bridge;
import me.abje.lingua.interpreter.Interpreter;
import me.abje.lingua.interpreter.InterpreterException;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The base class for Lingua objects. Objects have types and members.
 */
public class Obj {
    /**
     * This object's type. Can be null if this object is a {@link me.abje.lingua.interpreter.obj.ClassObj}.
     */
    private ClassObj type;

    /**
     * This object's members.
     * Keys are names of members, and values are the members' values.
     */
    private Map<String, Obj> members = new HashMap<>();

    /**
     * This object's super instance.
     */
    private Obj superInst;

    private static final MethodHandle convertToNumber;

    static {
        MethodHandle toNumberObj = null;
        try {
            toNumberObj = MethodHandles.lookup().unreflect(
                    Obj.class.getDeclaredMethod("toNumberObj", float.class));
        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }

        convertToNumber = toNumberObj;
    }

    public static final ClassObj SYNTHETIC = bridgeClass(Obj.class);

    /**
     * Creates a new object with the given type.
     *
     * @param type The type. Can be null if this object is a type itself.
     */
    public Obj(ClassObj type) {
        this.type = type;
    }

    /**
     * Invokes this object with the given arguments. Unless overridden, simply throws an exception.
     *
     * @param interpreter The Interpreter that it is being invoked in.
     * @param args        The arguments passed by the caller.
     * @return The result of invoking this object.
     */
    public Obj call(Interpreter interpreter, List<Obj> args) {
        throw new InterpreterException("InvalidOperationException", "object not callable", interpreter);
    }

    /**
     * Objects are by default truthy.
     *
     * @return True.
     */
    public boolean isTruthy() {
        return true;
    }

    /**
     * Returns the object at the given index inside this object.
     *
     * @param index The index.
     * @return The object at that index.
     * @throws me.abje.lingua.interpreter.InterpreterException If this object is not indexable.
     */
    public Obj getAtIndex(Obj index) {
        throw new InterpreterException("InvalidOperationException", "object not indexable");
    }

    /**
     * Sets the object at the given index inside this object.
     *
     * @param index The index.
     * @param value The object to set at that index.
     * @throws me.abje.lingua.interpreter.InterpreterException If this object is not indexable.
     */
    public void setAtIndex(Obj index, Obj value) {
        throw new InterpreterException("InvalidOperationException", "object not indexable");
    }

    /**
     * Gets a member of this object by name.
     *
     * @param name The member's name.
     * @return The member, never null.
     * @throws me.abje.lingua.interpreter.InterpreterException If a member by that name could not be found.
     */
    public Obj getMember(String name) {
        if (type != null && type.getFunctionMap().containsKey(name)) {
            Obj function = type.getFunctionMap().get(name);
            if (function instanceof FunctionObj) {
                return ((FunctionObj) function).withSelf(this).withSuper(superInst);
            } else if (function instanceof SyntheticFunctionObj) {
                SyntheticFunctionObj synthetic = (SyntheticFunctionObj) function;
                synthetic.setSelf(this);
                synthetic.setSuperInst(superInst);
                return function;
            } else {
                return function;
            }
        } else if (type != null && type.getFieldMap().containsKey(name)) {
            return members.getOrDefault(name, NullObj.get());
        } else if (superInst != null && superInst.members.containsKey(name)) {
            return superInst.members.getOrDefault(name, NullObj.get());
        } else if (superInst != null && superInst.type.getFunctionMap().containsKey(name)) {
            Obj function = superInst.type.getFunctionMap().get(name);
            if (function instanceof FunctionObj) {
                return ((FunctionObj) function).withSelf(this).withSuper(superInst);
            } else if (function instanceof SyntheticFunctionObj) {
                SyntheticFunctionObj synthetic = (SyntheticFunctionObj) function;
                synthetic.setSelf(this);
                synthetic.setSuperInst(superInst);
                return function;
            } else {
                return function;
            }
        } else {
            throw new InterpreterException("UndefinedException", "unknown field: " + name);
        }
    }

    /**
     * Sets a member of this object by name.
     *
     * @param name  The member's name.
     * @param value The new value of the member.
     * @throws me.abje.lingua.interpreter.InterpreterException If a member by that name could not be found.
     */
    public void setMember(String name, Obj value) {
        if (type != null && type.getFieldMap().containsKey(name)) {
            members.put(name, value);
        } else {
            throw new InterpreterException("UndefinedException", "unknown field: " + name);
        }
    }

    /**
     * Gets this object's type.
     */
    public ClassObj getType() {
        return type;
    }

    /**
     * Sets this object's type.
     *
     * @param type The new type of this object.
     */
    protected void setType(ClassObj type) {
        this.type = type;
    }

    public void setSuperInst(Obj superInst) {
        this.superInst = superInst;
    }

    public Obj getSuperInst() {
        return superInst;
    }

    public static <C extends Obj> ClassObj bridgeClass(Class<C> clazz) {

        String originalName = clazz.getSimpleName();

        // Calculate the name of the synthetic class as:
        // "Obj"     -> "Obj"
        // "${x}Obj" -> x
        // "${x}"    -> x

        String className;
        if (originalName.equals("Obj"))
            className = originalName;
        else if (originalName.endsWith("Obj"))
            className = originalName.substring(0, originalName.length() - 3);
        else
            className = originalName;

        ClassObj.Builder<C> builder = ClassObj.<C>builder(className);
        Map<String, Map<Integer, MethodMetadata>> methodMap = new HashMap<>();
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.getAnnotation(Bridge.class) != null) {
                try {
                    String methodName = method.getName();
                    MethodHandle initialHandle = MethodHandles.lookup().unreflect(method);
                    Class<?> returnType = initialHandle.type().returnType();
                    if (returnType == byte.class || returnType == short.class ||
                            returnType == int.class || returnType == float.class) {
                        initialHandle = MethodHandles.filterReturnValue(initialHandle,
                                convertToNumber.asType(MethodType.methodType(NumberObj.class, returnType)));
                    }
                    Class<?>[] parameterArray = initialHandle.type().parameterArray();
                    int interpreterIndex = -1;
                    for (int i = 0; i < parameterArray.length; i++) {
                        Class<?> type = parameterArray[i];
                        if (type == Interpreter.class) {
                            interpreterIndex = i;
                        }
                    }
                    Map<Integer, MethodMetadata> map = methodMap.computeIfAbsent(methodName, s -> new HashMap<>());
                    map.put(parameterArray.length - (interpreterIndex != -1 ? 1 : 0) -
                                    (!Modifier.isStatic(method.getModifiers()) ? 1 : 0),
                            new MethodMetadata(initialHandle, interpreterIndex));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        methodMap.forEach((methodName, map) -> addFunction(builder, methodName, map));
        return builder.build();
    }

    private static <C extends Obj> void addFunction(ClassObj.Builder<C> builder, String methodName, Map<Integer, MethodMetadata> map) {
        builder.withFunction(methodName, (interpreter, self, args) -> {
            if (map.containsKey(args.size())) {
                MethodMetadata meta = map.get(args.size());
                MethodHandle handle = meta.getHandle();
                int interpreterIndex = meta.getInterpreterIndex();
                if (self != null)
                    handle = handle.bindTo(self);
                if (interpreterIndex != -1)
                    handle = MethodHandles.insertArguments(handle, interpreterIndex - 1, interpreter);
                for (int i = 0; i < args.size(); i++) {
                    if (!handle.type().parameterType(i).isAssignableFrom(args.get(i).getClass())) {
                        throw new InterpreterException("CallException", "invalid argument for function " + methodName);
                    }
                }

                try {
                    //noinspection RedundantCast
                    return (Obj) handle.invokeWithArguments(args);
                } catch (InterpreterException e) {
                    throw e;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    return NullObj.get();
                }
            } else {
                throw new InterpreterException("CallException", "invalid number of arguments for function " + methodName);
            }
        });
    }

    @SuppressWarnings("unused")
    public static NumberObj toNumberObj(float f) {
        return NumberObj.of(f);
    }

    private static class MethodMetadata {
        private MethodHandle handle;
        private int interpreterIndex;

        private MethodMetadata(MethodHandle handle, int interpreterIndex) {
            this.handle = handle;
            this.interpreterIndex = interpreterIndex;
        }

        public MethodHandle getHandle() {
            return handle;
        }

        public void setHandle(MethodHandle handle) {
            this.handle = handle;
        }

        public int getInterpreterIndex() {
            return interpreterIndex;
        }

        public void setInterpreterIndex(int interpreterIndex) {
            this.interpreterIndex = interpreterIndex;
        }
    }
}
