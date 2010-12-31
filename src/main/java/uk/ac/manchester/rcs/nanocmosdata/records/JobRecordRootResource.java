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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.json.JSONObject;
import org.restlet.data.CharacterSet;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.data.Status;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;
import org.restlet.security.User;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import uk.ac.manchester.rcs.corypha.core.CoryphaTemplateUtil;
import uk.ac.manchester.rcs.corypha.core.HibernateFilter;
import uk.ac.manchester.rcs.nanocmosdata.records.legacy.DataContainer;
import uk.ac.manchester.rcs.nanocmosdata.records.legacy.UnMarshaller;
import uk.ac.manchester.rcs.nanocmosdata.records.legacy.UnMarshaller.UnmarshallingException;
import uk.ac.nanocmos.datamanagement.service.records.storage.JobRecord;
import uk.ac.nanocmos.datamanagement.service.records.storage.Search;

public class JobRecordRootResource extends ServerResource {
    private static final Log LOGGER = LogFactory
            .getLog(JobRecordRootResource.class);

    private Map<String, Object> data = null;
    private String createdSearch = null;

    @Override
    public void doInit() {
        super.doInit();

        setNegotiated(true);
    }

    @Get("html")
    public Representation toHtml() {
        return CoryphaTemplateUtil.buildTemplateRepresentation(getContext(),
                getRequest(), "jobrecordroot.ftl.html", data,
                MediaType.TEXT_HTML);
    }

    private void createNewRecord(Form form) {
        JobRecord record = new JobRecord();
        record.setId(UUID.randomUUID().toString());
        User user = getClientInfo().getUser();
        if (user != null) {
            record.setOwner(user.getName());
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm");
        String jobcreationtime = form.getFirstValue("newrec_jobcreationtime");
        if (jobcreationtime != null) {
            try {
                Date date = dateFormat.parse(jobcreationtime);
                record.setJobCreationTime(date);
            } catch (ParseException e) {
                LOGGER.error("Incorrect input not treated. TODO: implement");
            }
        }
        String jobstarttime = form.getFirstValue("newrec_jobstarttime");
        if (jobstarttime != null) {
            try {
                Date date = dateFormat.parse(jobstarttime);
                record.setJobStartTime(date);
            } catch (ParseException e) {
                LOGGER.error("Incorrect input not treated. TODO: implement");
            }
        }
        String jobendtime = form.getFirstValue("newrec_jobendtime");
        if (jobendtime != null) {
            try {
                Date date = dateFormat.parse(jobendtime);
                record.setJobEndTime(date);
            } catch (ParseException e) {
                LOGGER.error("Incorrect input not treated. TODO: implement");
            }
        }

        Session hibernateSession = HibernateFilter.getSession(getContext(),
                getRequest());
        hibernateSession.save(record);
        hibernateSession.getTransaction().commit();
        String newRecordUri = String.format("%s%s/%s/", getReference()
                .getTargetRef().toString(), FileRecordModule.ITEMS_PATHELEMENT,
                record.getId());

        setStatus(Status.SUCCESS_CREATED);
        setLocationRef(newRecordUri);

        data = new HashMap<String, Object>();
        data.put("new_record_uri", newRecordUri);
    }

    private void createNewRecord(Document doc) {
        JobRecord record = new JobRecord();
        record.setId(UUID.randomUUID().toString());

        DataContainer dataContainer;
        try {
            dataContainer = UnMarshaller.getInstance().unmarshall(
                    doc.getDocumentElement(), record);
            if (dataContainer != null) {
                User user = getClientInfo().getUser();
                if (user != null) {
                    record.setOwner(user.getName());
                }
                Session hibernateSession = HibernateFilter.getSession(
                        getContext(), getRequest());
                hibernateSession.save(record);
                hibernateSession.getTransaction().commit();
                String newRecordUri = String.format("%s%s/%s/", getReference()
                        .getTargetRef().toString(),
                        JobRecordModule.ITEMS_PATHELEMENT, record.getId());

                setStatus(Status.SUCCESS_CREATED);
                setLocationRef(newRecordUri);

                data = new HashMap<String, Object>();
                data.put("new_record_uri", newRecordUri);
            }
        } catch (UnmarshallingException e) {
            LOGGER.error("Incorrect input not treated. TODO: implement", e);
        }
    }

    private void createNewSearch(Form form) {
        try {
            Session hibernateSession = HibernateFilter.getSession(getContext(),
                    getRequest());
            UUID uuid = UUID.randomUUID();
            Search search = new Search();
            search.setId(uuid.toString());
            search.setQueryFormat("form");
            search.setQueryContent(form.encode(CharacterSet.UTF_8));
            hibernateSession.save(search);
            hibernateSession.getTransaction().commit();
            String newSearchUri = String.format("%s%s/%s", getReference()
                    .getTargetRef().toString(),
                    FileRecordModule.SEARCH_PATHELEMENT, search.getId());
            this.createdSearch = newSearchUri;

            setStatus(Status.REDIRECTION_FOUND);
            setLocationRef(newSearchUri);
        } catch (IOException e) {
            setStatus(Status.SERVER_ERROR_INTERNAL, e);
        }
    }

    private void processPostRequest(Representation entity) {
        if (MediaType.APPLICATION_WWW_FORM.isCompatible(entity.getMediaType())) {
            Form form = new Form(entity);
            String submit_type = form.getFirstValue("form_submit");
            if ("create-record".equals(submit_type)) {
                createNewRecord(form);
            } else if ("create-search".equals(submit_type)) {
                createNewSearch(form);
            } else {
                setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            }
        } else if (MediaType.APPLICATION_XML
                .isCompatible(entity.getMediaType())) {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            dbf.setValidating(false);
            try {
                DocumentBuilder db = dbf.newDocumentBuilder();
                createNewRecord(db.parse(entity.getStream()));
            } catch (ParserConfigurationException e) {
                LOGGER.error("Incorrect input not treated. TODO: implement", e);
            } catch (SAXException e) {
                LOGGER.error("Incorrect input not treated. TODO: implement", e);
            } catch (IOException e) {
                LOGGER.error("Incorrect input not treated. TODO: implement", e);
            }
        }
    }

    @Post("form:html")
    public Representation postHtml(Representation entity) {
        processPostRequest(entity);
        if ((data == null) && (createdSearch == null)) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
        return toHtml();
    }

    @Post("form:json")
    public Representation postJson(Representation entity) {
        processPostRequest(entity);
        if ((data == null) && (createdSearch == null)) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
            // TODO return something else than HTML
            return toHtml();
        } else {
            setStatus(Status.SUCCESS_CREATED);
            return new JsonRepresentation(new JSONObject(data));
        }
    }

    @Post("xml:html")
    public Representation postXml(Representation entity) {
        processPostRequest(entity);
        if ((data == null) && (createdSearch == null)) {
            setStatus(Status.CLIENT_ERROR_BAD_REQUEST);
        }
        return toHtml();
    }
}