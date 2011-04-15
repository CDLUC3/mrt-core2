<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    
    <!-- specific xml to actionable html -->
    
    <xsl:template match="/">
        <xsl:variable name="header">
            <xsl:for-each select="/*[1]">
                <xsl:value-of select="local-name()"/>
            </xsl:for-each>
        </xsl:variable>
        <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
        <xsl:choose>
            <xsl:when test="contains($header, 'tException')">
           
                <xsl:variable name="httpResponse">
                    <xsl:call-template name="MATCH">
                        <xsl:with-param 
                            name="match">hTTPResponse</xsl:with-param>
                    </xsl:call-template>
                </xsl:variable>     
                <xsl:variable name="detail">
                    <xsl:call-template name="MATCH">
                        <xsl:with-param 
                            name="match">detail</xsl:with-param>
                    </xsl:call-template>
                </xsl:variable>     
                <xsl:variable name="location">
                    <xsl:call-template name="MATCH">
                        <xsl:with-param 
                            name="match">location</xsl:with-param>
                    </xsl:call-template>
                </xsl:variable>     
                <xsl:variable name="description">
                    <xsl:call-template name="MATCH">
                        <xsl:with-param 
                            name="match">description</xsl:with-param>
                    </xsl:call-template>
                </xsl:variable>
                <head><title>HTTP Exception <xsl:value-of select="$httpResponse"/></title></head>
                <body>
                    <h1>HTTP Exception <xsl:value-of select="$httpResponse"/></h1>
                    <p><b>Error: </b> <xsl:value-of select="$detail"/></p>
                    <p><b>Description: </b><xsl:value-of select="$description"/></p>
                    <p><b>Location: </b><xsl:value-of select="$location"/></p>
                </body>
            </xsl:when>
            <xsl:otherwise>
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
                                    <xsl:choose>
                                        <xsl:when test="contains($value, '/state')">
                                            <xsl:variable name="value2">
                                                <xsl:value-of select="concat($value,'?t=xhtml')"/>
                                            </xsl:variable>
                                            <xsl:element name="a">
                                                <xsl:attribute name="href">
                                                    <xsl:value-of select="$value2"/>
                                                </xsl:attribute>
                                                <xsl:value-of select="$value"/>
                                            </xsl:element>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:element name="a">
                                                <xsl:attribute name="href">
                                                    <xsl:value-of select="$value"/>
                                                </xsl:attribute>
                                                <xsl:value-of select="$value"/>
                                            </xsl:element>
                                        </xsl:otherwise>
                                    </xsl:choose>
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
            </body>
            </xsl:otherwise>
            </xsl:choose>
        </html>
    </xsl:template>
    
    <xsl:template name="MATCH">
        <xsl:param name="match">none</xsl:param>
        <xsl:for-each select="//*">
            <xsl:if test="local-name() = $match">
                <xsl:value-of select="."/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
</xsl:stylesheet>
