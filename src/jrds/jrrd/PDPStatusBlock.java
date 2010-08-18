/*
 * Copyright (C) 2001 Ciaran Treanor <ciaran@codeloop.com>
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 *
 * $Id: PDPStatusBlock.java,v 1.1 2006/02/03 08:27:16 sasam Exp $
 */
package jrds.jrrd;

import java.io.IOException;

/**
 * Instances of this class model the primary data point status from an RRD file.
 *
 * @author <a href="mailto:ciaran@codeloop.com">Ciaran Treanor</a>
 * @version $Revision: 1.1 $
 */
public class PDPStatusBlock {

    long offset;
    long size;
    String lastReading;
    int unknownSeconds;
    double value;
    static private enum pdp_par_en {PDP_unkn_sec_cnt, PDP_val};

    PDPStatusBlock(RRDFile file) throws IOException {

        offset = file.getFilePointer();
        //rrd.pdp_prep[i].last_ds
        lastReading = file.readString(Constants.LAST_DS_LEN);

        //file.align(8);
        
        UnivalArray scratch = file.getUnivalArray(10);
        unknownSeconds = (int) scratch.getLong(pdp_par_en.PDP_unkn_sec_cnt);
        value = scratch.getDouble(pdp_par_en.PDP_val);
        //rrd.pdp_prep[i].scratch[PDP_unkn_sec_cnt].u_cnt
       //unknownSeconds = file.readInt();

        //rrd.pdp_prep[i].scratch[PDP_val].u_val
        //value = file.readDouble();

        // Skip rest of pdp_prep_t.par[]
        //file.skipBytes(80 - 2 * 8);

        size = file.getFilePointer() - offset;
    }

    /**
     * Returns the last reading from the data source.
     *
     * @return the last reading from the data source.
     */
    public String getLastReading() {
        return lastReading;
    }

    /**
     * Returns the current value of the primary data point.
     *
     * @return the current value of the primary data point.
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the number of seconds of the current primary data point is
     * unknown data.
     *
     * @return the number of seconds of the current primary data point is unknown data.
     */
    public int getUnknownSeconds() {
        return unknownSeconds;
    }

    /**
     * Returns a summary the contents of this PDP status block.
     *
     * @return a summary of the information contained in this PDP status block.
     */
    public String toString() {

        StringBuilder sb = new StringBuilder("[PDPStatus: OFFSET=0x");

        sb.append(Long.toHexString(offset));
        sb.append(", SIZE=0x");
        sb.append(Long.toHexString(size));
        sb.append(", lastReading=");
        sb.append(lastReading);
        sb.append(", unknownSeconds=");
        sb.append(unknownSeconds);
        sb.append(", value=");
        sb.append(value);
        sb.append("]");

        return sb.toString();
    }
}
