using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CloudSightApi
{
    /// <summary>
    /// 
    /// {"token":"mvjIf_2QDoJTolu72MaXwg","url":"//d1spq65clhrg1f.cloudfront.net/uploads/image_request/image/46/46324/46324835/8589130471575-arabian-horse-wallpaper-hd.jpg","ttl":53.715187256,"status":"not completed"}
    /// </summary>
    public class CloudSightResponse
    {
        public string token { get; set; }

        public string url { get; set; }

        public string status { get; set; }

        public double ttl { get; set; }

        public string name { get; set; }

        public string[] flags { get; set; }

        public string[] categories { get; set; }

        public string reason { get; set; }
        
        public bool IsNotCompleted { get { return "not completed".Equals(status); } }

        public bool IsCompleted { get { return "completed".Equals(status); } }

        public bool IsValid { get { return token != null; } }

        public override string ToString()
        {
            return Newtonsoft.Json.JsonConvert.SerializeObject(this);
        }
    }
}
