<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes"
		doctype-system="http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd"
		doctype-public="-//Hibernate/Hibernate Mapping DTD 3.0//EN" />
	<xsl:template match="subselect" />
	<xsl:template match="@sql-type[.='XmlType']" />
	<xsl:template match="node()|@*">
		<xsl:copy>
			<xsl:apply-templates select="node()|@*" />
		</xsl:copy>
	</xsl:template>
</xsl:stylesheet>