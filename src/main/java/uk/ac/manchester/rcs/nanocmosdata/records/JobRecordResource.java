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

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Session;
import org.restlet.data.MediaType;
import org.restlet.ext.freemarker.TemplateRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;

import uk.ac.manchester.rcs.corypha.core.CoryphaTemplateUtil;
import uk.ac.manchester.rcs.corypha.core.HibernateFilter;
import uk.ac.nanocmos.datamanagement.service.records.storage.JobRecord;

/**
 * This is a resource class for the records.
 * 
 * @author Bruno Harbulot (Bruno.Harbulot@manchester.ac.uk)
 * 
 * @param <RecordT>
 *            type of record
 * @param <CoreDataT>
 *            type of core-data (XmlBeans)
 */
public class JobRecordResource extends ServerResource {
    @SuppressWarnings("unused")
    private static final Log LOGGER = LogFactory
            .getLog(JobRecordResource.class);

    /**
     * Hibernate session used during the processing of this Resource.
     */
    protected Session hibernateSession;

    /**
     * ID of the record (key in the database).
     */
    protected String recordID;
    /**
     * Instance loaded from the DB.
     */
    protected JobRecord record;

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
                JobRecordModule.MAIN_ID_ATTRIBUTE);

        this.record = (JobRecord) this.hibernateSession.get(JobRecord.class,
                this.recordID);
        setExisting(this.record != null);

        if (this.record != null) {
            this.record.getAnnotations().size();
            this.record.getChildJobRecords().size();
            this.record.getChildFileRecords().size();
        }

        setNegotiated(true);
    }

    @Get("html")
    public Representation toHtml() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("record", this.record);
        return CoryphaTemplateUtil.buildTemplateRepresentation(getContext(),
                getRequest(), "jobrecord.ftl.html", data, MediaType.TEXT_HTML);
    }

    @Get("xml")
    public Representation toXml() {
        HashMap<String, Object> data = new HashMap<String, Object>();
        data.put("record", this.record);
        return new TemplateRepresentation("jobrecord.ftl.xml",
                CoryphaTemplateUtil.getConfiguration(getContext()), data,
                MediaType.APPLICATION_XML);
    }
}
