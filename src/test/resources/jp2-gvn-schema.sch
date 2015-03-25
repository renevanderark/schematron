<?xml version="1.0"?>
<!--
   Schematron jpylyzer schema: verify if JP2 conforms to 
   KB's  profile for access copies (A.K.A. KB_ACCESS_LOSSY_01/07/2014)
   Simplified version for Geheugen Van Nederland migration, omits specific requirements for
   resolution, colour space,compression ratio, XML box and codestream comment.
   Johan van der Knijff, KB / National Library of the Netherlands , 18 March 2014.
   
-->
<s:schema xmlns:s="http://purl.oclc.org/dsdl/schematron">
  <s:pattern>
    <s:title>KB Geheugen van Nederland access JP2 check</s:title>
    <!-- check that the jpylyzer element exists -->
    <s:rule context="/">
      <s:assert test="jpylyzer">no jpylyzer element found</s:assert>
    </s:rule>
    <!-- check that isValidJP2 element exists with the text 'True' -->
    <s:rule context="/jpylyzer">
      <s:assert test="isValidJP2 = 'True'">no valid JP2</s:assert>
    </s:rule>
    <!-- check X- and Y- tile sizes -->
    <s:rule context="/jpylyzer/properties/contiguousCodestreamBox/siz">
      <s:assert test="xTsiz = '1024'">wrong X Tile size</s:assert>
      <s:assert test="yTsiz = '1024'">wrong Y Tile size</s:assert>
    </s:rule>
    <!-- checks on codestream COD parameters -->
    <s:rule context="/jpylyzer/properties/contiguousCodestreamBox/cod">
      <!-- Error resilience features: sop, eph and segmentation symbols -->
      <s:assert test="sop = 'yes'">no start-of-packet headers</s:assert>
      <s:assert test="eph = 'yes'">no end-of-packet headers</s:assert>
      <s:assert test="segmentationSymbols = 'yes'">no segmentation symbols</s:assert>
      <!-- Progression order -->
      <s:assert test="order = 'RPCL'">wrong progression order</s:assert>
      <!-- Layers -->
      <s:assert test="layers = '8'">wrong number of layers</s:assert>
      <!-- Colour transformation (only for RGB images, i.e. number of components = 3)-->
      <s:assert test="(multipleComponentTransformation = 'yes') and (../../jp2HeaderBox/imageHeaderBox/nC = '3') or (multipleComponentTransformation = 'no') and (../../jp2HeaderBox/imageHeaderBox/nC = '1')">
                     no colour transformation</s:assert>
      <!-- Decomposition levels -->
      <s:assert test="levels = '5'">wrong number of decomposition levels</s:assert>
      <!-- Codeblock size -->
      <s:assert test="codeBlockWidth = '64'">wrong codeblock width</s:assert>
      <s:assert test="codeBlockHeight = '64'">wrong codeblock height</s:assert>
      <!-- Transformation (lossy vs lossless) -->
      <s:assert test="transformation = '9-7 irreversible'">wrong transformation</s:assert>
    </s:rule>

     </s:pattern>
</s:schema>
