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

public class JobRecord implements Record, Serializable {

    private static final long serialVersionUID = 2L;

    private String id;
    private Date creationTime;
    private Date modificationTime;
    private String owner;
    private Date jobCreationTime;
    private Date jobStartTime;
    private Date jobEndTime;
    private String parentJobRecord;
    private List<JobRecordChildJob> childJobRecords = new ArrayList<JobRecordChildJob>(
            0);
    private List<JobRecordChildFileRec> childFileRecords = new ArrayList<JobRecordChildFileRec>(
            0);
    private List<JobRecordAnnotation> annotations = new ArrayList<JobRecordAnnotation>(
            0);

    public JobRecord() {
    }

    public JobRecord(String id) {
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

    public void setOwner(String coreDataOwner) {
        this.owner = coreDataOwner;
    }

    public Date getJobCreationTime() {
        return this.jobCreationTime == null ? null : new Date(
                this.jobCreationTime.getTime());
    }

    public void setJobCreationTime(Date jobCreationTime) {
        this.jobCreationTime = jobCreationTime == null ? null : new Date(
                jobCreationTime.getTime());
    }

    public Date getJobStartTime() {
        return this.jobStartTime == null ? null : new Date(this.jobStartTime
                .getTime());
    }

    public void setJobStartTime(Date jobStartTime) {
        this.jobStartTime = jobStartTime == null ? null : new Date(jobStartTime
                .getTime());
    }

    public Date getJobEndTime() {
        return this.jobEndTime == null ? null : new Date(this.jobEndTime
                .getTime());
    }

    public void setJobEndTime(Date jobEndTime) {
        this.jobEndTime = jobEndTime == null ? null : new Date(jobEndTime
                .getTime());
    }

    public String getParentJobRecord() {
        return this.parentJobRecord;
    }

    public void setParentJobRecord(String parentJobRecord) {
        this.parentJobRecord = parentJobRecord;
    }

    public List<JobRecordChildJob> getChildJobRecords() {
        return this.childJobRecords;
    }

    public void setChildJobRecords(List<JobRecordChildJob> childJobRecords) {
        this.childJobRecords = childJobRecords;
    }

    public List<JobRecordChildFileRec> getChildFileRecords() {
        return this.childFileRecords;
    }

    public void setChildFileRecords(List<JobRecordChildFileRec> childFileRecords) {
        this.childFileRecords = childFileRecords;
    }

    public List<JobRecordAnnotation> getAnnotations() {
        return this.annotations;
    }

    public void setAnnotations(List<JobRecordAnnotation> annotations) {
        this.annotations = annotations;
    }

    public Annotation newAnnotation() {
        return new JobRecordAnnotation();
    }

    public void addAnnotation(Annotation annotation) {
        getAnnotations().add((JobRecordAnnotation) annotation);
    }
}
