<?xml version="1.0"?>

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

    <xsl:output method="xml" indent="yes"/>

    <xsl:template match="/model">
        <xsl:variable name="currentLogin" select="myLogin"/>
        <page>
            <title>JustJournal.com: Comment Changed</title>

            <content>
                <h2>Comment Changed</h2>
            </content>
        </page>
    </xsl:template>

</xsl:stylesheet>