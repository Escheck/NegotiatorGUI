<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="html"/>
	<xsl:template match="/">
		<STYLE>
		H1: {COLOR: blue FONT-FAMILY: Arial; }
		SubTotal {COLOR: green;  FONT-FAMILY: Arial}
		BODY {COLOR: blue; FONT-FAMILY: Arial; FONT-SIZE: 8pt;}
		TR.clsOdd { background-Color: beige;  }
		TR.clsEven { background-color: #cccccc; }
		</STYLE>
		
		<H2>Customer Listing (in Alternating row colors) </H2>
		<table border="1">
			<xsl:for-each select="/Tournament/NegotiationOutcome/resultsOfAgent">
				<tr>
					<xsl:choose>
						<xsl:when test="position() mod 2 = 1">
							<xsl:attribute name="agent">clsOdd</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="class">clsEven</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:for-each select="@*">
						<td>
							<xsl:value-of select="."/>
						</td>
					</xsl:for-each>
				</tr>
			</xsl:for-each>
		</table>
		<H3>Total Customers <xsl:value-of select="count(Tournament/NegotiationOutcome)"/>
		</H3>
	</xsl:template>

</xsl:stylesheet>