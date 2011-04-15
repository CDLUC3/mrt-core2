<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <!-- specific xml to actionable html -->
    
    <xsl:template match="/">
        <xsl:variable name="header">
            <xsl:for-each select="/*[1]">
                <xsl:value-of select="local-name()"/>
            </xsl:for-each>
        </xsl:variable>
        <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
            
            <head><title><xsl:value-of select="$header"/></title></head>
            <body>
                <h1><xsl:value-of select="$header"/></h1>
                <table border="1">
                    <tr>
                        <th>Name</th>
                        <th>Value</th>
                    </tr>
                    <xsl:for-each select="//*">
                        <xsl:if test="not(*)">
                        
                        <tr>
                            <xsl:variable name="value" select="current()"/>
                            <td><xsl:value-of select="local-name()"/></td>
                            <xsl:choose>
                            <xsl:when test="contains($value, 'http:')">
                                <td>
                                    <xsl:element name="a">
                                        <xsl:attribute name="href">
                                            <xsl:value-of select="$value"/>
                                        </xsl:attribute>
                                        <xsl:value-of select="$value"/>
                                    </xsl:element>
                                </td>
                            </xsl:when>
                            <xsl:otherwise>
                                <td><xsl:value-of select="$value"/></td>
                            </xsl:otherwise>
                            </xsl:choose>
                        </tr>
                        </xsl:if>
                    </xsl:for-each>
                </table>
            </body></html>
    </xsl:template>
</xsl:stylesheet>
