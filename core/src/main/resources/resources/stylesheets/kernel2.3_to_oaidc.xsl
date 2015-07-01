<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
    version="2.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xmlns:datacite="http://datacite.org/schema/kernel-2.3"
    xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
    exclude-result-prefixes="datacite">
    
    <xsl:output method="xml" omit-xml-declaration="yes" indent="yes" encoding="UTF-8" />
    
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="datacite:identifier">
        <dc:identifier>
            <xsl:choose>
                <xsl:when test="string-length(@identifierType) &gt; 0">
                    <xsl:value-of select="lower-case(@identifierType)"/>
                    <xsl:text>:</xsl:text>
                </xsl:when>
            </xsl:choose>
            <xsl:value-of select="."/>
        </dc:identifier>
    </xsl:template>
    
    <xsl:template match="datacite:creators">
        <xsl:for-each select="datacite:creator">
            <xsl:element name="dc:creator">
                <xsl:value-of select="./datacite:creatorName"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:titles">
        <xsl:for-each select="datacite:title">
            <xsl:element name="dc:title">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:publisher">
        <xsl:for-each select=".">
            <xsl:element name="dc:publisher">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:publicationYear">
        <xsl:element name="dc:date">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="datacite:subjects">
        <xsl:for-each select="datacite:subject">
            <xsl:element name="dc:subject">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:contributors">
        <xsl:for-each select="datacite:contributor">
            <xsl:element name="dc:contributor">
                <xsl:value-of select="./datacite:contributorName"/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:dates">
        <xsl:variable name="startDate" select="string(date[lower-case(@dateType) = 'startdate'])" />
        <xsl:variable name="endDate" select="string(date[lower-case(@dateType) = 'enddate'])" />
        
        <xsl:if test="$startDate and $endDate">
            <xsl:element name="dc:coverage">
                <xsl:value-of select="translate($startDate,'-','')"/>
                <xsl:text>-</xsl:text>
                <xsl:value-of select="translate($endDate,'-','')"/>
            </xsl:element>
        </xsl:if>
        
        <xsl:for-each select="datacite:date">
            <xsl:choose>
                <!-- dcterms not supported in oai_dc
                    <xsl:when test="lower-case(@dateType) = 'accepted'">
                    <xsl:element name="dcterms:dateAccepted">
                    <xsl:value-of select="."/>
                    </xsl:element>
                    </xsl:when>
                    <xsl:when test="lower-case(@dateType) = 'available'">
                    <xsl:element name="dcterms:available">
                    <xsl:value-of select="."/>
                    </xsl:element>
                    </xsl:when>
                    <xsl:when test="lower-case(@dateType) = 'created'">
                    <xsl:element name="dcterms:created">
                    <xsl:value-of select="."/>
                    </xsl:element>
                    </xsl:when>
                    <xsl:when test="lower-case(@dateType) = 'copyrighted'">
                    <xsl:element name="dcterms:dateCopyrighted">
                    <xsl:value-of select="."/>
                    </xsl:element>
                    </xsl:when>
                    <xsl:when test="lower-case(@dateType) = 'submitted'">
                    <xsl:element name="dcterms:dateSubmitted">
                    <xsl:value-of select="."/>
                    </xsl:element>
                    </xsl:when>
                    <xsl:when test="lower-case(@dateType) = 'updated'">
                    <xsl:element name="dcterms:modified">
                    <xsl:value-of select="."/>
                    </xsl:element>
                    </xsl:when>
                -->
                <xsl:when test="lower-case(@dateType) = 'issued'">
                    <xsl:element name="dc:date">
                        <xsl:value-of select="."/>
                    </xsl:element>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:language">
        <xsl:element name="dc:language">
            <xsl:value-of select="."/>
        </xsl:element>
    </xsl:template>
    
    <xsl:template match="datacite:resourceType">
        <xsl:for-each select=".">
            <xsl:if test="normalize-space(@resourceTypeGeneral)">
                <xsl:element name="dc:type">
                    <xsl:value-of select="@resourceTypeGeneral"/>
                </xsl:element>
            </xsl:if>
            <xsl:if test="normalize-space(.)">
                <xsl:element name="dc:type">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:alternateIdentifiers">
        <xsl:for-each select="datacite:alternateIdentifier">
            <xsl:element name="dc:identifier">
                <xsl:choose>
                    <xsl:when test="string-length(@alternateIdentifierType) &gt; 0">
                        <xsl:value-of select="lower-case(@alternateIdentifierType)"/>
                        <xsl:text>:</xsl:text>
                    </xsl:when>
                </xsl:choose>
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:relatedIdentifiers">
        <xsl:for-each select="datacite:relatedIdentifier">
            <xsl:element name="dc:relation">
                <xsl:choose>
                    <xsl:when test="string-length(@relatedIdentifierType) &gt; 0">
                        <xsl:value-of select="lower-case(@relatedIdentifierType)"/>
                        <xsl:text>:</xsl:text>
                    </xsl:when>
                </xsl:choose>
                <xsl:value-of select="."/>            
            </xsl:element>
        </xsl:for-each>            
    </xsl:template>
    
    <xsl:template match="datacite:sizes">
        <xsl:for-each select="datacite:size">
            <xsl:element name="dc:format">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:formats">
        <xsl:for-each select="datacite:format">
            <xsl:element name="dc:format">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:rights">
        <xsl:for-each select=".">
            <xsl:element name="dc:rights">
                <xsl:value-of select="."/>
            </xsl:element>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:descriptions">
        <xsl:for-each select="datacite:description">
            <xsl:if test="normalize-space(@descriptionType)">
                <xsl:element name="dc:description">
                    <xsl:value-of select="@descriptionType"/>
                </xsl:element>
            </xsl:if>
            <xsl:if test="normalize-space(.)">
                <xsl:element name="dc:description">
                    <xsl:value-of select="."/>
                </xsl:element>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="datacite:resource">
        <xsl:element name="oai_dc:dc">
            <xsl:attribute name="xsi:schemaLocation">http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd</xsl:attribute>        
            <xsl:namespace name="dc">http://purl.org/dc/elements/1.1/</xsl:namespace>
            <xsl:apply-templates select="datacite:titles"/>
            <xsl:apply-templates select="datacite:creators"/>
            <xsl:apply-templates select="datacite:publisher"/>
            <xsl:apply-templates select="datacite:publicationYear"/>
            <xsl:apply-templates select="datacite:identifier"/>
            <xsl:apply-templates select="datacite:alternateIdentifiers"/>
            <xsl:apply-templates select="datacite:relatedIdentifiers"/>
            <xsl:apply-templates select="datacite:subjects"/>
            <xsl:apply-templates select="datacite:descriptions"/>
            <xsl:apply-templates select="datacite:contributors"/>
            <xsl:apply-templates select="datacite:dates"/>
            <xsl:apply-templates select="datacite:language"/>
            <xsl:apply-templates select="datacite:resourceType"/>
            <xsl:apply-templates select="datacite:sizes"/>
            <xsl:apply-templates select="datacite:formats"/>
            <xsl:apply-templates select="datacite:rights"/>
        </xsl:element>
    </xsl:template>    
</xsl:stylesheet>
