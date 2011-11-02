using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using TemplateBuilder.Properties;
using System.Collections;
using System.Collections.Specialized;
using System.Text.RegularExpressions;
using System.Globalization;
using ICSharpCode.TextEditor.Document;
using ICSharpCode.TextEditor;

namespace TemplateBuilder
{
    public delegate void NodeClicker(object obj);

    public partial class Form1 : Form
    {
        public string noDocStatusMessage = "Please open a template to work with.";
        public string openedDocDefaultMessage = "Customize your template then click save when done.";
        public string formTitleDefault = "LPEX Template Builder";

        public static Template template = new Template();
        public static DocumentManager doc = new DocumentManager("ftl", "FreeMarkerTemplate");

        public bool _highlightingProviderLoaded = false;

        public Form1()
        {
            InitializeComponent();
            this.Text = formTitleDefault;

            //Visual Studio breaks form rendering if these are in the designer file!
            this.templateEditor.ActiveTextAreaControl.TextArea.DragDrop += new System.Windows.Forms.DragEventHandler(this.templateEditor_DragDrop);
            this.templateEditor.ActiveTextAreaControl.TextArea.DragOver += new System.Windows.Forms.DragEventHandler(this.templateEditor_DragOver);
            this.templateEditor.ActiveTextAreaControl.TextArea.KeyUp += new System.Windows.Forms.KeyEventHandler(this.templateEditor_KeyUp);

            //Document change handlers
            doc.newFileHandler = handleNewFileCreation;
            doc.recentFilesChangedHandler = updateRecentFiles;
            doc.currentFileChanged = updateCurrentFile;
            doc.updateStatus = updateStatus;
            doc.openedFileHandler = handleFileOpen;
            doc.beforeFileSavedHandler = handleBeforeFileSave;
            doc.afterFileSavedHandler = handleAfterFileSave;
            doc.fileChangedHandler = handleFileChanged;

            //Prompt change handlers
            template.promptRenameHandler = handlePromptRename;
            template.beforePromptDeleteHandler = handleBeforePromptDelete;

            //Get the recent files from the app settings file
            doc.RecentFiles = TemplateBuilder.Properties.Settings.Default.recentDocuments;

            foreach (string path in doc.RecentFiles)
            {
                doc.Open(path);
                break;
            }

            if (template.promptRenameHandler == null)
                MessageBox.Show("Rename handler erased.");

            //doc.Open(Path.GetDirectoryName(Application.ExecutablePath) + @"\PublicProcedure.ftl");
            //doc.New();

            TreeViewClickHandler.editor_pg = this.editor_pg;

            if (!_highlightingProviderLoaded)
            {
                // Attach to the text editor.
                HighlightingManager.Manager.AddSyntaxModeFileProvider(new AppSyntaxModeProvider());
                _highlightingProviderLoaded = true;
            }

            templateEditor.SetHighlighting("FTL");

            BuildRecentFilesMenu();
        }

        private void handleNewFileCreation()
        {
            template = new Template();
            template.promptRenameHandler = handlePromptRename;
            template.beforePromptDeleteHandler = handleBeforePromptDelete;
            refreshTreeView();
            editor_pg.SelectedObject = null;
            templateEditor.Document.TextContent = "";
            templateEditor.ActiveTextAreaControl.TextArea.Refresh();
        }

        private void handleBeforeFileSave()
        {
            doc.Data = template.Output;
            refreshAllFromOpenDocument();
        }

        private void handleAfterFileSave()
        {
            updateFormTitle(doc.FileName);
        }

        private void updateRecentFiles(StringCollection recentFiles)
        {
            TemplateBuilder.Properties.Settings.Default.recentDocuments = recentFiles;
            TemplateBuilder.Properties.Settings.Default.Save();
            BuildRecentFilesMenu();
        }

        private void updateCurrentFile(string filePath, string fileName)
        {
            updateFormTitle(fileName);
        }

