<?xml version="1.0"?>

<!--
Checkstyle XSL stylesheet for Huitale code.
Hacked and slashed from checkstyle-author.xsl and checkstyle-simple.xsl.
3.12.2008
Ari Tanninen 
-->

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


<xsl:template match="/">
<html>
<head>
<title>Checkstyle Report</title>
<style type="text/css">

    body {
      margin-left: 10;
      margin-right: 10;
      font: normal 68% verdana,arial,helvetica;
      background-color: #FFFFFF;
      color: #000000;
    }

    .a td {
      background: #efefef;
    }

    .b td {
      background: #ffffff;
    }

    th, td {
      text-align: left;
      vertical-align: top;
      padding: 5
    }

    th {
      font-weight: bold;
      background: #cccccc;
      color: black;
    }

    table, th, td {
      font-size: 100%;
      border: none;
    }

    h2 {
      font-weight:bold;
      font-size:140%;
      margin-bottom: 5;
    }

    h3 {
      font-size: 100%;
      font-weight: bold;
      background: #525D76;
      color: white;
      text-decoration: none;
      padding: 5px;
      margin-right: 2px;
      margin-left: 2px;
      margin-bottom: 0;
    }
</style>
</head>
<body>

<h1>Checkstyle Report</h1>

<h2>Summary</h2>
<table class="log">
    <tr class="a">
        <td>Files checked:</td>
        <td><xsl:number level="any" value="count(descendant::file)"/></td>
    </tr>
    <tr class="b">
        <td>Files with violations:</td>
        <td><xsl:number level="any" value="count(descendant::file[error])"/></td>
    </tr>
    <tr class="a">
        <td>Errors:</td>
        <td><xsl:number level="any" value="count(descendant::error[@severity='error'])"/></td>
    </tr>
    <tr class="b">
        <td>Warnings:</td>
        <td><xsl:number level="any" value="count(descendant::error[@severity='warning'])"/></td>
    </tr>
</table>

<h2>Coding Style Violations</h2>
<xsl:apply-templates />

</body>
</html>
</xsl:template>


<xsl:template match="file[error]">
<h3><xsl:value-of select="@name"/></h3>
<table class="log" width="100%">
    <tr>
        <th>Line</th>
        <th>Description</th>
        <xsl:if test="@severity='warning'">
        <th>Warning</th>
        </xsl:if>
        <xsl:if test="@severity='error'">
        <th>Error</th>
        </xsl:if>
        <th>Severity</th>
    </tr>
    <xsl:apply-templates select="error"/>
</table>
<br />
</xsl:template>


<xsl:template match="error">
    <tr>
        <xsl:call-template name="alternated-row"/>
        <td><xsl:value-of select="@line"/></td>
        <td><xsl:value-of select="@message"/></td>
        <td><xsl:value-of select="@severity"/></td>
    </tr>
</xsl:template>


<xsl:template name="alternated-row">
    <xsl:attribute name="class"><xsl:if test="position() mod 2 = 1">a</xsl:if><xsl:if test="position() mod 2 = 0">b</xsl:if></xsl:attribute>
</xsl:template>

</xsl:stylesheet>
