using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace TemplateBuilder
{
    public static class TreeViewClickHandler
    {
        public static PropertyGrid editor_pg = null;

        public static void PromptGroup_Click(object data)
        {
            PromptGroup castedObject = data as PromptGroup;
            editor_pg.SelectedObject = castedObject;
        }

        public static void Prompt_Click(object data)
        {
            Prompt castedObject = data as Prompt;
            editor_pg.SelectedObject = castedObject;
        }
    }
}
