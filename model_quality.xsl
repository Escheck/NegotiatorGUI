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
		
		<H2>Results Listing (in Alternating row colors) </H2>
			<xsl:for-each select="/Tournament/NegotiationOutcome/additional_log">
			<table border="1">
				<xsl:for-each select="../resultsOfAgent">

				<tr>
					<td>Agent <xsl:value-of select="@agent"/></td>
					<td> <xsl:value-of select="@agentClass"/></td>
					<td colspan="4"> <xsl:value-of select="@utilspace"/></td>
				</tr>
				</xsl:for-each>

				<tr>
					<td colspan="3"> Utility Space </td>
					<td colspan="3"> Weights </td>
				</tr>
				<tr>
					<td> Euclidean </td>
					<td> Ranking </td>
					<td> Pearsion </td>
					<td> Euclidean </td>
					<td> Ranking </td>
					<td> Pearsion </td>
				</tr>
				<tr>
					<xsl:choose>
						<xsl:when test="(position()-1) mod 20 &lt; 10">
							<xsl:attribute name="agent">clsOdd</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="class">clsEven</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:choose>
						<xsl:when test="(position()-1) mod 20 &lt; 10">
							<xsl:attribute name="agent">clsEven</xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="class">clsEven</xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:for-each select="learning_performance">
						<tr>
						<td>
							<xsl:value-of select="@euclidean_distance_utility_space"/>
						</td>
						<td>
							<xsl:value-of select="@ranking_distance_utility_space"/>
						</td>
						<td>
							<xsl:value-of select="@pearson_distance_utility_space"/>
						</td>
						<td>
							<xsl:value-of select="@euclidean_distance_weights"/>
						</td>
						<td>
							<xsl:value-of select="@ranking_distance_weights"/>
						</td>

						<td>
							<xsl:value-of select="@pearson_distance_weights"/>
						</td>

						</tr>
					</xsl:for-each>
				</tr>
			</table>
		
			</xsl:for-each>
		<H3>Total Rounds <xsl:value-of select="count(Tournament/NegotiationOutcome)"/>
		</H3>
	</xsl:template>

</xsl:stylesheet>