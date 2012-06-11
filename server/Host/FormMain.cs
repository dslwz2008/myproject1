using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using System.ServiceModel;
using System.Threading;
using ThrowAndCatchServer.Services;
using System.IO;

namespace ThrowAndCatchServer.Host
{
    public partial class FormMain : Form
    {
        bool _isRunning;
        ServiceHost _host;
        LogWriter _log;

        public FormMain()
        {
            InitializeComponent();

            _log = new LogWriter(textBox);
            Service.LogOut = _log;
            Service.CurrentDirectory = Environment.GetFolderPath(Environment.SpecialFolder.MyPictures);

            RefleshInfo();
        }

        private void buttonControlService_Click(object sender, EventArgs e)
        {
            if (_isRunning)
            {
                _host.Close();
                _host = null;
                buttonControlService.Text = "启动服务";
            }
            else
            {
                _host = new ServiceHost(typeof(ThrowAndCatchServer.Services.Service));
                _host.Open();
                buttonControlService.Text = "终止服务";
            }

            _isRunning = !_isRunning;
            RefleshInfo();
        }

        private void buttonChangeDir_Click(object sender, EventArgs e)
        {
            var dlg = new FolderBrowserDialog();
            if (Directory.Exists(Service.CurrentDirectory))
                dlg.SelectedPath = Service.CurrentDirectory;

            if (dlg.ShowDialog() == DialogResult.OK)
            {
                Service.CurrentDirectory = dlg.SelectedPath;
                RefleshInfo();
            }
        }

        private void RefleshInfo()
        {
            textBoxInfo.Text =
                "服务状态：" + (_isRunning ? "已启动" : "未启动") + Environment.NewLine +
                "当前目录：" + Service.CurrentDirectory ?? string.Empty;
        }
    }
}
