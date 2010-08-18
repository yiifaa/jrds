/*
 * Copyright (C) 2001 Ciaran Treanor <ciaran@codeloop.com>
 *
 * Distributable under GPL license.
 * See terms of license at gnu.org.
 *
 * $Id: CDPStatusBlock.java,v 1.1 2006/02/03 08:27:16 sasam Exp $
 */
package jrds.jrrd;

import java.io.IOException;
import java.io.PrintStream;

/**
 * Instances of this class model the consolidation data point status from an RRD file.
 *
 * @author <a href="mailto:ciaran@codeloop.com">Ciaran Treanor</a>
 * @version $Revision: 1.1 $
 */
public class CDPStatusBlock {

    static private enum cdp_par_en {CDP_val, CDP_unkn_pdp_cnt, CDP_hw_intercept, CDP_hw_last_intercept, CDP_hw_slope, 
        CDP_hw_last_slope, CDP_null_count,
        CDP_last_null_count, CDP_primary_val, CDP_secondary_val};

        long offset;
        long size;
        int unknownDatapoints;
        double value;

        double secondary_value;
        double primary_value;

        CDPStatusBlock(RRDFile file) throws IOException {
            //Should read MAX_CDP_PAR_EN = 10
            //Size should be 0x50

            offset = file.getFilePointer();
            UnivalArray scratch = file.getUnivalArray(10);
            value = scratch.getDouble(cdp_par_en.CDP_val);
            unknownDatapoints = (int) scratch.getDouble(cdp_par_en.CDP_unkn_pdp_cnt);
            primary_value = scratch.getDouble(cdp_par_en.CDP_primary_val);
            secondary_value = scratch.getDouble(cdp_par_en.CDP_secondary_val);
//            System.out.println("========");
//            for(cdp_par_en i:cdp_par_en.values())
//                System.out.println(scratch.getDouble(i));

            //rrd.cdp_prep[i * rrd.stat_head->ds_cnt + ii].scratch[CDP_val].u_val
            //value = file.readDouble();
            //rrd.cdp_prep[i * rrd.stat_head->ds_cnt + ii].scratch[CDP_unkn_pdp_cnt].u_cnt)
            //unknownDatapoints = file.readInt();

            // Skip rest of cdp_prep_t.scratch
            //file.skipBytes(80 - 2 * 8);
            //file.align();

            size = file.getFilePointer() - offset;
        }

        /**
         * Returns the number of unknown primary data points that were integrated.
         *
         * @return the number of unknown primary data points that were integrated.
         */
        public int getUnknownDatapoints() {
            return unknownDatapoints;
        }

        /**
         * Returns the value of this consolidated data point.
         *
         * @return the value of this consolidated data point.
         */
        public double getValue() {
            return value;
        }

        void toXml(PrintStream s) {

            s.print("\t\t\t<ds><value> ");
            s.print(value);
            s.print(" </value>  <unknown_datapoints> ");
            s.print(unknownDatapoints);
            s.println(" </unknown_datapoints></ds>");
        }

        /**
         * Returns a summary the contents of this CDP status block.
         *
         * @return a summary of the information contained in the CDP status block.
         */
        public String toString() {

            StringBuilder sb = new StringBuilder("[CDPStatusBlock: OFFSET=0x");

            sb.append(Long.toHexString(offset));
            sb.append(", SIZE=0x");
            sb.append(Long.toHexString(size));
            sb.append(", unknownDatapoints=");
            sb.append(unknownDatapoints);
            sb.append(", value=");
            sb.append(value);
            sb.append(", primaryValue=");
            sb.append(primary_value);
            sb.append(", secondaryValue=");
            sb.append(secondary_value);
            sb.append("]");

            return sb.toString();
        }
}
