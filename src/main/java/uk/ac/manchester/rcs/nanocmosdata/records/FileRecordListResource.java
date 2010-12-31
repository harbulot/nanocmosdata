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

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Subqueries;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.restlet.ext.json.JsonRepresentation;
import org.restlet.representation.Representation;
import org.restlet.resource.Get;
import org.restlet.resource.ResourceException;
import org.restlet.resource.ServerResource;

import uk.ac.manchester.rcs.corypha.core.HibernateFilter;
import uk.ac.nanocmos.datamanagement.service.records.storage.FileRecord;

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
public class FileRecordListResource extends ServerResource {
    public final static String ORACLE11G_WORKAROUND_FLAG_PARAMETER = "oracle11g_workaround";

    @SuppressWarnings("unused")
    private static final Log LOGGER = LogFactory
            .getLog(FileRecordListResource.class);

    /**
     * Hibernate session used during the processing of this Resource.
     */
    protected Session hibernateSession;

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

        setExisting(true);
        setNegotiated(true);
    }

    protected Criteria refineCriteria(Criteria criteria) {
        return criteria;
    }

    @Get("*:json")
    public Representation toJson() throws ResourceException {
        try {
            Criteria crit;
            if ("true"
                    .equalsIgnoreCase(getContext().getParameters()
                            .getFirstValue(ORACLE11G_WORKAROUND_FLAG_PARAMETER,
                                    "false"))) {
                crit = this.hibernateSession.createCriteria(FileRecord.class);
                crit = refineCriteria(crit);
            } else {
                DetachedCriteria dcrit = DetachedCriteria
                        .forClass(FileRecord.class);
                dcrit
                        .setResultTransformer(CriteriaSpecification.DISTINCT_ROOT_ENTITY);
                dcrit.setProjection(Projections.id());
                crit = this.hibernateSession.createCriteria(FileRecord.class);
                crit = refineCriteria(crit);
                crit.add(Subqueries.propertyIn("id", dcrit));
            }

            Long rowCount = (Long) crit.setProjection(Projections.rowCount())
                    .uniqueResult();
            if (rowCount == null) {
                rowCount = 0L;
            }
            Long.toString(rowCount);
            crit.setProjection(null);

            int firstResult;
            try {
                firstResult = Integer.parseInt(getReference().getQueryAsForm()
                        .getFirstValue("iDisplayStart"));
            } catch (NumberFormatException e) {
                firstResult = 1;
            }
            crit.setFirstResult(firstResult);

            int maxResults;
            try {
                maxResults = Integer.parseInt(getReference().getQueryAsForm()
                        .getFirstValue("iDisplayLength"));
            } catch (NumberFormatException e) {
                maxResults = 10;
            }
            crit.setMaxResults(maxResults);

            int sortcol;
            try {
                sortcol = Integer.parseInt(getReference().getQueryAsForm()
                        .getFirstValue("iSortCol_0"));
            } catch (NumberFormatException e) {
                sortcol = 0;
            }
            String sortPropName;
            switch (sortcol) {
            case 0:
                sortPropName = "id";
                break;
            case 1:
                sortPropName = "owner";
                break;
            case 2:
                sortPropName = "type";
                break;
            case 3:
                sortPropName = "location";
                break;
            default:
                sortPropName = "id";
            }

            Property sortProp = Property.forName(sortPropName);
            String sortDir = getReference().getQueryAsForm().getFirstValue(
                    "sSortDir_0");
            if ((sortDir == null) || (!sortDir.equals("desc"))) {
                crit.addOrder(sortProp.asc());
            } else {
                crit.addOrder(sortProp.desc());
            }

            @SuppressWarnings("unchecked")
            List<FileRecord> records = (List<FileRecord>) crit.list();
            JSONArray rows = new JSONArray();
            for (FileRecord record : records) {
                JSONArray row = new JSONArray();
                if (record.getId() != null) {
                    row.put(record.getId());
                } else {
                    row.put("");
                }
                if (record.getOwner() != null) {
                    row.put(record.getOwner());
                } else {
                    row.put("");
                }
                if (record.getType() != null) {
                    row.put(record.getType());
                } else {
                    row.put("");
                }
                if (record.getLocation() != null) {
                    row.put(record.getLocation());
                } else {
                    row.put("");
                }
                rows.put(row);
            }

            this.hibernateSession.getTransaction().commit();
            JSONObject data = new JSONObject();

            data.put("sEcho", getReference().getQueryAsForm().getFirstValue(
                    "sEcho"));
            data.put("iTotalRecords", rowCount);
            data.put("iTotalDisplayRecords", rowCount);
            data.put("aaData", rows);
            return new JsonRepresentation(data);
        } catch (JSONException e) {
            throw new ResourceException(e);
        }
    }
}
