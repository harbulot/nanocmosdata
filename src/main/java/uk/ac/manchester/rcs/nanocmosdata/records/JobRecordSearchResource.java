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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.restlet.data.Form;
import org.restlet.data.MediaType;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;

import uk.ac.manchester.rcs.corypha.core.CoryphaTemplateUtil;
import uk.ac.manchester.rcs.corypha.core.HibernateFilter;
import uk.ac.manchester.rcs.nanocmosdata.records.legacy.CommonConstants;
import uk.ac.nanocmos.datamanagement.service.records.storage.Search;

public class JobRecordSearchResource extends JobRecordListResource {
    private static final Log LOGGER = LogFactory
            .getLog(JobRecordSearchResource.class);
    protected final String SEARCH_RESULTS_NAMESPACE = CommonConstants.HYPERMEDIA_XML_NAMESPACE;
    protected final String SEARCH_RESULTS_LOCALNAME = "SearchResults";

    protected String searchID;
    protected Search search;

    protected Form searchForm;

    @Override
    public void doInit() {
        super.doInit();

        this.hibernateSession = HibernateFilter.getSession(getContext(),
                getRequest());

        this.searchID = (String) getRequest().getAttributes().get(
                FileRecordModule.MAIN_ID_ATTRIBUTE);

        System.out.println("Search ID: " + searchID);
        if (this.searchID != null) {
            this.search = (Search) this.hibernateSession.get(Search.class,
                    this.searchID);
            if ((this.search != null)
                    && ("form".equals(this.search.getQueryFormat()))) {
                this.searchForm = new Form(this.search.getQueryContent());
            }
        } else {
            this.searchForm = getReference().getQueryAsForm();
        }

        if (this.searchForm == null) {
            setExisting(false);
        }

        setNegotiated(true);
    }

    @Override
    protected Criteria refineCriteria(Criteria criteria) {
        String owner = searchForm.getFirstValue("owner");
        if ((owner != null) && (owner.length() > 0)) {
            criteria = criteria.add(Restrictions.like("owner", owner));
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("d MMM yyyy HH:mm");

        {
            Date fromDate = null;
            Date toDate = null;
            String fromDateStr = searchForm
                    .getFirstValue("jobcreationtime_from");
            if ((fromDateStr != null) && (fromDateStr.length() > 0)) {
                try {
                    fromDate = dateFormat.parse(fromDateStr);
                } catch (ParseException e) {
                    LOGGER
                            .error("Incorrect input not treated. TODO: implement");
                }
            }
            String toDateStr = searchForm.getFirstValue("jobcreationtime_to");
            if ((toDateStr != null) && (toDateStr.length() > 0)) {
                try {
                    toDate = dateFormat.parse(toDateStr);
                } catch (ParseException e) {
                    LOGGER
                            .error("Incorrect input not treated. TODO: implement");
                }
            }
            if ((fromDate != null) && (toDate != null)) {
                criteria = criteria.add(Restrictions.between("jobCreationTime",
                        fromDate, toDate));
            } else if (fromDate != null) {
                criteria = criteria.add(Restrictions.ge("jobCreationTime",
                        fromDate));
            } else if (toDate != null) {
                criteria = criteria.add(Restrictions.le("jobCreationTime",
                        toDate));
            }
        }

        {
            Date fromDate = null;
            Date toDate = null;
            String fromDateStr = searchForm.getFirstValue("jobstarttime_from");
            if ((fromDateStr != null) && (fromDateStr.length() > 0)) {
                try {
                    fromDate = dateFormat.parse(fromDateStr);
                } catch (ParseException e) {
                    LOGGER
                            .error("Incorrect input not treated. TODO: implement");
                }
            }
            String toDateStr = searchForm.getFirstValue("jobstarttime_to");
            if ((toDateStr != null) && (toDateStr.length() > 0)) {
                try {
                    toDate = dateFormat.parse(toDateStr);
                } catch (ParseException e) {
                    LOGGER
                            .error("Incorrect input not treated. TODO: implement");
                }
            }
            if ((fromDate != null) && (toDate != null)) {
                criteria = criteria.add(Restrictions.between("jobStartTime",
                        fromDate, toDate));
            } else if (fromDate != null) {
                criteria = criteria.add(Restrictions.ge("jobStartTime",
                        fromDate));
            } else if (toDate != null) {
                criteria = criteria
                        .add(Restrictions.le("jobStartTime", toDate));
            }
        }

        {
            Date fromDate = null;
            Date toDate = null;
            String fromDateStr = searchForm.getFirstValue("jobendtime_from");
            if ((fromDateStr != null) && (fromDateStr.length() > 0)) {
                try {
                    fromDate = dateFormat.parse(fromDateStr);
                } catch (ParseException e) {
                    LOGGER
                            .error("Incorrect input not treated. TODO: implement");
                }
            }
            String toDateStr = searchForm.getFirstValue("jobendtime_to");
            if ((toDateStr != null) && (toDateStr.length() > 0)) {
                try {
                    toDate = dateFormat.parse(toDateStr);
                } catch (ParseException e) {
                    LOGGER
                            .error("Incorrect input not treated. TODO: implement");
                }
            }
            if ((fromDate != null) && (toDate != null)) {
                criteria = criteria.add(Restrictions.between("jobEndTime",
                        fromDate, toDate));
            } else if (fromDate != null) {
                criteria = criteria
                        .add(Restrictions.ge("jobEndtime", fromDate));
            } else if (toDate != null) {
                criteria = criteria.add(Restrictions.le("jobEndTime", toDate));
            }
        }

        return criteria;
    }

    @Get("*:html")
    public Representation toHtml() {
        if (this.searchID != null) {
            return CoryphaTemplateUtil.buildTemplateRepresentation(
                    getContext(), getRequest(),
                    "jobrecordsearchresults.ftl.html", null,
                    MediaType.TEXT_HTML);
        } else {
            return CoryphaTemplateUtil.buildTemplateRepresentation(
                    getContext(), getRequest(), "jobrecordsearch.ftl.html",
                    null, MediaType.TEXT_HTML);
        }
    }
}
