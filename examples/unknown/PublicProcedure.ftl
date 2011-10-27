<#--
  <template>
    <name>Public Procedure Template</name>
    <description>Use this template when you need to create a public facing procedure for an API.</description>
    <promptgroups>
      <promptgroup name="procedure" repeatable="no" maxRepeats="0">
        <prompt>
          <type checkedValue="export" uncheckedValue="">checkbox</type>
          <name>export</name>
          <label>Export?</label>
          <description>Check this box if the procedure should be exported from this module.</description>
          <hint>checked</hint>
        </prompt>
        <prompt>
          <type>text</type>
          <name>nameSpace</name>
          <label>Namespace</label>
          <description>Enter a namespace</description>
          <hint>NameSpace</hint>
        </prompt>
        <prompt>
          <type>text</type>
          <name>name</name>
          <label>Procedure Name</label>
          <description>Enter a procedure name</description>
          <hint>exampleProcedureName</hint>
        </prompt>
        <prompt>
          <type>text</type>
          <name>description</name>
          <label>Procedure Description</label>
          <description>Enter a procedure description</description>
          <hint>Example procedure</hint>
        </prompt>
        <prompt>
          <type>multiline</type>
          <name>documentation</name>
          <label>Procedure Documentation</label>
          <description>Documentation for this procedure to describe what it's function is.</description>
          <hint>This procedure will...</hint>
        </prompt>
        <prompt>
          <type>text</type>
          <name>returnDescription</name>
          <label>Return Description</label>
          <description>Return parameter description</description>
          <hint>This procedure returns...</hint>
        </prompt>
      </promptgroup>
      <promptgroup name="parameter" repeatable="yes" maxRepeats="10">
        <prompt>
          <type>text</type>
          <name>name</name>
          <label>Parameter Name</label>
          <description>Name of the parameter (pr_ will be added automatically)</description>
          <hint>exampleParameterName</hint>
        </prompt>
        <prompt>
          <type>text</type>
          <name>description</name>
          <label>Parameter Description</label>
          <description>Description of the parameter</description>
          <hint>Parameter description...</hint>
        </prompt>
      </promptgroup>
    </promptgroups>
  </template>
-->
     /**
      *--------------------------------------------------------------------
      * @brief ${procedure.description}
      *
      * ${procedure.documentation}
      *
      * @author ${author}
      * @date   ${date}
      *
      <#list parameter.repeats as parm>
      * @param  ${parm.description}
      </#list>  
      *
      * @return ${procedure.returnDescription}
      *--------------------------------------------------------------------
      */
 
      // BEGIN PROCEDURE
     p ${procedure.nameSpace}_${procedure.name}...
     p                 b                   ${procedure.export}
 
      // Procedure Interface
      /////////////////////////////////
     d ${procedure.nameSpace}_${procedure.name}...
     d                 pi              n
     <#list parameter.repeats as parm>
     d  pr_${parm.name}...
     d                 s               n
     </#list>  
 
      // Define Local Vars
      /////////////////////////////////
      // *None
 
      //Main
      /free
      
       //TODO: Perform some business logic here
 
       return true;
      /end-free
 
     p ${procedure.nameSpace}_${procedure.name}...
     p                 e
      // END PROCEDURE