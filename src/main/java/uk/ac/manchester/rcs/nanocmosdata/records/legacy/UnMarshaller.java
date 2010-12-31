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

import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import uk.ac.nanocmos.datamanagement.service.records.storage.Annotation;
import uk.ac.nanocmos.datamanagement.service.records.storage.FileRecord;
import uk.ac.nanocmos.datamanagement.service.records.storage.FileRecordAnnotation;
import uk.ac.nanocmos.datamanagement.service.records.storage.JobRecord;
import uk.ac.nanocmos.datamanagement.service.records.storage.JobRecordAnnotation;
import uk.ac.nanocmos.datamanagement.service.records.storage.Record;

/**
 * @author Bruno Harbulot (Bruno.Harbulot@manchester.ac.uk)
 * 
 */
public class UnMarshaller {
    private static final Log LOGGER = LogFactory.getLog(UnMarshaller.class);

    private static final class Singleton {
        private static final UnMarshaller instance = new UnMarshaller();
    }

    public static UnMarshaller getInstance() {
        return Singleton.instance;
    }

    private static ThreadLocal<XPathFactory> XPATH_FACTORY = new ThreadLocal<XPathFactory>();
    private static ThreadLocal<XPath> XPATH = new ThreadLocal<XPath>();

    private static XPathFactory getXPathFactory() {
        XPathFactory xPathFactory = XPATH_FACTORY.get();
        if (xPathFactory == null) {
            xPathFactory = XPathFactory.newInstance();
            XPATH_FACTORY.set(xPathFactory);
        }
        return xPathFactory;
    }

    private static XPath getXPath() {
        XPath xPath = XPATH.get();
        if (xPath == null) {
            xPath = getXPathFactory().newXPath();
            xPath.setNamespaceContext(new NamespaceContext() {
                public String getNamespaceURI(String prefix) {
                    if ("nc".equals(prefix)) {
                        return CommonConstants.HYPERMEDIA_XML_NAMESPACE;
                    }
                    return XMLConstants.NULL_NS_URI;
                }

                public String getPrefix(String namespaceURI) {
                    throw new UnsupportedOperationException();
                }

                public Iterator<String> getPrefixes(String namespaceURI) {
                    throw new UnsupportedOperationException();
                }
            });
            XPATH.set(xPath);
        }
        return xPath;
    }

    private static XPathExpression getXPathExpr(String strExpr)
            throws XPathExpressionException {
        XPathExpression expr = getXPath().compile(strExpr);
        return expr;
    }

    private UnMarshaller() {

    }

    private DataContainer unmarshallFileRecordCoreData(Element element,
            FileRecord fileRecord) throws UnmarshallingException {
        if (fileRecord == null) {
            fileRecord = new FileRecord();
        }
        DataContainer dataContainer = new DataContainer(fileRecord);

        try {
            Element locationElem = (Element) getXPathExpr("nc:location")
                    .evaluate(element, XPathConstants.NODE);
            if (locationElem != null) {
                fileRecord.setLocation(locationElem.getTextContent());
            }
            Element typeElem = (Element) getXPathExpr("nc:type").evaluate(
                    element, XPathConstants.NODE);
            if (typeElem != null) {
                fileRecord.setType(typeElem.getTextContent());
            }
            Element parentJobElem = (Element) getXPathExpr("nc:parentJob")
                    .evaluate(element, XPathConstants.NODE);
            if (parentJobElem != null) {
                fileRecord.setParentJobRecord(parentJobElem.getTextContent());
            }
            Element ownerElem = (Element) getXPathExpr("nc:owner").evaluate(
                    element, XPathConstants.NODE);
            if (ownerElem != null) {
                fileRecord.setOwner(ownerElem.getTextContent());
            }
        } catch (XPathExpressionException e) {
            throw new UnmarshallingException(e);
        } catch (DOMException e) {
            throw new UnmarshallingException(e);
        }

        return dataContainer;
    }

