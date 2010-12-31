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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public final class FileRecord implements Record, Serializable {

    private static final long serialVersionUID = 640485029403142513L;
    private String id;
    private Date creationTime;
    private Date modificationTime;
    private String owner;
    private String type;
    private String location;
    private String parentJobRecord;
    private List<FileRecordAnnotation> annotations = new ArrayList<FileRecordAnnotation>(
            0);

    public FileRecord() {
    }

    public FileRecord(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCreationTime() {
        return this.creationTime == null ? null : new Date(this.creationTime
                .getTime());
    }

    public void setCreationTime(Date creationTime) {
        this.creationTime = creationTime == null ? null : new Date(creationTime
                .getTime());
    }

    public Date getModificationTime() {
        return this.modificationTime == null ? null : new Date(
                this.modificationTime.getTime());
    }

    public void setModificationTime(Date modificationTime) {
        this.modificationTime = modificationTime == null ? null : new Date(
                modificationTime.getTime());
    }

    public String getOwner() {
        return this.owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getLocation() {
        return this.location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getParentJobRecord() {
        return this.parentJobRecord;
    }

    public void setParentJobRecord(String parentJobRecord) {
        this.parentJobRecord = parentJobRecord;
    }

    public List<FileRecordAnnotation> getAnnotations() {
        return this.annotations;
    }

    public void setAnnotations(List<FileRecordAnnotation> annotations) {
        this.annotations = annotations;
    }

    public Annotation newAnnotation() {
        return new FileRecordAnnotation();
    }

    public void addAnnotation(Annotation annotation) {
        getAnnotations().add((FileRecordAnnotation) annotation);
    }
}
