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
package uk.ac.manchester.rcs.nanocmosdata.records;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.representation.OutputRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;
import org.restlet.representation.Variant;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import uk.ac.manchester.rcs.corypha.core.HibernateFilter;
import uk.ac.nanocmos.datamanagement.service.records.storage.FileRecord;
import uk.ac.nanocmos.datamanagement.service.records.storage.FileRecordAnnotation;

/**
 * This is an abstract resource that provides the methods common to the record
 * annotation resources.
 * 
 * @author Bruno Harbulot (Bruno.Harbulot@manchester.ac.uk)
 * 
 */
public class FileRecordAnnotationResource extends ServerResource {
    @SuppressWarnings("unused")
    private static final Log LOGGER = LogFactory
            .getLog(FileRecordAnnotationResource.class);

    /**
     * Hibernate session used during the processing of this Resource.
     */
    protected Session hibernateSession;

    /**
     * ID of the record (key in the database).
     */
    protected String recordID;
    protected String annotationID;
    /**
     * Instance loaded from the DB.
     */
    protected FileRecord record;
    protected FileRecordAnnotation annotation;

    /**
     * Extracts the resource ID from the request and loads the record.
     * 
     * @return true if such a record exists, false if it doesn't.
     */
    @Override
    public void doInit() {
        super.doInit();

        this.hibernateSession = HibernateFilter.getSession(getContext(),
                getRequest());

        this.recordID = (String) getRequest().getAttributes().get(
                FileRecordModule.MAIN_ID_ATTRIBUTE);

        this.record = (FileRecord) this.hibernateSession.get(FileRecord.class,
                this.recordID);

        this.annotationID = (String) getRequest().getAttributes().get(
                FileRecordModule.ANNOTATION_ID_ATTRIBUTE);

        if (this.record != null) {
            try {
                long id = Long.parseLong(annotationID);
                this.annotation = (FileRecordAnnotation) this.hibernateSession
                        .get(FileRecordAnnotation.class, id);
                if ((this.annotation != null)
                        && (this.annotation.getContainer().getId()
                                .equals(recordID))) {

                    if (this.annotation.getContentType() != null) {
                        getVariants().add(
                                new Variant(MediaType.valueOf(this.annotation
                                        .getContentType())));
                    }
                    setExisting(true);
                    if (this.annotation.getXmlContent() != null) {
                        getVariants().add(
                                new Variant(MediaType.APPLICATION_XML));
                    } else if (this.annotation.getTextContent() != null) {
                        getVariants().add(new Variant(MediaType.TEXT_PLAIN));
                    } else if (this.annotation.getBinaryContent() != null) {
                        getVariants()
                                .add(
                                        new Variant(
                                                MediaType.APPLICATION_OCTET_STREAM));
                    } else {
                        setExisting(false);
                    }
                } else {
                    this.annotation = null;
                }
            } catch (NumberFormatException e) {
                this.annotation = null;
            }

            if (this.annotation == null) {
                setExisting(false);
            }
        } else {
            setExisting(false);
        }
        setNegotiated(false);
    }

    @Override
    public Representation get() throws ResourceException {
        if (this.annotation.getXmlContent() != null) {
            return new StringRepresentation(annotation.getXmlContent(),
                    MediaType.valueOf(this.annotation.getContentType()));
        } else if (this.annotation.getTextContent() != null) {
            return new StringRepresentation(annotation.getTextContent(),
                    MediaType.valueOf(this.annotation.getContentType()));
        } else if (this.annotation.getBinaryContent() != null) {
            return new OutputRepresentation(MediaType.valueOf(this.annotation
                    .getContentType())) {
                @Override
                public void write(OutputStream os) throws IOException {
                    os.write(FileRecordAnnotationResource.this.annotation
                            .getBinaryContent());
                }
            };
        } else {
            return null;
        }
    }

