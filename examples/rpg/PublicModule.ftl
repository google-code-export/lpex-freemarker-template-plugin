<#--<?xml version="1.0" encoding="utf-16"?>
<template>
  <name>Public Module Template</name>
  <description>Use this template when you need to create a public facing procedure for an API.</description>
  <promptgroups>
    <promptgroup name="ModuleInfo" repeatable="no">
      <prompt>
        <type>text</type>
        <name>namespace</name>
        <label>Namespace</label>
        <description>Enter a namespace for this module</description>
        <hint>exampleProcedureName</hint>
      </prompt>
      <prompt>
        <type>text</type>
        <name>brief</name>
        <label>Brief Description</label>
        <description>Enter a breif description (this typically matches the description on the object)</description>
        <hint>Example Module Brief</hint>
      </prompt>
      <prompt>
        <type>multiline</type>
        <name>documentation</name>
        <label>Module Documentation</label>
        <description>Documentation for this module to describe what it's purpose is.</description>
        <hint>This module will...</hint>
      </prompt>
      <prompt>
        <type>text</type>
        <name>sharedElementsCopybook</name>
        <label>Shared Elements Copybook</label>
        <description>Shared elements copy book name</description>
        <hint>AAA10Y001</hint>
      </prompt>
    </promptgroup>
  </promptgroups>
</template>-->     /**********************************************************************
      * @brief ${ModuleInfo.namespace} - ${ModuleInfo.brief}
      *
      * ${ModuleInfo.documentation}
      *
      * @author ${author}
      * @date   ${date}
      *
      * @category ${ModuleInfo.namespace}
      **********************************************************************
      */

     h nomain
     h option(*NoDebugIO : *SrcStmt : *ShowCpy : *xRef)
     h copyright('Copyright (c) 2012 Estes Express. All rights reserved.')


      // Includes
      /////////////////////////////////
      /define ${ModuleInfo.namespace}_PrivateAll
      /define ${ModuleInfo.namespace}_PublicConstants
      /define ${ModuleInfo.namespace}_PublicStructs
      /define ${ModuleInfo.namespace}_PublicProcedures 
      /copy *libl/qrpglesrc,${ModuleInfo.sharedElementsCopybook}


      // Private Procedures
      /////////////////////////////////
      // *None


      // Define Global Variables
      /////////////////////////////////
      // *None



      //////////////////////////////////////////////////////////////////////
      // PUBLIC PROCEDURES                                                //
      //////////////////////////////////////////////////////////////////////
      
      //REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE 
      //
      //  Define public procedures here with 3 lines between each one
      //
      //REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE 








      //////////////////////////////////////////////////////////////////////
      // PRIVATE PROCEDURES                                               //
      //////////////////////////////////////////////////////////////////////
      
      //REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE 
      //
      //  Define private procedures here with 3 lines between each one
      //
      //REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE REMOVE 