        private void updateStatus(string status)
        {
            statusBar_status.Text = status;
        }

        private void handleFileOpen()
        {
            refreshAllFromOpenDocument();
        }

        private void handleFileChanged()
        {
            updateFormTitle(doc.FileName);
        }

        private void handlePromptRename(Prompt prompt, string newName)
        {
            string oldVariableName = prompt.VariableName;
            string newVariableName = Prompt.GetVariableName(prompt.Parent.Name, newName);
            template.TemplateText = template.TemplateText.Replace(oldVariableName, newVariableName);
            refreshTemplateText();
        }

        private void handleBeforePromptDelete(Prompt prompt)
        {
            string oldVariableName = prompt.VariableName;
            template.TemplateText = template.TemplateText.Replace(oldVariableName, "");
            refreshTemplateText();
        }

        private void refreshAllFromOpenDocument()
        {
            template = new Template();
            template.promptRenameHandler = handlePromptRename;
            template.beforePromptDeleteHandler = handleBeforePromptDelete;
            template.RawText = doc.Data;
            updateFormTitle(doc.FileName);
            refreshTreeView();
            refreshTemplateText();
        }

        private Hashtable BuildObjectIDPack(object data, string type, object clickHandler)
        {
            Hashtable map = new Hashtable();
            map.Add("Data", data);
            map.Add("Type", type);
            map.Add("ClickHandler", clickHandler);
            return map;
        }

        private void refreshTemplateText()
        {
            this.templateEditor.Text = template.TemplateText; 
        }

        #region Misc

        private void updateProgress(int percentage, string action)
        {
            statusBar_progress.Visible = (percentage > 0);
            statusBar_progress.ProgressBar.Value = percentage;
            statusStrip1.Update();
            if (action != string.Empty) { log(action); }
            logText.Update();
        }

        private void log(string msg)
        {
            //Write a message out to the log window
            //this.logText.Text = DateTime.Now.ToString("yyyy.MM.dd hh:mm:ss") + "   " + msg + "\r\n" + this.logText.Text;
            this.logText.Text += DateTime.Now.ToString("yyyy.MM.dd hh:mm:ss") + "   " + msg + "\r\n";
            this.logText.Select(this.logText.Text.Length + 1, 2);
            this.logText.ScrollToCaret();
            this.logText.Update();
        }

        private void updateFormTitle(string fileName)
        {
            string changeIndicator = "";
            if (doc.Changed)
                changeIndicator = "*";
            this.Text = formTitleDefault + " - " + fileName + changeIndicator;
        }

        #endregion

        #region Handle recent files management

        private void recentFiles_Click(object sender, EventArgs e)
        {
            ToolStripMenuItem recentFileMenuItem = (ToolStripMenuItem)sender;
            doc.Open((string)recentFileMenuItem.Tag);
        }

        private void BuildRecentFilesMenu()
        {
            recentFilesToolStripMenuItem.Enabled = false;
            recentFilesToolStripMenuItem.DropDownItems.Clear();
            foreach (string path in doc.RecentFiles)
            {
                recentFilesToolStripMenuItem.Enabled = true;
                ToolStripMenuItem item = new ToolStripMenuItem(PathShortener(path));
                item.Tag = path;
                item.Click += new System.EventHandler(this.recentFiles_Click);
                recentFilesToolStripMenuItem.DropDownItems.AddRange(new System.Windows.Forms.ToolStripItem[] { item });
            }
        }

        #endregion

        #region Treeview builders and helpers

        public static string PathShortener(string path)
        {
            const string pattern = @"^(\w+:|\\)(\\[^\\]+\\[^\\]+\\).*(\\[^\\]+\\[^\\]+)$";
            const string replacement = "$1$2...$3";
            if (Regex.IsMatch(path, pattern))
            {
                return Regex.Replace(path, pattern, replacement);
            }
            else
            {
                return path;
            }
        }

