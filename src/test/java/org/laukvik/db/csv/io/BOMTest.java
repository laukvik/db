/*
 * Copyright 2015 Laukviks Bedrifter.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.laukvik.db.csv.io;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Morten Laukvik <morten@laukvik.no>
 */
public class BOMTest {

    @Test
    public void utf8() {
        BOM bom = BOM.parse((byte) 239, (byte) 187, (byte) 191);
        assertEquals(BOM.UTF8.getClass(), bom.getClass());
    }

    @Test
    public void utf16BE() {
        BOM bom = BOM.parse((byte) 254, (byte) 255);
        assertEquals(BOM.UTF16BE.getClass(), bom.getClass());
    }

    @Test
    public void utf16LE() {
        BOM bom = BOM.parse((byte) 255, (byte) 254);
        assertEquals(BOM.UTF16LE.getClass(), bom.getClass());
    }

    @Test
    public void utf32BE() {
        BOM bom = BOM.parse((byte) 0, (byte) 0, (byte) 254, (byte) 255);
        assertEquals(BOM.UTF32LE.getClass(), bom.getClass());
    }

    @Test
    public void utf32LE() {
        BOM bom = BOM.parse((byte) 0, (byte) 0, (byte) 255, (byte) 254);
        assertEquals(BOM.UTF32LE.getClass(), bom.getClass());
    }

}
