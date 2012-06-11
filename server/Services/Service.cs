using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using System.ServiceModel.Web;
using System.IO;
using System.Web;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

namespace ThrowAndCatchServer.Services
{
    public class Service : IService
    {
        static public TextWriter LogOut { get; set; }
        static public string CurrentDirectory { get; set; }

        [WebGet(UriTemplate = "/Test/")]
        public Stream Test()
        {
            // 记录请求信息
            WriteRequestInfomation();

            var bytes = Encoding.UTF8.GetBytes("{ \"test\" : \"ok\" }");
            var stream = new MemoryStream(bytes);
            WebOperationContext.Current.OutgoingResponse.ContentType = "text/plain";
            return stream;
        }

        [WebGet(UriTemplate = "/FolderContent/")]
        public Stream GetFolderContent()
        {
            // 记录请求信息
            WriteRequestInfomation();

            string path = Service.CurrentDirectory;

            JObject json = new JObject();
            try
            {
                // 取得所有文件信息
                var files = Directory.GetFiles(path).Select(i => Path.GetFileName(i));

                // 转换为Json
                var jarray = new JArray();
                foreach (var i in files)
                    jarray.Add(i);

                json.Add("content", jarray);
            }
            catch (Exception exc)
            {
                json = new JObject();
                // Release时应当修改
                json.Add("error", exc.Message);
            }

            WebOperationContext.Current.OutgoingResponse.ContentType = "text/plain";
            MemoryStream ms = new MemoryStream(Encoding.UTF8.GetBytes(json.ToString()));

            return ms;
        }

        [WebGet(UriTemplate = "/ResourceContent/{name}")]
        public Stream GetResourceContent(string name)
        {
            // 记录请求信息
            WriteRequestInfomation();

            // 取得文件路径
            string path = Path.Combine(Service.CurrentDirectory,
                HttpUtility.UrlDecode(name).Replace('/', Path.DirectorySeparatorChar));

            try
            {
                string minetype = GetMineType(name);
                WebOperationContext.Current.OutgoingResponse.ContentType = minetype;
                var stream = File.OpenRead(path);

                WriteLogWithNewLine("读取并返回文件：" + path);

                return stream;
            }
            catch (Exception exc)
            {
                var json = new JObject();
                json.Add("error", exc.Message);
                WriteLogWithNewLine("读取文件时发生异常：" + Environment.NewLine + json.ToString());
                WebOperationContext.Current.OutgoingResponse.ContentType = "text/plain";
                MemoryStream ms = new MemoryStream(Encoding.UTF8.GetBytes(json.ToString()));
                return ms;
            }
        }

        [WebInvoke(Method = "POST", UriTemplate = "/PostResourceContent/{name}")]
        public void PostResourceContent(Stream transfer, string name)
        {
            // 记录请求信息
            WriteRequestInfomation();

            // 取得文件路径
            string path = Path.Combine(Service.CurrentDirectory, name);

            using (var fs = File.Create(path, 10240))
            {
                var buffer = new byte[10240];
                int i = transfer.Read(buffer, 0, buffer.Length);
                while (i > 0)
                {
                    fs.Write(buffer, 0, i);
                    i = transfer.Read(buffer, 0, buffer.Length);
                }
            }

            WriteLogWithNewLine("写入文件完毕：" + path);
        }

        private string GetMineType(string file)
        {
            string minetype = string.Empty;
            string ext = Path.GetExtension(file).ToLower();
            switch (ext)
            {
                case ".png":
                    minetype = "image/png";
                    break;
                case ".jpg":
                case ".jpeg":
                    minetype = "image/jpeg";
                    break;
                case ".tif":
                case ".tiff":
                    minetype = "image/tiff";
                    break;
                case ".mp4":
                    minetype = "audio/mp4";
                    break;
            }

            return minetype;
        }

        private void WriteRequestInfomation()
        {
            var context = WebOperationContext.Current;
            WriteLog(DateTime.Now.ToString());
            WriteLog("请求地址：" + context.IncomingRequest.UriTemplateMatch.RequestUri);
            WriteLog("匹配路径：" + context.IncomingRequest.UriTemplateMatch.Template);
        }

        private void WriteLog(string s)
        {
            if (Service.LogOut != null)
                LogOut.WriteLine(s);
        }

        private void WriteLogWithNewLine(string s)
        {
            if (Service.LogOut != null)
                LogOut.WriteLine(s+Environment.NewLine);
        }

        private void WriteLog()
        {
            if (Service.LogOut != null)
                LogOut.WriteLine(Environment.NewLine);
        }
    }
}
