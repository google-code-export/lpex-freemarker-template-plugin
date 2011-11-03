using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace TemplateBuilder
{
    public class Prompt
    {
        public enum PromptType
        {
            TEXT,
            MULTILINE,
            DATE,
            CHECKBOX
        }

        public PromptGroup Parent { get; set; }
        public PromptType Type { get; set; }
        public int OrderKey { get; set; }
        public string Label { get; set; }
        public string Description { get; set; }
        public string Hint { get; set; }
        public string CheckedValue { get; set; }
        public string UncheckedValue { get; set; }
        public string DateFormat { get; set; }
        public string DefaultValue { get; set; }

        public Prompt()
        {
            this.Name = "PromptName";
            this.Type = PromptType.TEXT;
            this.Label = "Label here";
            this.DefaultValue = "Default here";
            this.Parent = null;
            this.OrderKey = 0;
        }

        public Prompt(string name, PromptType type, string label, PromptGroup parent)
        {
            this.Name = name;
            this.Type = type;
            this.Parent = parent;
            this.OrderKey = Parent.Count;

            this.Description = "Description here";
            this.Hint = "Hint here";
            this.Label = "Label here";
            this.DefaultValue = "Default here";
        }

        public void SetTypeFromString(string type)
        {
            if (type == "text") Type = PromptType.TEXT;
            else if (type == "multiline") Type = PromptType.MULTILINE;
            else if (type == "checkbox") Type = PromptType.CHECKBOX;
            else if (type == "date") Type = PromptType.DATE;
            else Type = PromptType.TEXT;
        }

        private void callRenameHandler(string newName)
        {
            try
            {
                if (Parent.Parent.promptRenameHandler != null)
                    Parent.Parent.promptRenameHandler(this, newName);
            }
            catch { }
        }

        private string _Name;
        public string Name
        {
            set
            {
                if (value.Contains(' '))
                {
                    throw (new ArgumentException("Name cannot contain spaces."));
                }
                else
                {
                    callRenameHandler(value);
                    this._Name = value;
                }
            }
            get { return this._Name; }
        }

        public string VariableName
        {
            get
            {
                return GetVariableName(this.Parent, this.Name);
            }
        }

        public static string GetVariableName(PromptGroup promptGroup, string promtName)
        {
            string variable = "";
            if (promptGroup.Repeatable)
            {
                variable = "\r\n<#list " + promptGroup.Name + ".repeats as var>\r\n";
                variable += "${var." + promtName + "}\r\n";
                variable += "</#list>";
            }
            else
            {
                variable = "${" + promptGroup.Name + "." + promtName + "}";
            }
            return variable;
        }

        private void callDeleteHandler()
        {
            if (Parent.Parent.beforePromptDeleteHandler != null)
                Parent.Parent.beforePromptDeleteHandler(this);
        }

        public void Remove()
        {
            callDeleteHandler();
            this.Parent.Remove(this);
        }
    }
}
