using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace CloudSightApi
{
    public class Helper
    {
        /// <summary>
        /// Post Image to Cloud Sight, given the image contents (bytes), using the provider Authentication Key
        /// </summary>
        /// <param name="authKey">The Authentication Key provided by CloudSight</param>
        /// <param name="imageBytes">The image bytes</param>
        /// <returns></returns>
        public static CloudSightResponse PostImage(string authKey, byte[] imageBytes)
        {
            RestSharp.RestClient client = new RestSharp.RestClient("https://cloudsightapi.com/");

            var request = new RestSharp.RestRequest("/image_requests", RestSharp.Method.POST);
            request.AddHeader("Authorization", "CloudSight " + authKey);
            request.AddParameter("image_request[locale]", "en-US");
            request.AddFile("image_request[image]", imageBytes, "uploaded_file.jpg", "image/jpeg");
            var execution = client.Execute(request);

            CloudSightResponse response = new CloudSightResponse();

            // All is well
            if (execution.ResponseStatus == RestSharp.ResponseStatus.Completed && execution.StatusCode == System.Net.HttpStatusCode.OK)
                response = Newtonsoft.Json.JsonConvert.DeserializeObject<CloudSightResponse>(execution.Content);

            return response;
        }

        /// <summary>
        /// Get Image Response (Status and Processing Results, if any), given the token got by Posting
        /// </summary>
        /// <param name="authKey">The Authentication Key provided by CloudSight</param>
        /// <param name="token">The token returned by <see cref="PostImage"/></param>
        /// <param name="waitTillCompletion">If </param>
        /// <returns></returns>
        public static CloudSightResponse GetImageResponse(string authKey, string token, bool waitTillCompletion = true)
        {
            // Validate token
            if (string.IsNullOrWhiteSpace(token))
                throw new ArgumentException("Token cannot be null or empty");

            // Create restful client
            RestSharp.RestClient client = new RestSharp.RestClient("https://cloudsightapi.com/");

            // Create Request
            var request = new RestSharp.RestRequest("/image_responses/" + token, RestSharp.Method.GET);
            request.AddHeader("Authorization", "CloudSight " + authKey);
            CloudSightResponse response = new CloudSightResponse();

            // Do (and loop when necessary) until we got response
            do
            {
                var execution = client.Execute(request);
                if (execution.ResponseStatus == RestSharp.ResponseStatus.Completed && execution.StatusCode == System.Net.HttpStatusCode.OK)
                {
                    response = Newtonsoft.Json.JsonConvert.DeserializeObject<CloudSightResponse>(execution.Content);
                    if (waitTillCompletion && response.IsNotCompleted)
                    {
                        System.Threading.Thread.Sleep(1000);
                    }
                }
                else
                {
                    break;
                }
            } while (waitTillCompletion && response.IsNotCompleted);

            return response;
        }
    }
}