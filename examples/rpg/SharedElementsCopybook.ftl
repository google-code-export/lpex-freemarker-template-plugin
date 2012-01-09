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
    </promptgroup>
  </promptgroups>
</template>-->      /if defined (DontCopy)
     /**********************************************************************
      * @brief ${ModuleInfo.namespace} - Shared Elements Copy Book
      *
      * This copy book contains all shared elements like constants, structs,
      * and procedures both private and public.
      *
      * @author ${author}
      * @date   ${date}
      *
      * @category ${ModuleInfo.namespace}
      **********************************************************************
      */
      /endif


      //////////////////////////////////////////////////////////////////////
      // PUBLIC                                                           //
      //////////////////////////////////////////////////////////////////////

      /if not defined (${ModuleInfo.namespace}_PublicAllDone)

      /if defined (${ModuleInfo.namespace}_PublicAll)
      /define ${ModuleInfo.namespace}_PublicAllDone
      /define ${ModuleInfo.namespace}_PublicConstants
      /define ${ModuleInfo.namespace}_PublicStructs
      /define ${ModuleInfo.namespace}_PublicProcedures
      /endif

      /if defined (${ModuleInfo.namespace}_PublicStructs)
      // Public Structs
      /////////////////////////////////
      // *None
      /endif

      /if defined (${ModuleInfo.namespace}_PublicConstants)
      // Public Constants
      /////////////////////////////////
      // *None
      /endif

      /if defined (${ModuleInfo.namespace}_PublicProcedures)
      // Public Procedures
      /////////////////////////////////
      // *None
      /endif

      /endif



      //////////////////////////////////////////////////////////////////////
      // PRIVATE                                                          //
      //////////////////////////////////////////////////////////////////////

      /if not defined (${ModuleInfo.namespace}_PrivateAllDone)

      // Prevent the private procedures from being included with public
      /if defined (${ModuleInfo.namespace}_PublicAll)
      /eof
      /endif

      /if defined (${ModuleInfo.namespace}_PrivateAll)
      /define ${ModuleInfo.namespace}_PrivateAllDone
      /define ${ModuleInfo.namespace}_PrivateConstants
      /define ${ModuleInfo.namespace}_PrivateStructs
      /define ${ModuleInfo.namespace}_PrivateProcedures
      /endif

      /if defined (${ModuleInfo.namespace}_PrivateStructs)
      // Shared Private Structs
      /////////////////////////////////
      // *None
      /endif

      /if defined (${ModuleInfo.namespace}_PrivateConstants)
      // Shared Private Constants
      /////////////////////////////////
     d UP              c                   'ABCDEFGHIJKLMNOPQRSTUVWXYZ'
     d LO              c                   'abcdefghijklmnopqrstuvwxyz'
     d DIGITS          c                   '1234567890'
     d QT              c                   ''''
     d OK              c                   0
     d EOF             c                   100
     d TRUE            c                   *on
     d FALSE           c                   *off
     d Q               c                   X'7D'
     d PROGRAM_STATUS...
     d               esds                  extname(pgmesdslng) qualified
      /endif


      /if defined (${ModuleInfo.namespace}_PrivateProcedures)
      // Shared Private Procedures
      /////////////////////////////////
      // *None
      /endif

      /endif 
