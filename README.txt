This is the NanoCMOS data management system.

It uses the Corypha framework (small developed as part of this project),
which is based on Restlet and provides features such as templates and
persistence.


It is intended to be run as a standalone application, as it makes the 
configuration of the GSI (SSL) layer easier, but this can also be done for
a Servlet container.


The test bundled with this source code can be run locally or against
an Oracle database. However, since we can't bundle the Oracle JDBC driver,
it has to be added explicitly.



The purpose of this is to model records to keep track of files (FileRecord)
and jobs submitted to a cluster/grid (JobRecord).
This is built as a web service, with a web-based interface: records can
be created and queried either using a web browser or via dedicated clients
(for example, Python-based, but they could also be implemented otherwise).
This uses content-type negotiation, provided by the Restlet framework, to 
distinguish between a browser-based and an automated client interactions.
(This also uses Restlet's tunnel to correct browser's issues with this,
more particularly when they claim to understand XML better than HTML.)



A good starting point to try this application is:
  uk.ac.manchester.rcs.nanocmosdata.records.RecordApplicationLocalTest
(Although it's in the unit test directory, it has a 'main' method.)

The sample configuration files are in src/test/resources.