    /**
     * Processes PUT requests.
     */
    @Override
    public Representation put(Representation entity) {
        try {
            // Annotation<? extends Record> annotation =
            // getRecordStorageManager()
            // .newAnnotation();
            // annotation.setId(this.annotationID);

            annotation.setContentType(entity.getMediaType().toString());
            if (entity.getMediaType().isCompatible(
                    MediaType.APPLICATION_ALL_XML)) {
                // TODO check that it's actually XML.
                annotation.setXmlContent(entity.getText());
                annotation.setTextContent(null);
                annotation.setBinaryContent(null);
                annotation.setLink(null);
            } else if (entity.getMediaType().isCompatible(MediaType.TEXT_PLAIN)) {
                annotation.setXmlContent(null);
                annotation.setTextContent(entity.getText());
                annotation.setBinaryContent(null);
                annotation.setLink(null);
            } else {
                annotation.setXmlContent(null);
                annotation.setTextContent(null);
                byte[] binaryContent = null;
                if (entity.getSize() < 0) {
                    // TODO handle error
                    throw new RuntimeException();
                } else {
                    // TODO improve handling of binary data (perhaps via a
                    // stream)

                    InputStream is = null;
                    try {
                        is = entity.getStream();
                        binaryContent = new byte[(int) entity.getSize()];
                        int offset = 0;
                        int read = 0;
                        while (offset < binaryContent.length
                                && (read = is.read(binaryContent, offset,
                                        binaryContent.length - offset)) >= 0) {
                            offset += read;
                        }
                    } finally {
                        if (is != null) {
                            is.close();
                        }
                    }
                }
                annotation.setBinaryContent(binaryContent);
                annotation.setLink(null);
            }
            this.hibernateSession.saveOrUpdate(annotation);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public Representation delete(Variant variant) {
        // TODO fix return types
        if (this.annotation != null) {
            this.hibernateSession.delete(annotation);
        }

        getResponse().setStatus(Status.SUCCESS_OK);
        return null;
    }

    // /**
    // * Processes POST requests.
    // */
    // @Override
    // public Representation post(Representation entity, Variant variant) {
    // try {
    //
    // FileRecordAnnotation annotation = new FileRecordAnnotation();
    //
    // annotation.setContentType(entity.getMediaType().toString());
    // if (entity.getMediaType().isCompatible(
    // MediaType.APPLICATION_ALL_XML)) {
    // String textContent =
    // AnnotationDocument xbAnnotationDoc = AnnotationDocument.Factory
    // .parse(entity.getStream());
    // AnnotationType xbAnnotation = xbAnnotationDoc.getAnnotation();
    //
    // XmlCursor xmlCursor = xbAnnotation.newCursor();
    // if (xmlCursor.toFirstChild()) {
    // // TODO check for some rough validity criterion (e.g. no
    // // sub-annotation).
    // annotation
    // .setXmlContent();
    // }
    // annotation.setTextContent(null);
    // annotation.setBinaryContent(null);
    // annotation.setLink(null);
    // } else if (entity.getMediaType().isCompatible(MediaType.TEXT_PLAIN)) {
    // annotation.setXmlContent(null);
    // annotation.setTextContent(entity.getText());
    // annotation.setBinaryContent(null);
    // annotation.setLink(null);
    // } else {
    // annotation.setXmlContent(null);
    // annotation.setTextContent(null);
    // byte[] binaryContent = null;
    // if (entity.getSize() < 0) {
    // // TODO handle error
    // throw new RuntimeException();
    // } else {
    // // TODO improve handling of binary data (perhaps via a
    // // stream)
    //
    // InputStream is = null;
    // try {
    // is = entity.getStream();
    // binaryContent = new byte[(int) entity.getSize()];
    // int offset = 0;
    // int read = 0;
    // while (offset < binaryContent.length
    // && (read = is.read(binaryContent, offset,
    // binaryContent.length - offset)) >= 0) {
    // offset += read;
    // }
    // } finally {
    // if (is != null) {
    // is.close();
    // }
    // }
    // }
    // annotation.setBinaryContent(binaryContent);
    // annotation.setLink(null);
    // }
    // getRecordStorageManager().saveRecordAnnotation(annotation);
    //
    // getResponse().setStatus(Status.SUCCESS_OK);
    // } catch (XmlException e1) {
    // getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    // throw new TransactionException(e1);
    // } catch (IOException e1) {
    // getResponse().setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
    // throw new TransactionException(e1);
    // }
    // return null;
    // }
}
