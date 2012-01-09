<#--<?xml version="1.0" encoding="utf-16"?>
<template>
  <name>Revision Comment Block</name>
  <description>When making revisions to an ILE program, a revision block comment should be added to the bottom of the list of prior revision comments with the newest always at the bottom.</description>
  <promptgroups>
    <promptgroup name="RevisionInfo" repeatable="no">
      <prompt>
        <type>text</type>
        <name>ProjectTicketNumber</name>
        <label>Project/Ticket</label>
        <description>Enter the project or ticket number that you are performing the work on.</description>
        <hint>00001</hint>
      </prompt>
      <prompt>
        <type>multiline</type>
        <name>Procedures</name>
        <label>Changed Procedures</label>
        <description>List the procedures here that were changed in this revision. If there are multiples, list list one per line.</description>
        <hint>Namespace_procedureName</hint>
      </prompt>
      <prompt>
        <type>text</type>
        <name>ModTag</name>
        <label>Mod Tag</label>
        <description>Using your initials in upper case combined with the next available tag number in your sequence (for your initials) generate a modification tag that is to mark source code lines that were changed.</description>
        <hint>RN01</hint>
      </prompt>
      <prompt>
        <type>multiline</type>
        <name>RevisionDocumentation</name>
        <label>Documentation</label>
        <description>Describe the revision in detail here. Use basic HTML tags like br and pre to apply formatting to your revision documentation.</description>
        <hint>Document the revision here</hint>
      </prompt>
    </promptgroup>
  </promptgroups>
</template>-->
      * @rev ${date} ${author}
      **********************************************************************
      *
      *      Project/Ticket.....: ${RevisionInfo.ProjectTicketNumber}
      *      Procedure..........: ${RevisionInfo.Procedures}
      *      Mod Tag............: ${RevisionInfo.ModTag}
      *
      *      ${RevisionInfo.RevisionDocumentation}
      *
      **********************************************************************