        public static string TitleCase(string TextToFormat)
        {
            return new CultureInfo("en").TextInfo.ToTitleCase(TextToFormat.ToLower());
        }

        private void refreshTreeView()
        {
            foreach(PromptGroup pg in template)
            {
                template.Sort(delegate(PromptGroup p1, PromptGroup p2) { return p1.OrderKey.CompareTo(p2.OrderKey); });
                foreach (Prompt p in pg)
                {
                    pg.Sort(delegate(Prompt p1, Prompt p2) { return p1.OrderKey.CompareTo(p2.OrderKey); });
                }
            }
            documentTree.BeginUpdate();
            documentTree.Nodes.Clear();
            int topNode = 0;
            try
            {
                topNode = documentTree.TopNode.Index;
            }
            catch { }

            TreeNode templateNode = new TreeNode(template.Name, Icons.SCRIPT, Icons.SCRIPT);
            templateNode.Tag = BuildObjectIDPack(template, "template", new NodeClicker(TreeViewClickHandler.Template_Click));
            templateNode.Name = template.Name;

            int icon = 0;
            foreach (PromptGroup promptGroup in template)
            {
                icon = Icons.FORM;
                if (promptGroup.Repeatable)
                        icon = Icons.APPLICATION_CASCADE;
                TreeNode promptGroupNode = new TreeNode(promptGroup.Name, icon, icon);
                promptGroupNode.Tag = BuildObjectIDPack(promptGroup, "promptgroup", new NodeClicker(TreeViewClickHandler.PromptGroup_Click));
                promptGroupNode.Name = promptGroup.Name;

                //Add each prompt
                foreach (Prompt prompt in promptGroup)
                {
                    icon = 0;
                    switch (prompt.Type)
	                {
                        case Prompt.PromptType.TEXT:
                            icon = Icons.TEXT_FIELD;
                            break;
                        case Prompt.PromptType.MULTILINE:
                            //icon = Icons.TEXT_ALIGN_LEFT;
                            icon = Icons.TEXT_FIELD_ADD;
                            break;
                        case Prompt.PromptType.DATE:
                            icon = Icons.DATE;
                            break;
                        case Prompt.PromptType.CHECKBOX:
                            icon = Icons.CHECKBOX;
                            break;
                        default:
                            icon = Icons.TEXT_FIELD;
                            break;
	                }
                    TreeNode promptNode = new TreeNode(prompt.Name, icon, icon);
                    promptNode.Tag = BuildObjectIDPack(prompt, "prompt", new NodeClicker(TreeViewClickHandler.Prompt_Click));
                    promptNode.Name = prompt.Name;
                    promptGroupNode.Nodes.Add(promptNode);
                }

                templateNode.Nodes.Add(promptGroupNode);
            }
            documentTree.Nodes.Add(templateNode);

            documentTree.ExpandAll();
            try
            {
                documentTree.TopNode = documentTree.Nodes[topNode];
            }
            catch { }
            documentTree.EndUpdate();

            doc.Data = template.Output;
        }

        public void reportProgress(int progress, string action)
        {
            statusBar_progress.Visible = (progress > 0);
            statusBar_progress.ProgressBar.Value = progress;
            statusStrip1.Update();
            if (action != string.Empty) { log(action); }
            logText.Update();
        }

        private void PopulateTreeView(Template template, TreeView tree)
        {
            reportProgress(0, "Populating tree view...");
            tree.Nodes.Clear();
            reportProgress(0, "  Loading prompt groups...");
            LoadPromptGroups(template, tree);
            reportProgress(0, "");
        }

        private void LoadPromptGroups(Template template, TreeView tree)
        {
            //
        }

        #endregion

        #region Form element event handlers
        private void MainForm_FormClosing(object sender, FormClosingEventArgs e)
        {
            if (!doc.HandleUnsavedContent())
            {
                e.Cancel = true;
            }
        }

        private void openFile_btn_Click(object sender, EventArgs e)
        {
            doc.Open();
        }