    private DataContainer unmarshallJobRecordCoreData(Element element,
            JobRecord jobRecord) throws UnmarshallingException {
        if (jobRecord == null) {
            jobRecord = new JobRecord();
        }
        DataContainer dataContainer = new DataContainer(jobRecord);

        SimpleDateFormat dateFormatter = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss");

        try {
            Element parentJobElem = (Element) getXPathExpr("nc:parentJob")
                    .evaluate(element, XPathConstants.NODE);
            if (parentJobElem != null) {
                jobRecord.setParentJobRecord(parentJobElem.getTextContent());
            }
            Element ownerElem = (Element) getXPathExpr("nc:owner").evaluate(
                    element, XPathConstants.NODE);
            if (ownerElem != null) {
                jobRecord.setOwner(ownerElem.getTextContent());
            }

            Element jobCreationTimeElem = (Element) getXPathExpr(
                    "nc:jobCreationTime")
                    .evaluate(element, XPathConstants.NODE);
            if (jobCreationTimeElem != null) {
                if (jobCreationTimeElem.getTextContent().length() > 0) {
                    jobRecord.setJobCreationTime(dateFormatter
                            .parse(jobCreationTimeElem.getTextContent()));
                } else {
                    jobRecord.setJobCreationTime(null);
                }
            }

            Element jobStartTimeElem = (Element) getXPathExpr("nc:jobStartTime")
                    .evaluate(element, XPathConstants.NODE);
            if (jobStartTimeElem != null) {
                if (jobStartTimeElem.getTextContent().length() > 0) {
                    jobRecord.setJobStartTime(dateFormatter
                            .parse(jobStartTimeElem.getTextContent()));
                } else {
                    jobRecord.setJobStartTime(null);
                }
            }
            Element jobEndTimeElem = (Element) getXPathExpr("nc:jobEndTime")
                    .evaluate(element, XPathConstants.NODE);
            if (jobEndTimeElem != null) {
                if (jobEndTimeElem.getTextContent().length() > 0) {
                    jobRecord.setJobEndTime(dateFormatter.parse(jobEndTimeElem
                            .getTextContent()));
                } else {
                    jobRecord.setJobEndTime(null);
                }
            }
        } catch (XPathExpressionException e) {
            throw new UnmarshallingException(e);
        } catch (DOMException e) {
            throw new UnmarshallingException(e);
        } catch (ParseException e) {
            throw new UnmarshallingException(e);
        }

        return dataContainer;
    }

