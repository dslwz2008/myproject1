using System;
using System.Collections.Generic;
using System.Linq;
using System.Runtime.Serialization;
using System.ServiceModel;
using System.Text;
using System.IO;

namespace ThrowAndCatchServer.Services
{
    [ServiceContract]
    public interface IService
    {
        [OperationContract]
        Stream Test();

        [OperationContract]
        Stream GetFolderContent();

        [OperationContract]
        Stream GetResourceContent(string name);

        [OperationContract]
        void PostResourceContent(Stream stream, string name);
    }
}
