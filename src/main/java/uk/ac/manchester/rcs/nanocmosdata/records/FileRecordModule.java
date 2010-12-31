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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.restlet.Restlet;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;

import uk.ac.manchester.rcs.corypha.core.CoryphaApplication;
import uk.ac.manchester.rcs.corypha.core.CoryphaModule;
import uk.ac.manchester.rcs.corypha.core.CoryphaTemplateUtil;
import uk.ac.manchester.rcs.corypha.core.HibernateFilter;
import uk.ac.manchester.rcs.corypha.core.IApplicationProvider;
import uk.ac.manchester.rcs.corypha.core.IMenuProvider;
import uk.ac.manchester.rcs.corypha.core.SlashRedirectResource;
import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;

/**
 * @author Bruno Harbulot
 * 
 */
public class FileRecordModule extends CoryphaModule implements
        IApplicationProvider, IMenuProvider {

    public static final String ITEMS_PATHELEMENT = "items";
    public static final String SEARCH_PATHELEMENT = "search";
    public static final String METADATA_PATHELEMENT = "metadata";
    public static final String MAIN_ID_ATTRIBUTE = "uk.ac.nanocmos.datamanagement.service.attr.main_id";
    public static final String ANNOTATION_ID_ATTRIBUTE = "uk.ac.nanocmos.datamanagement.service.attr.annotation_id";

    public static class Application1 extends CoryphaApplication {
        @Override
        public String getAutoPrefix() {
            return "filerecord/";
        }

        @Override
        public Restlet createInboundRoot() {
            getTunnelService().setUserAgentTunnel(true);
            getTunnelService().setExtensionsTunnel(true);

            Configuration cfg = CoryphaTemplateUtil
                    .getConfiguration(getContext());
            CoryphaTemplateUtil.addTemplateLoader(cfg, new ClassTemplateLoader(
                    FileRecordModule.class, "templates"));

            Router router = new Router(getContext());
            router.setDefaultMatchingMode(Router.MODE_FIRST_MATCH);

            router.attach(ITEMS_PATHELEMENT, FileRecordListResource.class);
            router.attach(String.format("%s/{%s}", ITEMS_PATHELEMENT,
                    MAIN_ID_ATTRIBUTE), SlashRedirectResource.class);
            router.attach(String.format("%s/{%s}/", ITEMS_PATHELEMENT,
                    MAIN_ID_ATTRIBUTE), FileRecordResource.class);

            router.attach(SEARCH_PATHELEMENT, SlashRedirectResource.class);
            router.attach(String.format("%s/", SEARCH_PATHELEMENT),
                    FileRecordSearchResource.class);
            router.attach(String.format("%s/{%s}", SEARCH_PATHELEMENT,
                    MAIN_ID_ATTRIBUTE), FileRecordSearchResource.class);

            router.attach(String.format("%s/{%s}/%s/{%s}", ITEMS_PATHELEMENT,
                    MAIN_ID_ATTRIBUTE, METADATA_PATHELEMENT,
                    ANNOTATION_ID_ATTRIBUTE),
                    FileRecordAnnotationResource.class);
            router.attachDefault(FileRecordRootResource.class);
            router.setDefaultMatchingQuery(false);

            HibernateFilter hibernateFilter = new HibernateFilter(getContext(),
                    router);
            return hibernateFilter;
        }

        @Override
        public CoryphaApplication getApplication() {
            return this;
        }

        @Override
        public Restlet getHtdocsRestlet() {
            return new Directory(getContext(),
                    "clap://thread/uk/ac/manchester/rcs/nanocmosdata/records/htdocs/");
        }
    }

    private final Application1 application1 = new Application1();
    private final List<MenuItem> menuItems = Collections
            .unmodifiableList(Arrays.asList(new MenuItem[] { new MenuItem(
                    "File Records", "/filerecord/") }));

    @Override
    public CoryphaApplication getApplication() {
        return application1;
    }

    @Override
    public List<MenuItem> getMenuItems() {
        return menuItems;
    }
}
