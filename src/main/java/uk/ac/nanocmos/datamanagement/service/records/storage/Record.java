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
import java.util.List;

/**
 * This interface provides the common elements that constitute a record. This is
 * used by the classes generated using Hibernate: FileRecord, Experiment, Job
 * and Task. See the Hibernate mapping file for more details.
 * 
 * @author Bruno Harbulot (Bruno.Harbulot@manchester.ac.uk)
 * 
 */
public interface Record {
    /**
     * Sets the ID used by Hibernate.
     * 
     * @param id
     *            identifier used by Hibernate to load and store.
     */
    public void setId(String id);

    /**
     * Gets the ID used by Hibernate.
     * 
     * @return identifier used by Hibernate to load and store.
     */
    public String getId();

    public Date getModificationTime();

    public Date getCreationTime();

    public String getOwner();

    public void setOwner(String owner);

    /**
     * This is a factory method that should return a new object corresponding to
     * the type of XmlAnnotation associated with the class implementing this
     * interface.
     * 
     * @return Concrete ExternalAnnotation for this type of record.
     */
    public Annotation newAnnotation();

    /**
     * Adds an annotation to this record.
     * 
     * @param annotation
     *            annotation to add.
     * @throws ClassCastException
     *             if the type is not acceptable for this type of record.
     */
    public void addAnnotation(Annotation annotation);

    /**
     * Returns the list of annotations.
     * 
     * @return List of annotations of this resource.
     */
    public List<? extends Annotation> getAnnotations();

    public String getParentJobRecord();

    public void setParentJobRecord(String parentJobRecord);
}
