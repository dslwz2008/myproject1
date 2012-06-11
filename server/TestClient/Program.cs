using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Net;
using System.IO;

namespace TestClient
{
    class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("准备上传，按任意键继续...");
            Console.ReadKey();
            Post("http://192.168.72.4:10002/PostResourceContent/1.jpg", "1.jpg");
            Console.WriteLine("完成");
            Console.ReadKey();
        }

        static private void Post(string url, string file)
        {
            var webClient = new WebClient();
            webClient.Headers.Add("Content-Type", GetMineType(file));

            byte[] buffer;
            using (var fs = File.OpenRead(file))
            {
                buffer = new byte[(int)fs.Length];
                fs.Read(buffer, 0, (int)fs.Length);
            }
            
            webClient.UploadData(url, buffer);
        }

        static private void PostOld(string url, string file)
        {
            var request = HttpWebRequest.Create(
                new Uri(url));

            // POST方式
            request.Method = "POST";
            // Content-Type
            request.ContentType = GetMineType(file);
            // Content-Length
            request.ContentLength = new FileInfo(file).Length;

            var stream = request.GetRequestStream();
            using (var fs = File.OpenRead(file))
            {
                byte[] buffer = new byte[10240];
                int i = fs.Read(buffer, 0, buffer.Length);
                while (i > 0)
                {
                    stream.Write(buffer, 0, i);
                    i = fs.Read(buffer, 0, buffer.Length);
                }
            }
            request.GetResponse();
        }

        static private string GetMineType(string file)
        {
            string minetype = null;
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
    }


}
