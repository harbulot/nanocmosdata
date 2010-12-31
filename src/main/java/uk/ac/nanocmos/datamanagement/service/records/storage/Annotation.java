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
package uk.ac.nanocmos.datamanagement.service.records.storage;

import java.util.Date;

/**
 * This interface provides the common elements that constitute an annotation.
 * This is used by the classes generated using Hibernate: FileRecord,
 * Experiment, Job and Task. See the Hibernate mapping file for more details.
 * 
 * @author Bruno Harbulot (Bruno.Harbulot@manchester.ac.uk)
 * 
 */
public interface Annotation {
    /**
     * Attaches this annotation to a particular record.
     * 
     * @param container
     *            AbstractRecord to which this annotation is to belong.
     */
    public void setContainer(Record container);

    /**
     * Gets the AbstractRecord to which this annotation belongs.
     * 
     * @return AbstractRecord to which this annotation belongs.
     */
    public Record getContainer();

    public Date getModificationTime();

    public Date getCreationTime();

    public void setContentType(String contentType);

    public String getContentType();

    public void setXmlContent(String xmlContent);

    public String getXmlContent();

    public void setTextContent(String textContent);

    public String getTextContent();

    public void setBinaryContent(byte[] binaryContent);

    public byte[] getBinaryContent();

    public void setLink(String link);

    public String getLink();

    /**
     * Returns the ID used by Hibernate to identify this annotation.
     * 
     * @return ID used by Hibernate to identify this annotation.
     */
    public Long getId();

    public void setId(Long id);
}