        private void openToolStripMenuItem_Click(object sender, EventArgs e)
        {
            doc.Open();
        }

        private void saveFile_btn_Click(object sender, EventArgs e)
        {
            doc.Save();
        }

        private void saveToolStripMenuItem_Click(object sender, EventArgs e)
        {
            doc.Save();
        }

        private void saveAsToolStripMenuItem_Click(object sender, EventArgs e)
        {
            doc.SaveAs();
            refreshAllFromOpenDocument();
        }

        private void newFile_btn_Click(object sender, EventArgs e)
        {
            doc.New();
        }

        private void newToolStripMenuItem_Click(object sender, EventArgs e)
        {
            doc.New();
        }

        private void addPromptGroup_btn_Click(object sender, EventArgs e)
        {
            template.Add(new PromptGroup("NewPromptGroup", template));
            refreshTreeView();
        }

        private void toolStripButton1_Click(object sender, EventArgs e)
        {
            refreshTreeView();
        }

        private TreeNode m_OldSelectNode;
        private void documentTree_MouseUp(object sender, System.Windows.Forms.MouseEventArgs e)
        {
            // select the node the mouse is over through code. The select event is not fired if its the same
            // node. Only act if it's the RMB.
            if (e.Button != MouseButtons.Right)
            {
                return;
            }

            Point p = new Point(e.X, e.Y);

            // Store the selected node (can deselect a node).
            documentTree.SelectedNode = documentTree.GetNodeAt(e.X, e.Y);
            TreeNode node = documentTree.SelectedNode;

            // Show menu only if the right mouse button is clicked.
            if (e.Button == MouseButtons.Right)
            {
                if (node != null)
                {
                    // Select the node the user has clicked.
                    // The node appears selected until the menu is displayed on the screen.
                    m_OldSelectNode = documentTree.SelectedNode;
                    documentTree.SelectedNode = node;

                    // Find the appropriate ContextMenu depending on the selected node.
                    Hashtable idPack = node.Tag as Hashtable;
                    switch (idPack["Type"] as string)
                    {
                        case "promptgroup":
                            menuPromptGroup.Show(documentTree, p);
                            break;
                        case "prompt":
                            menuPrompt.Show(documentTree, p);
                            break;
                    }

                    // Highlight the selected node.
                    documentTree.SelectedNode = m_OldSelectNode;
                    m_OldSelectNode = null;
                }
            }
        }

        private void documentTree_AfterSelect(object sender, TreeViewEventArgs e)
        {
            try
            {
                Hashtable idPack = e.Node.Tag as Hashtable;
                NodeClicker clicker = idPack["ClickHandler"] as NodeClicker;
                editor_pg.SelectedObject = idPack["Data"];
                clicker(idPack["Data"]);
            }
            catch { }
        }

        private void documentTree_MouseDown(object sender, MouseEventArgs e)
        {
            if (e.Button == MouseButtons.Right)
            {
                return;
            }

            // Get the tree.
            TreeView tree = (TreeView)sender;

            // Get the node underneath the mouse.
            TreeNode node = tree.GetNodeAt(e.X, e.Y);
            tree.SelectedNode = node;

            // Start the drag-and-drop operation with a cloned copy of the node.
            if (node != null)
            {
                //Enable drag from the tree view
                tree.DoDragDrop(node.Clone(), DragDropEffects.Copy);
            }
        }

        private void editor_pg_PropertyValueChanged(object s, PropertyValueChangedEventArgs e)
        {
            refreshTreeView();
        }

