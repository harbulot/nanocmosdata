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
import java.util.Date;

public class FileRecordAnnotation implements Annotation, Serializable {

    private static final long serialVersionUID = -6167654599614975750L;

    private Long id;
    private String annotationtype;
    private FileRecord fileRecord;
    private Date creationTime;
    private Date modificationTime;
    private String contentType;
    private String xmlContent;
    private String textContent;
    private byte[] binaryContent;
    private String link;

    public FileRecordAnnotation() {
    }

    public FileRecordAnnotation(FileRecord fileRecord) {
        this.fileRecord = fileRecord;
    }

    public FileRecordAnnotation(String annotationtype, FileRecord fileRecord,
            Date creationTime, Date modificationTime, String contentType,
            String xmlContent, String textContent, byte[] binaryContent,
            String link) {
        this.annotationtype = annotationtype;
        this.fileRecord = fileRecord;
        this.creationTime = creationTime == null ? null : new Date(creationTime
                .getTime());
        this.modificationTime = modificationTime == null ? null : new Date(
                modificationTime.getTime());
        this.contentType = contentType;
        this.xmlContent = xmlContent;
        this.textContent = textContent;
        this.binaryContent = binaryContent;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAnnotationtype() {
        return this.annotationtype;
    }

    public void setAnnotationtype(String annotationtype) {
        this.annotationtype = annotationtype;
    }

    public FileRecord getFileRecord() {
        return this.fileRecord;
    }

    public void setFileRecord(FileRecord fileRecord) {
        this.fileRecord = fileRecord;
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

    public String getContentType() {
        return this.contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getXmlContent() {
        return this.xmlContent;
    }

    public void setXmlContent(String xmlContent) {
        this.xmlContent = xmlContent;
    }

    public String getTextContent() {
        return this.textContent;
    }

    public void setTextContent(String textContent) {
        this.textContent = textContent;
    }

    public byte[] getBinaryContent() {
        return this.binaryContent;
    }

    public void setBinaryContent(byte[] binaryContent) {
        this.binaryContent = binaryContent;
    }

    public String getLink() {
        return this.link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public void setContainer(Record container) {
        setFileRecord((FileRecord) container);
    }

    public Record getContainer() {
        return getFileRecord();
    }
}
