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

import java.util.Collections;
import java.util.List;

import uk.ac.nanocmos.datamanagement.service.records.storage.Annotation;
import uk.ac.nanocmos.datamanagement.service.records.storage.FileRecord;
import uk.ac.nanocmos.datamanagement.service.records.storage.JobRecord;
import uk.ac.nanocmos.datamanagement.service.records.storage.Record;

/**
 * @author Bruno Harbulot (Bruno.Harbulot@manchester.ac.uk);
 * 
 */
public class DataContainer {
    public final FileRecord fileRecord;
    public final JobRecord jobRecord;
    public final Annotation annotation;
    public final List<? extends Annotation> annotations;

    public final List<? extends Record> records;
    public final int listPageSize;
    public final int listStartItem;
    public final int listTotalCount;

    private DataContainer(FileRecord fileRecord, JobRecord jobRecord,
            Annotation annotation, List<? extends Annotation> annotations,
            List<? extends Record> records, int listPageSize,
            int listStartItem, int listTotalCount) {
        this.fileRecord = fileRecord;
        this.jobRecord = jobRecord;
        this.annotation = annotation;
        if (annotations != null) {
            this.annotations = Collections.unmodifiableList(annotations);
        } else {
            this.annotations = null;
        }
        if (records != null) {
            this.records = Collections.unmodifiableList(records);
        } else {
            this.records = null;
        }
        this.listPageSize = listPageSize;
        this.listStartItem = listStartItem;
        this.listTotalCount = listTotalCount;
    }

    public DataContainer(FileRecord fileRecord) {
        this(fileRecord, null, null, null, null, 0, 0, 0);
    }

    public DataContainer(JobRecord jobRecord) {
        this(null, jobRecord, null, null, null, 0, 0, 0);
    }

    public DataContainer(Annotation annotation) {
        this(null, null, annotation, null, null, 0, 0, 0);
    }

    public static DataContainer newAnnotationListContainer(
            List<? extends Annotation> annotations) {
        return new DataContainer(null, null, null, annotations, null, 0, 0, 0);
    }

    public static DataContainer newRecordListContainer(
            List<? extends Record> records, int listPageSize,
            int listStartItem, int listTotalCount) {
        return new DataContainer(null, null, null, null, records, listPageSize,
                listStartItem, listTotalCount);
    }
}
