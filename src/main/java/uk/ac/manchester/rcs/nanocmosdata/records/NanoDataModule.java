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
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.hibernate.HibernateException;
import org.hibernate.MappingException;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.dialect.Dialect;
import org.hibernate.dialect.Oracle10gDialect;
import org.hibernate.util.DTDEntityResolver;
import org.xml.sax.SAXException;

import uk.ac.manchester.rcs.corypha.core.CoryphaModule;
import uk.ac.manchester.rcs.corypha.core.IHibernateConfigurationContributor;

/**
 * @author Bruno Harbulot
 * 
 */
public class NanoDataModule extends CoryphaModule implements
        IHibernateConfigurationContributor {
    @Override
    public void configureHibernate(AnnotationConfiguration configuration) {
        try {
            Dialect dialect = Dialect.getDialect(configuration.getProperties());
            InputStream hbmStream = ClassLoader
                    .getSystemResourceAsStream("NanoCmosDatamanagement.hbm.xml");
            if (!(dialect instanceof Oracle10gDialect)) {
                TransformerFactory tFactory = TransformerFactory.newInstance();
                InputStream xslInputStream = ClassLoader
                        .getSystemResourceAsStream("hbm2nooracle.xsl");
                Source xslSource = new StreamSource(xslInputStream);
                Transformer t = tFactory.newTransformer(xslSource);

                DocumentBuilderFactory dbf = DocumentBuilderFactory
                        .newInstance();
                dbf.setValidating(false);
                dbf.setNamespaceAware(true);
                DocumentBuilder db = dbf.newDocumentBuilder();
                db.setEntityResolver(new DTDEntityResolver());
                Source xmlSource = new DOMSource(db.parse(hbmStream));

                StringWriter sw = new StringWriter();
                StreamResult streamResult = new StreamResult(sw);
                t.transform(xmlSource, streamResult);

                hbmStream.close();
                xslInputStream.close();

                configuration.addXML(sw.toString());
            } else {
                configuration.addInputStream(hbmStream);
                hbmStream.close();
            }
        } catch (MappingException e) {
            throw new RuntimeException(e);
        } catch (HibernateException e) {
            throw new RuntimeException(e);
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new RuntimeException(e);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }
}
