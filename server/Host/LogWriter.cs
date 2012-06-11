using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.IO;
using System.Windows.Forms;

namespace ThrowAndCatchServer.Host
{
    public class LogWriter: TextWriter
    {
        private TextBox _textBox;

        public LogWriter(TextBox textBox)
        {
            _textBox = textBox;
        }

        public override void Write(char value)
        {
            _textBox.AppendText(value.ToString());
        }

        public override void Write(string value) 
        {
            _textBox.AppendText(value);
        }

        public override void Write(char[] buffer, int index, int count) 
        {
            _textBox.AppendText(new string(buffer, index, count));
        }

        public override void Close()
        {
            this.Dispose(true);
        }

        public override Encoding Encoding
        {
            get { return Encoding.UTF8; }
        }
    }
}
