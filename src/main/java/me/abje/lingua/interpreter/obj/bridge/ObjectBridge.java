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

package me.abje.lingua.interpreter.obj.bridge;

import me.abje.lingua.interpreter.*;
import me.abje.lingua.interpreter.obj.*;
import me.abje.lingua.util.TriFunction;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectBridge {
    private static MethodHandle convertToNumber;
    private static MethodHandle convertFromNumber;

    static {
        try {
            convertToNumber = handle("toNumberObj", float.class);
            convertFromNumber = handle("fromNumberObj", NumberObj.class);
        } catch (IllegalAccessException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    private static MethodHandle handle(String name, Class... parameterTypes)
            throws NoSuchMethodException, IllegalAccessException {
        return MethodHandles.lookup().unreflect(ObjectBridge.class.getDeclaredMethod(name, parameterTypes));
    }

    public static <C> Map<String, Map<Integer, MethodMetadata>> createMethodMap(Class<C> clazz, C instance) {
        Map<String, Map<Integer, MethodMetadata>> methodMap = new HashMap<>();
        for (Method method : clazz.getDeclaredMethods()) {
            Bridge bridge = method.getAnnotation(Bridge.class);
            if (bridge != null) {
                try {
                    String methodName = bridge.value().isEmpty() ? method.getName() : bridge.value();
                    MethodHandle initialHandle = MethodHandles.lookup().unreflect(method);
                    Class<?> returnType = initialHandle.type().returnType();
                    if (returnType == byte.class || returnType == short.class ||
                            returnType == int.class || returnType == float.class) {
                        initialHandle = MethodHandles.filterReturnValue(initialHandle,
                                convertToNumber.asType(MethodType.methodType(NumberObj.class, returnType)));
                    }
                    if (instance != null)
                        initialHandle = initialHandle.bindTo(instance);
                    Class<?>[] parameterArray = initialHandle.type().parameterArray();
                    if (parameterArray.length > 0 && parameterArray[parameterArray.length - 1] == Obj[].class)
                        initialHandle = initialHandle.asVarargsCollector(Obj[].class);
                    int interpreterIndex = -1;
                    for (int i = 0; i < parameterArray.length; i++) {
                        Class<?> type = parameterArray[i];
                        if (type == Interpreter.class) {
                            interpreterIndex = i;
                        } else if (type == int.class) {
                            initialHandle = MethodHandles.filterArguments(initialHandle, i, convertFromNumber);
                        }
                    }
                    Map<Integer, MethodMetadata> map = methodMap.computeIfAbsent(methodName, s -> new HashMap<>());
                    map.put(parameterArray.length - (interpreterIndex != -1 ? 1 : 0) -
                                    (instance == null && !Modifier.isStatic(method.getModifiers()) ? 1 : 0),
                            new MethodMetadata(initialHandle, interpreterIndex, bridge.anyLength()));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return methodMap;
    }

    public static <C> Map<String, ObjField> createFieldMap(Class<C> clazz, C instance) {
        Map<String, ObjField> fieldMap = new HashMap<>();
        for (Method method : clazz.getDeclaredMethods()) {
            FieldBridge bridge = method.getAnnotation(FieldBridge.class);
            if (bridge != null) {
                try {
                    String methodName = bridge.value().isEmpty() ? method.getName() : bridge.value();
                    MethodHandle initialHandle = MethodHandles.lookup().unreflect(method);
                    Class<?> returnType = initialHandle.type().returnType();
                    if (returnType == byte.class || returnType == short.class ||
                            returnType == int.class || returnType == float.class) {
                        initialHandle = MethodHandles.filterReturnValue(initialHandle,
                                convertToNumber.asType(MethodType.methodType(returnType, NumberObj.class)));
                    }
                    if (instance != null)
                        initialHandle = initialHandle.bindTo(instance);
                    Class<?>[] parameterArray = initialHandle.type().parameterArray();
                    int interpreterIndex = -1;
                    for (int i = 0; i < parameterArray.length; i++) {
                        Class<?> type = parameterArray[i];
                        if (type == Interpreter.class) {
                            interpreterIndex = i;
                        } else if (type == int.class) {
                            initialHandle = MethodHandles.filterArguments(initialHandle, i, convertFromNumber);
                        }
                    }

                    final MethodHandle finalHandle = initialHandle;
                    final int index = interpreterIndex;
                    fieldMap.put(methodName, new GetterObjField(methodName, method.isAnnotationPresent(Static.class),
                            (interpreter, self) -> {
                                MethodHandle handle = finalHandle;
                                if (index != -1)
                                    handle = MethodHandles.insertArguments(handle, index - 1, interpreter);
                                if (self != null)
                                    handle = handle.bindTo(self);
                                try {
                                    Object result = handle.invokeWithArguments();
                                    if (result != null) {
                                        if (result instanceof String) {
                                            return new StringObj((String) result);
                                        } else {
                                            return (Obj) result;
                                        }
                                    } else {
                                        return NullObj.get();
                                    }
                                } catch (Throwable throwable) {
                                    throw new RuntimeException(throwable);
                                }
                            }, null));
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
        return fieldMap;
    }

    public static <C> void addFunction(ClassObj.Builder<C> builder, String methodName, Map<Integer, MethodMetadata> map) {
        builder.withFunction(methodName, createFunctionBridge(methodName, map));
    }

    public static <C> TriFunction<Interpreter, C, List<Obj>, Obj> createFunctionBridge(String methodName, Map<Integer, MethodMetadata> map) {
        return (interpreter, self, args) -> {
            boolean hasThisSize = map.containsKey(args.size());
            if (hasThisSize || (map.containsKey(1) && map.get(1).isAnyLength())) {
                MethodMetadata meta = hasThisSize ? map.get(args.size()) : map.get(1);
                MethodHandle handle = meta.getHandle();
                int interpreterIndex = meta.getInterpreterIndex();
                if (self != null)
                    handle = handle.bindTo(self);
                if (interpreterIndex != -1)
                    handle = MethodHandles.insertArguments(handle, interpreterIndex - 1, interpreter);
                if (!meta.anyLength)
                    for (int i = 0; i < args.size(); i++) {
                        if (!handle.type().parameterType(i).isAssignableFrom(args.get(i).getClass())) {
                            throw new InterpreterException("CallException", "invalid argument for function " + methodName);
                        }
                    }

                try {
                    Object result = handle.invokeWithArguments(args);
                    if (result != null) {
                        return (Obj) result;
                    } else {
                        return NullObj.get();
                    }
                } catch (InterpreterException e) {
                    throw e;
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                    return NullObj.get();
                }
            } else {
                throw new InterpreterException("CallException", "invalid number of arguments for function " + methodName);
            }
        };
    }

    @SuppressWarnings("unused")
    public static NumberObj toNumberObj(float f) {
        return NumberObj.of(f);
    }

    @SuppressWarnings("unused")
    public static int fromNumberObj(NumberObj num) {
        return (int) num.getValue();
    }

    public static class MethodMetadata {
        private MethodHandle handle;
        private int interpreterIndex;
        private boolean anyLength;

        private MethodMetadata(MethodHandle handle, int interpreterIndex, boolean anyLength) {
            this.handle = handle;
            this.interpreterIndex = interpreterIndex;
            this.anyLength = anyLength;
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

        public boolean isAnyLength() {
            return anyLength;
        }

        public void setAnyLength(boolean anyLength) {
            this.anyLength = anyLength;
        }

        @Override
        public String toString() {
            return "MethodMetadata{" +
                    "handle=" + handle +
                    ", interpreterIndex=" + interpreterIndex +
                    ", anyLength=" + anyLength +
                    '}';
        }
    }
}
