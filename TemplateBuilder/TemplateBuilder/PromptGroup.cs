﻿using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Xml;

namespace TemplateBuilder
{
    public class PromptGroup : List<Prompt>
    {
        public const bool REPEATABLE = true;
        public const bool NOT_REPEATABLE = false;

        public PromptGroup(string name)
        {
            try
            {
                this.Name = name;
            }
            catch (Exception e)
            {
                throw e;
            }

            Repeatable = false;
            MaxRepeats = 0;
        }

        public PromptGroup(string name, bool repeatable, int maxRepeats)
        {
            try
            {
                this.Name = name;
            }
            catch (Exception e)
            {
                throw e;
            }

            Repeatable = repeatable;
            MaxRepeats = maxRepeats;
        }

        public PromptGroup(string name, string repeatable, string maxRepeats)
        {
            try
            {
                this.Name = name;
            }
            catch (Exception e)
            {
                throw e;
            }

            SetRepeatableFromString(repeatable);
            int.TryParse(maxRepeats, out _MaxRepeats);
        }

        public bool Repeatable { get; set; }

        public void SetRepeatableFromString(string repeatable)
        {

            if ((string.Equals(repeatable, "yes", StringComparison.CurrentCultureIgnoreCase)) ||
                (string.Equals(repeatable, "true", StringComparison.CurrentCultureIgnoreCase)))
            {
                Repeatable = true;
            }
            else
            {
                Repeatable = false;
            }
        }

        private int _MaxRepeats;
        public int MaxRepeats
        {
            set { this._MaxRepeats = value; }
            get { return this._MaxRepeats; }
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
    }
}