/*-----------------------------------------------------------------------
  
Copyright (c) 2007-2010, The University of Manchester, United Kingdom.
All rights reserved.

Redistribution and use in source and binary forms, with or without 
modification, are permitted provided that the following conditions are met:

 * Redistributions of source code must retain the above copyright notice, 
      this list of conditions and the following disclaimer.
 * Redistributions in binary form must reproduce the above copyright 
      notice, this list of conditions and the following disclaimer in the 
      documentation and/or other materials provided with the distribution.
 * Neither the name of The University of Manchester nor the names of 
      its contributors may be used to endorse or promote products derived 
      from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE 
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE 
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR 
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF 
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS 
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN 
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) 
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
POSSIBILITY OF SUCH DAMAGE.

-----------------------------------------------------------------------*/
package uk.ac.manchester.rcs.nanocmosdata.records.legacy;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class defines constants that are common to both the server-side and the
 * client-side.
 * 
 * @author Bruno Harbulot (Bruno.Harbulot@manchester.ac.uk)
 * 
 */
public class CommonConstants {
    private CommonConstants() {
    }

    /**
     * Namespace of the XML representations handled natively by this system.
     */
    public static final String HYPERMEDIA_XML_NAMESPACE = "http://www.rcs.manchester.ac.uk/research/nanocmos/2008/01/datamanagement/hypermedia";

    public static final String MEDIATYPE_NANOCMOS_DATAMANAGEMENT_XML = "application/vnd.nanocmos.datamg+xml";
    @Deprecated
    public static final String ACCEPT_MEDIATYPE_HEADER = MEDIATYPE_NANOCMOS_DATAMANAGEMENT_XML
            + ";q=0.9,application/xml;q=0.8";
    public static final List<String> ACCEPT_MEDIATYPES = Collections
            .unmodifiableList(Arrays.asList(new String[] {
                    MEDIATYPE_NANOCMOS_DATAMANAGEMENT_XML, "application/xml" }));
}
