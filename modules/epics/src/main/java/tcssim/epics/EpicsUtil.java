/*
 * Copyright (c) 2016-2023 Association of Universities for Research in Astronomy, Inc. (AURA)
 * For license information see LICENSE or https://opensource.org/licenses/BSD-3-Clause
 */

package tcssim.epics;

import com.cosylab.epics.caj.cas.util.MemoryProcessVariable;
import gov.aps.jca.dbr.DBRType;
import gov.aps.jca.dbr.DBR_Int;

public class EpicsUtil {
    static MemoryProcessVariable createEnumMemoryProcessVariable(String name, short[] init) {
        return new MemoryProcessVariable(name, null, DBRType.ENUM, init);
    }

    static int[] intValue(int[] v) { return v; }
    static double[] doubleValue(double[] v) { return v; }
}
