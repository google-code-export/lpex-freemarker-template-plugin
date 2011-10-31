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
        public string Label { get; set; }
        public string Description { get; set; }
        public string Hint { get; set; }
        public string CheckedValue { get; set; }
        public string UncheckedValue { get; set; }
        public string DateFormat { get; set; }

        public Prompt() {}

        public Prompt(string name, PromptType type, string label, PromptGroup parent)
        {
            this.Name = name;
            this.Type = type;
            this.Label = label;
            this.Parent = parent;

            this.Description = "";
            this.Hint = "";
        }

        public void SetTypeFromString(string type)
        {
            if (type == "text") Type = PromptType.TEXT;
            else if (type == "multiline") Type = PromptType.MULTILINE;
            else if (type == "checkbox") Type = PromptType.CHECKBOX;
            else if (type == "date") Type = PromptType.DATE;
            else Type = PromptType.TEXT;
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
                    this._Name = value;
                }
            }
            get { return this._Name; }
        }

        public string VariableName
        {
            get
            {
                return "${" + this.Parent.Name + "." + this._Name + "}";
            }
        }
    }
}
