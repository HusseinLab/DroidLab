using System;
using Microsoft.VisualStudio.TestTools.UnitTesting;

namespace SellerDroidTests
{
    [TestClass]
    public class CloudSightTest
    {
        string authKey = "ox6GXT3qGyrXDnsDy3YbHg";

        [TestMethod]
        public void TestCloudSightPost()
        {
            RestSharp.RestClient client = new RestSharp.RestClient("https://cloudsightapi.com/");

            var request = new RestSharp.RestRequest("/image_requests", RestSharp.Method.POST);
            request.AddHeader("Authorization", "CloudSight " + authKey);
            request.AddParameter("image_request[locale]", "en-US");
            request.AddFile("image_request[image]", @"C:\Users\fachamieh\Pictures\8589130471575-arabian-horse-wallpaper-hd.jpg", "image/jpeg");
            var response = client.Execute(request);

            if(response.ResponseStatus == RestSharp.ResponseStatus.Completed && response.StatusCode == System.Net.HttpStatusCode.OK)
            {
                // {"token":"mvjIf_2QDoJTolu72MaXwg","url":"//d1spq65clhrg1f.cloudfront.net/uploads/image_request/image/46/46324/46324835/8589130471575-arabian-horse-wallpaper-hd.jpg","ttl":53.715187256,"status":"not completed"}
            }

            System.Diagnostics.Debug.WriteLine("Response Status:" + response.ResponseStatus);
            System.Diagnostics.Debug.WriteLine("Response Code:" + response.StatusCode);

            System.Diagnostics.Debug.WriteLine("Content Length: " + response.ContentLength);
            System.Diagnostics.Debug.WriteLine("Content Type: " + response.ContentType);
            System.Diagnostics.Debug.WriteLine(response.Content);
        }

        [TestMethod]
        public void TestCloudSightResponse()
        {
            var token = "mvjIf_2QDoJTolu72MaXwg";

            var response = CloudSightApi.Helper.GetImageResponse(authKey, token);

            System.Diagnostics.Debug.WriteLine(response.ToString());
        }

        [TestMethod]
        public void TestCloudSight()
        {
            var imageBytes = System.IO.File.ReadAllBytes(@"C:\Users\fachamieh\Pictures\8589130471575-arabian-horse-wallpaper-hd.jpg");

            var response = CloudSightApi.Helper.PostImage(authKey, imageBytes);
            
            if(response.IsValid)
            {
                response = CloudSightApi.Helper.GetImageResponse(authKey, response.token);
            }

            System.Diagnostics.Debug.WriteLine(response);
        }
    }
}
