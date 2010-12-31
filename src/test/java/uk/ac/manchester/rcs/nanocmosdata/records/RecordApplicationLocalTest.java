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

import java.util.Date;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.junit.Ignore;
import org.restlet.Component;
import org.restlet.Context;
import org.restlet.data.Protocol;

import uk.ac.manchester.rcs.corypha.core.CoryphaModule;
import uk.ac.manchester.rcs.corypha.core.CoryphaRootApplication;
import uk.ac.manchester.rcs.corypha.core.IHibernateConfigurationContributor;
import uk.ac.manchester.rcs.nanocmosdata.records.FileRecordModule;
import uk.ac.manchester.rcs.nanocmosdata.records.JobRecordModule;
import uk.ac.manchester.rcs.nanocmosdata.records.NanoDataModule;
import uk.ac.nanocmos.datamanagement.service.records.storage.FileRecord;

/**
 * @author Bruno Harbulot
 * 
 */
@Ignore
public class RecordApplicationLocalTest {
    public static class TestInitialConfigModule extends CoryphaModule implements
            IHibernateConfigurationContributor {
        @Override
        public void configureHibernate(AnnotationConfiguration configuration) {
            configuration.configure("hibernate-local-test.cfg.xml");
        }
    }

    public static class TestDummyRecordsModule extends CoryphaModule implements
            IHibernateConfigurationContributor {
        @Override
        public void configureHibernate(AnnotationConfiguration configuration) {
            try {
                SessionFactory sf = configuration.buildSessionFactory();
                Session session = sf.openSession();
                session.beginTransaction();
                try {
                    FileRecord record = new FileRecord("0001");
                    if (session.get(FileRecord.class, record.getId()) == null) {
                        record.setCreationTime(new Date());
                        record.setModificationTime(new Date());
                        record.setLocation("http://example.org/test-location/");
                        record.setType("text/plain");
                        record.setOwner("Bruno");
                        session.save(record);
                    }

                    record = new FileRecord("0002");
                    if (session.get(FileRecord.class, record.getId()) == null) {
                        record.setCreationTime(new Date());
                        record.setModificationTime(new Date());
                        record
                                .setLocation("http://example.org/other-location/");
                        record.setType("text/plain");
                        record.setOwner("Gordon");
                        session.save(record);
                    }

                    record = new FileRecord("0003");
                    if (session.get(FileRecord.class, record.getId()) == null) {
                        record.setCreationTime(new Date());
                        record.setModificationTime(new Date());
                        record.setLocation("http://example.org/test/");
                        record.setType("image/jpeg");
                        record.setOwner("Bruno");
                        session.save(record);
                    }

                    session.getTransaction().commit();
                } catch (Throwable t) {
                    session.getTransaction().rollback();
                    throw t;
                }
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
        }
    }

    public static void main(String[] args) throws Throwable {
        Component component = new Component();
        component.getServers().add(Protocol.HTTP, 8182);
        component.getClients().add(Protocol.CLAP);

        CoryphaRootApplication cmsRootApplication = new CoryphaRootApplication();
        cmsRootApplication.setContext(component.getContext()
                .createChildContext());
        Context cmsRootAppContext = cmsRootApplication.getContext();

        cmsRootAppContext.getParameters().add(
                CoryphaRootApplication.MODULE_CLASSES_CTX_PARAM,
                TestInitialConfigModule.class.getName());
        cmsRootAppContext.getParameters().add(
                CoryphaRootApplication.MODULE_CLASSES_CTX_PARAM,
                NanoDataModule.class.getName());
        cmsRootAppContext.getParameters().add(
                CoryphaRootApplication.MODULE_CLASSES_CTX_PARAM,
                FileRecordModule.class.getName());
        cmsRootAppContext.getParameters().add(
                CoryphaRootApplication.MODULE_CLASSES_CTX_PARAM,
                JobRecordModule.class.getName());
        cmsRootAppContext.getParameters().add(
                CoryphaRootApplication.MODULE_CLASSES_CTX_PARAM,
                TestDummyRecordsModule.class.getName());

        component.getDefaultHost().attachDefault(cmsRootApplication);

        component.start();
    }
}
