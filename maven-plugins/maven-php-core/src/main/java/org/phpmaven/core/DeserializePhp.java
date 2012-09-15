package org.phpmaven.core;

/*
Copyright (c) 2007 Zsolt Szász <zsolt at lorecraft dot com>

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
"Software"), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Deserializes a serialized PHP data structure into corresponding Java objects. It supports
 * the integer, float, boolean, string primitives that are mapped to their Java
 * equivalent, plus arrays that are parsed into <code>Map</code> instances and objects
 * that are represented by {@link SerializedPhpParser.PhpObject} instances.
 * <p>
 * Example of use:
 * <pre>
 *      String input = "O:8:"TypeName":1:{s:3:"foo";s:3:"bar";}";
 *      SerializedPhpParser serializedPhpParser = new SerializedPhpParser(input);
 *      Object result = serializedPhpParser.parse();
 * </pre>
 *
 * The <code>result</code> object will be a <code>PhpObject</code> with the name "TypeName" and
 * the attribute "foo" = "bar".
 * 
 * @author http://code.google.com/p/serialized-php-parser/
 */
public class DeserializePhp {

    /**
     * Null representation.
     */
    public static final Object NULL = new Object() {
        @Override
        public String toString() {
            return "NULL";
        }
    };

    /** the php serialized input. */
    private final String input;

    /** current string index in serialization. */
    private int index;

    /** true to assume utf-8. */
    private boolean assumeUTF8 = true;

    /** the attribute name regexp that is allowed. */
    private Pattern acceptedAttributeNameRegex;
    
    /**
     * Constructor to create a new deserializer.
     * @param input input
     */
    public DeserializePhp(String input) {
        this.input = input;
    }

    /**
     * Constructor to create a new deserializer.
     * @param input input.
     * @param assumeUTF8 true to assume utf-8.
     */
    public DeserializePhp(String input, boolean assumeUTF8) {
        this.input = input;
        this.assumeUTF8 = assumeUTF8;
    }

    /**
     * Parses the input and returns an object.
     * @return either PhpObject or a primitive (for example Boolean).
     */
    public Object parse() {
        final char type = input.charAt(index);
        switch (type) {
            case 'i':
                index += 2;
                return parseInt();
            case 'd':
                index += 2;
                return parseFloat();
            case 'b':
                index += 2;
                return parseBoolean();
            case 's':
                index += 2;
                return parseString();
            case 'a':
                index += 2;
                return parseArray();
            case 'O':
                index += 2;
                return parseObject();
            case 'N':
                index += 2;
                return NULL;
            default:
                throw new IllegalStateException("Encountered unknown type [" + type
                        + "@" + index + "]");
        }
    }

    /**
     * Parses an object.
     * @return php object.
     */
    private Object parseObject() {
        final PhpObject phpObject = new PhpObject();
        final int strLen = readLength();
        phpObject.name = input.substring(index, index + strLen);
        index = index + strLen + 2;
        final int attrLen = readLength();
        for (int i = 0; i < attrLen; i++) {
            final Object key = parse();
            final Object value = parse();
            if (isAcceptedAttribute(key)) {
                phpObject.attributes.put(key, value);
            }
        }
        index++;
        return phpObject;
    }

    /**
     * Parses an array.
     * @return array.
     */
    private Map<Object, Object> parseArray() {
        final int arrayLen = readLength();
        final Map<Object, Object> result = new LinkedHashMap<Object, Object>();
        for (int i = 0; i < arrayLen; i++) {
            final Object key = parse();
            final Object value = parse();
            if (isAcceptedAttribute(key)) {
                result.put(key, value);
            }
        }
        index++;
        return result;
    }

    /**
     * Tests if the attribute is accepted.
     * @param key key.
     * @return true if it is accepted.
     */
    private boolean isAcceptedAttribute(Object key) {
        if (acceptedAttributeNameRegex == null) {
            return true;
        }
        if (!(key instanceof String)) {
            return true;
        }
        return acceptedAttributeNameRegex.matcher((String) key).matches();
    }

    /**
     * reads a length integer from serialized php.
     * @return length
     */
    private int readLength() {
        final int delimiter = input.indexOf(':', index);
        final int arrayLen = Integer.valueOf(input.substring(index, delimiter));
        index = delimiter + 2;
        return arrayLen;
    }

    /**
     * Assumes strings are utf8 encoded.
     *
     * @return string
     */
    private String parseString() {
        final int strLen = readLength();

        int utfStrLen = 0;
        int byteCount = 0;
        while (byteCount != strLen) {
            final char ch = input.charAt(index + utfStrLen++);
            if (assumeUTF8) {
                if ((ch >= 0x0001) && (ch <= 0x007F)) {
                    byteCount++;
                } else if (ch > 0x07FF) {
                    byteCount += 3;
                } else {
                    byteCount += 2;
                }
            } else {
                byteCount++;
            }
        }
        final String value = input.substring(index, index + utfStrLen);
        index = index + utfStrLen + 2;
        return value;
    }

    /**
     * Parses a boolean.
     * @return boolean.
     */
    private Boolean parseBoolean() {
        final int delimiter = input.indexOf(';', index);
        String value = input.substring(index, delimiter);
        if ("1".equals(value)) {
            value = "true";
        } else if ("0".equals(value)) {
            value = "false";
        }
        index = delimiter + 1;
        return Boolean.valueOf(value);
    }

    /**
     * Parses a float.
     * @return float.
     */
    private Double parseFloat() {
        final int delimiter = input.indexOf(';', index);
        final String value = input.substring(index, delimiter);
        index = delimiter + 1;
        return Double.valueOf(value);
    }

    /**
     * Parses an integer.
     * @return integer
     */
    private Integer parseInt() {
        final int delimiter = input.indexOf(';', index);
        final String value = input.substring(index, delimiter);
        index = delimiter + 1;
        return Integer.valueOf(value);
    }
    
    /**
     * Sets the regexp for accepted attribute names.
     * @param acceptedAttributeNameRegex regexp.
     */
    public void setAcceptedAttributeNameRegex(String acceptedAttributeNameRegex) {
        this.acceptedAttributeNameRegex = Pattern.compile(acceptedAttributeNameRegex);
    }

    /**
     * Represents an object that has a name and a map of attributes.
     */
    public static class PhpObject {
        public String name;
        public Map<Object, Object> attributes = new HashMap<Object, Object>();

        @Override
        public String toString() {
            return "\"" + name + "\" : " + attributes.toString();
        }
    }
}