    private DataContainer unmarshallAnnotation(Element element, Record record)
            throws UnmarshallingException {
        DataContainer dataContainer = null;

        NodeList childNodes = element.getChildNodes();
        Element innerElement = null;
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node instanceof Element) {
                innerElement = (Element) node;
                break;
            }
        }
        if (innerElement == null) {
            LOGGER.warn("Couldn't find core data element.");
            return null;
        }

        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            Transformer t = tFactory.newTransformer();

            Source xmlSource = new DOMSource(innerElement);
            StringWriter buffer = new StringWriter();
            StreamResult streamResult = new StreamResult(buffer);

            t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.setOutputProperty(OutputKeys.INDENT, "no");
            t.transform(xmlSource, streamResult);

            Annotation annotation;
            if (record instanceof FileRecord) {
                annotation = new FileRecordAnnotation();
            } else if (record instanceof JobRecord) {
                annotation = new JobRecordAnnotation();
            } else {
                // TODO + null case too
                annotation = null;
                return null;
            }

            annotation.setXmlContent(buffer.toString());
            annotation.setContentType("application/xml");

            dataContainer = new DataContainer(annotation);
        } catch (TransformerConfigurationException e) {
            throw new UnmarshallingException(e);
        } catch (IllegalArgumentException e) {
            throw new UnmarshallingException(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new UnmarshallingException(e);
        } catch (TransformerException e) {
            throw new UnmarshallingException(e);
        }

        return dataContainer;
    }

    private DataContainer unmarshallMetadata(Element element, Record record)
            throws UnmarshallingException {
        DataContainer dataContainer = null;

        try {
            NodeList annotationElements = (NodeList) getXPathExpr(
                    "nc:Annotation").evaluate(element, XPathConstants.NODESET);

            List<Annotation> annotations = null;
            if (record == null) {
                annotations = new ArrayList<Annotation>();
            }

            for (int i = 0; i < annotationElements.getLength(); i++) {
                Element annotationElement = (Element) annotationElements
                        .item(i);
                DataContainer annotationDataContainer = unmarshallAnnotation(
                        annotationElement, record);

                if ((annotationDataContainer != null)
                        && (annotationDataContainer.annotation != null)) {
                    if (record != null) {
                        record
                                .addAnnotation(annotationDataContainer.annotation);
                    } else {
                        annotations.add(annotationDataContainer.annotation);
                    }
                }
            }

            if (record == null) {
                dataContainer = DataContainer
                        .newAnnotationListContainer(annotations);
            } else {
                dataContainer = DataContainer.newAnnotationListContainer(record
                        .getAnnotations());
            }
        } catch (XPathExpressionException e) {
            throw new UnmarshallingException(e);
        }

        return dataContainer;
    }

    private DataContainer unmarshallRecord(Element element, Record record)
            throws UnmarshallingException {
        DataContainer dataContainer = null;

        try {
            Element coreDataElem = (Element) getXPathExpr(
                    "nc:FileRecordCoreData").evaluate(element,
                    XPathConstants.NODE);
            if (coreDataElem != null) {
                FileRecord fileRecord;
                if (record == null) {
                    fileRecord = new FileRecord();
                    record = fileRecord;
                } else {
                    fileRecord = (FileRecord) record;
                }
                unmarshallFileRecordCoreData(coreDataElem, fileRecord);
                dataContainer = new DataContainer(fileRecord);

            } else {
                coreDataElem = (Element) getXPathExpr("nc:JobCoreData")
                        .evaluate(element, XPathConstants.NODE);
                if (coreDataElem != null) {
                    JobRecord jobRecord;
                    if (record == null) {
                        jobRecord = new JobRecord();
                        record = jobRecord;
                    } else {
                        jobRecord = (JobRecord) record;
                    }
                    unmarshallJobRecordCoreData(coreDataElem, jobRecord);
                    dataContainer = new DataContainer(jobRecord);
                }
            }

            Element metaDataElem = (Element) getXPathExpr("nc:Metadata")
                    .evaluate(element, XPathConstants.NODE);
            if (metaDataElem != null) {
                unmarshallMetadata(metaDataElem, record);
            }
        } catch (XPathExpressionException e) {
            throw new UnmarshallingException(e);
        }

        return dataContainer;
    }

    public DataContainer unmarshall(Element element, Record record)
            throws UnmarshallingException {
        if ("Record".equals(element.getLocalName())
                && CommonConstants.HYPERMEDIA_XML_NAMESPACE.equals(element
                        .getNamespaceURI())) {
            return unmarshallRecord(element, record);
        } else if ("Metadata".equals(element.getLocalName())
                && CommonConstants.HYPERMEDIA_XML_NAMESPACE.equals(element
                        .getNamespaceURI())) {
            return unmarshallMetadata(element, record);
        } else if ("FileRecordCoreData".equals(element.getLocalName())
                && CommonConstants.HYPERMEDIA_XML_NAMESPACE.equals(element
                        .getNamespaceURI())) {
            try {
                return unmarshallFileRecordCoreData(element,
                        (FileRecord) record);
            } catch (ClassCastException e) {
                throw new UnmarshallingException(e);
            }
        } else if ("JobCoreData".equals(element.getLocalName())
                && CommonConstants.HYPERMEDIA_XML_NAMESPACE.equals(element
                        .getNamespaceURI())) {
            try {
                return unmarshallJobRecordCoreData(element, (JobRecord) record);
            } catch (ClassCastException e) {
                throw new UnmarshallingException(e);
            }
        }
        return null;
    }

    public DataContainer unmarshall(JSONObject jsonObject) {
        return null;
    }

    // /*
    // * From DOM to Record object.
    // */
    // public Annotation addAnnotation(Record record, AnnotationType
    // xbAnnotation) {
    // String href = xbAnnotation.getHref();
    // Annotation annotation = record.newAnnotation();
    // if (href != null) {
    // annotation.setLink(href);
    // } else {
    // XmlCursor xmlCursor = xbAnnotation.newCursor();
    // if (xmlCursor.toFirstChild()) {
    // // TODO check for some rough validity criterion (e.g. no
    // // sub-annotation).
    // annotation
    // .setXmlContent(xmlCursor
    // .xmlText(CommonConstants.XMLBEANS_FULLSTORAGE_XMLOPTIONS));
    // annotation.setContentType(MediaType.APPLICATION_XML.toString());
    // }
    // }
    // if (annotation != null) {
    // annotation.setContainer(record);
    // record.addAnnotation(annotation);
    // }
    // return annotation;
    // }
    //
    // protected void populateMetadata(Record record, Element domMetadata)
    // throws XmlException {
    // MetadataDocument xbMetadataDoc = MetadataDocument.Factory
    // .parse(domMetadata);
    // populateMetadata(record, xbMetadataDoc.getMetadata());
    // }
    //
    // protected void populateMetadata(Record record, MetadataType xbMetadata) {
    // for (AnnotationType xbAnnotation : xbMetadata.getAnnotationList()) {
    // addAnnotation(record, xbAnnotation);
    // }
    // }
    //
    // public void populateRecord(Context context, Request request,
    // Reference recordUri, Record record, Element domRecordElem)
    // throws XmlException {
    // RecordDocument xbRecordDoc = RecordDocument.Factory
    // .parse(domRecordElem);
    // RecordType xbRecord = xbRecordDoc.getRecord();
    //
    // LocalSequenceType xbCoreData = xbRecord.getFileRecordCoreData();
    // if (xbCoreData == null) {
    // xbCoreData = xbRecord.getJobCoreData();
    // }
    // if (xbCoreData != null) {
    // populateCoreData(context, request, recordUri, record,
    // (Element) xbCoreData.getDomNode());
    // }
    //
    // MetadataType xbMetadata = xbRecord.getMetadata();
    // if (xbMetadata != null) {
    // populateMetadata(record, (Element) xbMetadata.getDomNode());
    // }
    // }
    //
    // public void populateFileRecordCoreData(Context context, Request request,
    // Session hbSession, Reference recordUri, Record arecord,
    // Element domCoreData) {
    // FileRecord record = (FileRecord) arecord;
    // try {
    // FileRecordCoreDataDocument xbCoreDataDoc =
    // FileRecordCoreDataDocument.Factory
    // .parse(domCoreData);
    // FileRecordCoreDataType xbCoreData = xbCoreDataDoc
    // .getFileRecordCoreData();
    // if (xbCoreData != null) {
    // if (xbCoreData.isSetOwner()) {
    // record.setOwner(xbCoreData.getOwner());
    // }
    // record.setLocation(xbCoreData.getLocation());
    // record.setType(xbCoreData.getType());
    // record.setParentJobRecord(xbCoreData.getParentJob());
    //
    // Reference resourceRef = request.getResourceRef();
    //
    // String parentJobRecordUri = record.getParentJobRecord();
    //
    // LOGGER.debug("Parent job record: " + parentJobRecordUri);
    //
    // if (parentJobRecordUri != null) {
    // String deploymentUri = resourceRef.getScheme()
    // + "://"
    // + resourceRef.getAuthority()
    // + "/"
    // + context
    // .getParameters()
    // .getFirstValue(
    // MainNanoDataApplication.DEPLOYMENT_URL_CTX_PARAM_NAME);
    // parentJobRecordUri = parentJobRecordUri
    // .substring(deploymentUri.length());
    // LOGGER.debug("Parent job record (internal): "
    // + "riap://application/" + parentJobRecordUri);
    //
    // final Request internalReq = new Request(Method.HEAD,
    // "riap://application/" + parentJobRecordUri);
    // final Response internalResp = new Response(internalReq);
    // internalReq
    // .getAttributes()
    // .put(
    // HibernateTransactionFilter.HIBERNATE_SESSION_ATTRIBUTE,
    // request
    // .getAttributes()
    // .get(
    // HibernateTransactionFilter.HIBERNATE_SESSION_ATTRIBUTE));
    //
    // Uniform clientDispatcher = context.getClientDispatcher();
    // clientDispatcher.handle(internalReq, internalResp);
    // String parentJobRecordId = (String) internalReq
    // .getAttributes().get(
    // MainNanoDataApplication.MAIN_ID_ATTRIBUTE);
    // if (LOGGER.isDebugEnabled()) {
    // LOGGER.debug("Reponse status: "
    // + internalResp.getStatus());
    // LOGGER.debug("Parent job record ID: "
    // + parentJobRecordId);
    // // TODO check that this main ID is indeed that of a job
    // // record.
    // }
    //
    // RecordStorageManager<? extends JobRecord> jobRecordStorageManager = new
    // HibernateJobStorageManager(
    // hbSession);
    // JobRecord jobRecord = jobRecordStorageManager
    // .loadRecord(parentJobRecordId);
    // // TODO relative URIs...
    // // Reference fileRecordUri = new Reference(resourceRef
    // // .toString()
    // // + "/..");
    // // fileRecordUri.normalize();
    //
    // jobRecord.getChildFileRecords().add(
    // new JobRecordChildFileRec(recordUri.toString()));
    //
    // hbSession.save(jobRecord);
    // }
    // } else {
    // record.setLocation(null);
    // record.setType(null);
    // record.setParentJobRecord(null);
    // }
    // hbSession.save(record);
    // } catch (XmlException e) {
    // LOGGER.warn(e);
    // }
    // }

    public static class UnmarshallingException extends Exception {
        private static final long serialVersionUID = 1L;

        public UnmarshallingException(String msg, Throwable t) {
            super(msg, t);
        }

        public UnmarshallingException(String msg) {
            super(msg);
        }

        public UnmarshallingException(Throwable t) {
            super(t);
        }
    }
}