        private void templateEditor_DragDrop(object sender, DragEventArgs e)
        {
            TreeNode node = (TreeNode)e.Data.GetData(typeof(TreeNode));
            ICSharpCode.TextEditor.TextArea textArea = (ICSharpCode.TextEditor.TextArea)sender;

            Hashtable idPack = node.Tag as Hashtable;
            switch (idPack["Type"] as string)
            {
                case "promptgroup":
                    break;
                case "prompt":
                    break;
            }

            //Build the variable to insert here
            Prompt prompt = idPack["Data"] as Prompt;

            Point p = textArea.PointToClient(new Point(e.X, e.Y));
            textArea.BeginUpdate();
            textArea.Document.UndoStack.StartUndoGroup();
            try
            {
                int offset = textArea.Caret.Offset;
                if (e.Data.GetDataPresent(typeof(DefaultSelection)))
                {
                    ISelection sel = (ISelection)e.Data.GetData(typeof(DefaultSelection));
                    if (sel.ContainsPosition(textArea.Caret.Position))
                    {
                        return;
                    }
                    int len = sel.Length;
                    textArea.Document.Remove(sel.Offset, len);
                    if (sel.Offset < offset)
                    {
                        offset -= len;
                    }
                }
                textArea.SelectionManager.ClearSelection();
                InsertString(textArea, offset, prompt.VariableName);
                textArea.Document.RequestUpdate(new TextAreaUpdate(TextAreaUpdateType.WholeTextArea));
                template.TemplateText = textArea.Document.TextContent;
                doc.Changed = true;
                updateFormTitle(doc.FileName);
            }
            finally
            {
                textArea.Document.UndoStack.EndUndoGroup();
                textArea.EndUpdate();
            }
        }

        void InsertString(ICSharpCode.TextEditor.TextArea textArea, int offset, string str)
        {
            textArea.Document.Insert(offset, str);
            textArea.SelectionManager.SetSelection(new DefaultSelection(textArea.Document,
                                                                        textArea.Document.OffsetToPosition(offset),
                                                                        textArea.Document.OffsetToPosition(offset + str.Length)));
            textArea.Caret.Position = textArea.Document.OffsetToPosition(offset + str.Length);
            textArea.Refresh();
        }

        private void templateEditor_DragOver(object sender, DragEventArgs e)
        {
            if (e.Data.GetData(typeof(TreeNode)) != null)
            {
                //ICSharpCode.TextEditor.TextArea textArea = (ICSharpCode.TextEditor.TextArea)sender;
                //Point pt = textArea.PointToClient(new Point(e.X, e.Y));
                //textArea.GetChildAtPoint(pt);

                TreeNode node = (TreeNode)e.Data.GetData(typeof(TreeNode));
                Hashtable idPack = node.Tag as Hashtable;
                switch (idPack["Type"] as string)
                {
                    case "promptgroup":
                        break;
                    case "prompt":
                        e.Effect = DragDropEffects.Copy;
                        break;
                }
            }
        }

        private void documentTree_MouseDoubleClick(object sender, MouseEventArgs e)
        {

            // Point where the mouse is clicked.
            Point p = new Point(e.X, e.Y);

            // Get the node that the user has clicked.
            TreeNode node = documentTree.GetNodeAt(p);

            try
            {
                Hashtable idPack = node.Tag as Hashtable;
                NodeClicker clicker = idPack["ClickHandler"] as NodeClicker;
                editor_pg.SelectedObject = idPack["Data"];
                clicker(idPack["Data"]);
            }
            catch { }
        }

