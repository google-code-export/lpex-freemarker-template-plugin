using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.IO;
using System.Collections.Specialized;

namespace TemplateBuilder
{
    public delegate void StatusUpdater(string status);
    public delegate void OpenedFileHandler();
    public delegate void NewFileHandler();
    public delegate void RecentFilesChangedHandler(StringCollection recentFiles);
    public delegate void CurrentFileChangedHandler(string filePath, string fileName);
    public delegate void BeforeFileSavedHandler();
    public delegate void AfterFileSavedHandler();
    public delegate void FileChangedHandler();

    public class DocumentManager
    {
        public string FilePath { get; set; }
        public string FileName { get; set; }
        public bool Opened { get; set; }
        public bool IsNew { get; set; }
        public string FileType { get; set; }
        public string FileTypeName { get; set; }
        public string Data { get; set; }
        public StringCollection RecentFiles { get; set; }


        private bool _Changed;
        public bool Changed
        {
            set
            {
                if (value == true)
                {
                    if (fileChangedHandler != null)
                        fileChangedHandler();
                }
                this._Changed = value;
            }
            get { return this._Changed; }
        }

        public StatusUpdater updateStatus = null;
        public OpenedFileHandler openedFileHandler = null;
        public NewFileHandler newFileHandler = null;
        public RecentFilesChangedHandler recentFilesChangedHandler = null;
        public CurrentFileChangedHandler currentFileChanged = null;
        public BeforeFileSavedHandler beforeFileSavedHandler = null;
        public AfterFileSavedHandler afterFileSavedHandler = null;
        public FileChangedHandler fileChangedHandler = null;

        public DocumentManager(string fileType, string fileTypeName)
        {
            initialize();
            FileType = fileType;
            FileTypeName = fileTypeName;
            RecentFiles = new StringCollection();
        }

        public string FileTypeFilter {
            get { return FileTypeName + " files (*." + FileType + ")|*." + FileType + ""; }
        }

        public bool IsUnsaved
        {
            get { return ((Changed) || (IsNew == true)); }
        }

        public bool New()
        {
            if (HandleUnsavedContent())
            {
                initialize();
                FilePath = "";
                FileName = "Untitled";
                Changed = true;
                Opened = true;
                IsNew = true;
                Data = "";
                if (updateStatus != null)
                    updateStatus("New file created.");
                if (newFileHandler != null)
                    newFileHandler();
                if (currentFileChanged != null)
                    currentFileChanged(FilePath, FileName);
                return true;
            }
            else
            {
                //user opted not to do away with the already open document
                return false;
            }
        }

        public bool Open()
        {
            //Prompt the user with a file open dialog then return the path
            OpenFileDialog openDlg = new OpenFileDialog();
            if (FilePath == string.Empty)
            {
                openDlg.InitialDirectory = Application.ExecutablePath;
            }
            else
            {
                openDlg.InitialDirectory = FilePath;
            }
            openDlg.Title = "Open...";
            openDlg.Filter = FileTypeFilter;
            DialogResult result = openDlg.ShowDialog();
            if (result == DialogResult.OK)
            {
                return Open(openDlg.FileName);
            }
            else
            {
                return false;
            }
        }

        public bool Open(string path)
        {
            if (HandleUnsavedContent())
            {
                if (File.Exists(path))
                {
                    FilePath = path;
                    FileName = Path.GetFileName(path);
                    Changed = false;
                    IsNew = false;
                    Data = File.ReadAllText(FilePath);
                    if (Data == string.Empty)
                    {
                        initialize();
                        if (updateStatus != null)
                            updateStatus("The file was empty.");
                        return false;
                    }
                    else
                    {
                        Opened = true;
                        updateRecentFiles();
                        if (openedFileHandler != null)
                            openedFileHandler();
                        if (updateStatus != null)
                            updateStatus("Opened the file.");
                        return true;
                    }
                }
                else
                {
                    return false;
                }
            }
            else
            {
                //user opted not to do away with the already open document
                return false;
            }
        }

        public bool Save()
        {
            if (beforeFileSavedHandler != null)
                beforeFileSavedHandler();
            if ((FilePath == string.Empty) || (IsNew))
            {
                if (SaveAs())
                {
                    if (afterFileSavedHandler != null)
                        afterFileSavedHandler();
                    return true;
                }
                return false;
            }
            else
            {
                if (saveToPath(FilePath))
                {
                    updateRecentFiles();
                    Changed = false;
                    if (updateStatus != null)
                        updateStatus("Saved the file.");
                    if (afterFileSavedHandler != null)
                        afterFileSavedHandler();
                    return true;
                }
                else
                {
                    if (updateStatus != null)
                        updateStatus("Failed to save the file.");
                    return false;
                }
            }
        }

        public bool SaveAs()
        {
            if (saveToPath(promptSaveAs()))
            {
                updateRecentFiles();
                Changed = false;
                if (updateStatus != null)
                    updateStatus("Saved the file.");
                return true;
            }
            else
            {
                if (updateStatus != null)
                    updateStatus("Failed to save the file.");
                return false;
            }
        }

        public bool HandleUnsavedContent()
        {
            if (IsUnsaved)
            {
                DialogResult res = MessageBox.Show("Save changes to " +
                    FileName, "Unsaved Content",
                    MessageBoxButtons.YesNoCancel);
                if (res == DialogResult.Yes)
                {
                    return Save();
                }
                if (res == DialogResult.No)
                {
                    return true;
                }
                if (res == DialogResult.Cancel)
                {
                    return false;
                }
            }
            else
            {
                return true;
            }
            return false;
        }

        private string promptSaveAs()
        {
            //Prompt the user with a file save dialog then return the path
            SaveFileDialog saveDlg = new SaveFileDialog();
            saveDlg.InitialDirectory = Application.ExecutablePath;
            saveDlg.Title = "Save As...";
            saveDlg.AddExtension = true;
            saveDlg.DefaultExt = FileType;
            //saveDlg.OverwritePrompt = true;
            saveDlg.ValidateNames = true;
            saveDlg.Filter = FileTypeFilter;
            saveDlg.CheckPathExists = true;
            saveDlg.FileName = FilePath;
            DialogResult result = saveDlg.ShowDialog();
            if (result == DialogResult.OK)
            {
                return saveDlg.FileName;
            }
            else
            {
                return "";
            }
        }

        private bool saveToPath(string path)
        {
            if (path == string.Empty) { return false; }
            Exception error = null;
            try
            {
                System.IO.FileInfo file = new System.IO.FileInfo(FilePath);
                System.IO.StreamWriter streamWriter = file.CreateText();
                streamWriter.WriteLine(Data);
                streamWriter.Close();
            }
            catch (System.Exception e)
            {
                error = e;
            }
            if (error != null)
            {
                if (updateStatus != null)
                    updateStatus("Failed to save file.");
                return false;
            }
            IsNew = false;
            Changed = false;
            FilePath = path;
            FileName = Path.GetFileName(path);
            return true;
        }

        private void initialize()
        {
            Opened = false;
            Changed = false;
            IsNew = false;
            FilePath = "";
            Data = "";
        }

        private void updateRecentFiles()
        {
            //Update recent files
            try
            {
                //Delete it if it already exists so it can be re-added to the top
                RecentFiles.Remove(FilePath);
            }
            catch { }

            //Add it to the top
            RecentFiles.Insert(0, FilePath);

            try
            {
                //Only keep up to ten recents
                RecentFiles.RemoveAt(10);
            }
            catch { }
            recentFilesChangedHandler(RecentFiles);
        }
    }
}