        private void addPromptToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                TreeNode node = documentTree.SelectedNode;
                Hashtable idPack = node.Tag as Hashtable;
                if ((string)idPack["Type"] == "promptgroup")
                {
                    PromptGroup promptGroup = (PromptGroup)idPack["Data"];
                    promptGroup.Add(new Prompt("NewPrompt", Prompt.PromptType.TEXT, "", promptGroup));
                    refreshTreeView();
                }
            }
            catch(Exception ex)
            {
                MessageBox.Show("Error creating prompt: " + ex.Message);
            }
        }

        private void templateEditor_KeyUp(object sender, KeyEventArgs e)
        {
            ICSharpCode.TextEditor.TextArea textArea = (ICSharpCode.TextEditor.TextArea)sender;
            template.TemplateText = textArea.Document.TextContent;
            doc.Changed = true;
        }

        private void exitToolStripMenuItem_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void deletePromptMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                TreeNode node = documentTree.SelectedNode;
                Hashtable idPack = node.Tag as Hashtable;
                if ((string)idPack["Type"] == "prompt")
                {
                    Prompt prompt = (Prompt)idPack["Data"];
                    prompt.Remove();
                    refreshTreeView();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error creating prompt: " + ex.Message);
            }
        }

        private void moveUpToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                TreeNode node = documentTree.SelectedNode;
                Hashtable idPack = node.Tag as Hashtable;
                if ((string)idPack["Type"] == "prompt")
                {
                    Prompt prompt = (Prompt)idPack["Data"];
                    prompt.OrderKey--;
                    refreshTreeView();
                }
                else if ((string)idPack["Type"] == "promptgroup")
                {
                    PromptGroup promptGroup = (PromptGroup)idPack["Data"];
                    int savedOrderKey = promptGroup.OrderKey;
                    promptGroup.OrderKey = promptGroup.OrderKey - 2;
                    doc.Changed = true;
                    updateFormTitle(doc.FileName);
                    //promptGroup.Parent.ForEach(delegate(PromptGroup p) { if (p.OrderKey == promptGroup.OrderKey) { p.OrderKey = savedOrderKey; } });
                    refreshTreeView();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error reordering: " + ex.Message);
            }
        }
        #endregion

        private void moveDownToolStripMenuItem_Click(object sender, EventArgs e)
        {
            try
            {
                TreeNode node = documentTree.SelectedNode;
                Hashtable idPack = node.Tag as Hashtable;
                if ((string)idPack["Type"] == "prompt")
                {
                    Prompt prompt = (Prompt)idPack["Data"];
                    prompt.OrderKey++;
                    refreshTreeView();
                }
                else if ((string)idPack["Type"] == "promptgroup")
                {
                    PromptGroup promptGroup = (PromptGroup)idPack["Data"];
                    int savedOrderKey = promptGroup.OrderKey;
                    promptGroup.OrderKey = promptGroup.OrderKey + 2;
                    doc.Changed = true;
                    updateFormTitle(doc.FileName);
                    //promptGroup.Parent.ForEach(delegate(PromptGroup p) { if (p.OrderKey == promptGroup.OrderKey) { p.OrderKey = savedOrderKey; } });
                    refreshTreeView();
                }
            }
            catch (Exception ex)
            {
                MessageBox.Show("Error reordering: " + ex.Message);
            }
        }
    }

    public static class Icons
    {
        public const int DATABASE_TABLE = 0;
        public const int BRICK = 1;
        public const int CHART_PIE = 2;
        public const int CLOCK = 3;
        public const int MONITOR = 4;
        public const int SCRIPT = 5;
        public const int TABLE_EDIT = 6;
        public const int TABLE_GEAR = 7;
        public const int TABLE_LIGHTNING = 8;
        public const int TABLE_SORT = 9;
        public const int DATABASE_ADD = 10;
        public const int FOLDER_WRENCH = 11;
        public const int WRENCH = 12;
        public const int REPORT = 13;
        public const int TEXT_FIELD = 14;
        public const int APPLICATION_XP_TERMINAL = 15;
        public const int IMAGE = 16;
        public const int IMAGES = 17;
        public const int DATABASE = 18;
        public const int TEXT_FIELD_SUB = 19;
        public const int TEXT_FIELD_ADD = 20;
        public const int FORM = 21;
        public const int FORM_ADD = 22;
        public const int FORM_EDIT = 23;
        public const int FORM_DELETE = 24;
        public const int FORM_MAGNIFY = 25;
        public const int DATE = 26;
        public const int TEXT_ALIGN_LEFT = 27;
        public const int CHECKBOX = 28;
        public const int CHECKBOX_OLD = 29;
        public const int APPLICATION_CASCADE = 30;
    }
}
