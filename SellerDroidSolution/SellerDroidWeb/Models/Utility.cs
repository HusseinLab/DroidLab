using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Web;
using System.Web.Security;

namespace SellerDroidWeb
{
    public class Utility
    {
        public static Models.AspNetUser CurrentUser
        {
            get
            {
                Models.AspNetUser user = null;

                if (HttpContext.Current.User.Identity.IsAuthenticated)
                {
                    lock (HttpContext.Current)
                    {
                        user = HttpContext.Current.Items["CurrentUser"] as Models.AspNetUser;
                        if (user == null)
                        {
                            user = DB.AspNetUsers.Single(u => u.UserName == HttpContext.Current.User.Identity.Name);
                            HttpContext.Current.Items["CurrentUser"] = user;
                        }
                    }
                }
                return user;
            }
        }

        public static bool IsAdmin()
        {
            return IsInRole("Administrator");
        }

        public static bool IsSupplier()
        {
            return IsInRole("Supplier");
        }

        public static bool IsInRole(string role)
        {
            return CurrentUser == null ? false : CurrentUser.AspNetRoles.Any(r => r.Name == role);
        }

        public static SellerDroidWeb.Models.SellerDatabaseEntities DB
        {
            get
            {
                lock (HttpContext.Current)
                {
                    var db = HttpContext.Current.Items["Entities"] as Models.SellerDatabaseEntities;
                    if(db == null)
                    {
                        db = new Models.SellerDatabaseEntities();
                        HttpContext.Current.Items["Entities"] = db;
                    }
                    return db;
                }
            }
        }

        public static string GetGoogleImagingAnnotations(string imageBase64)
        {
            // Create the json request content for google vision
            var googleRequest = @"{
                 'requests': [
                  {
                    'image': { 'content': '" + imageBase64 + @"' },
                    'features': [ { 'type': 'LABEL_DETECTION', 'maxResults': 1 } ]
                  }
                 ]
                }";
            var googleRequestBytes = System.Text.UTF8Encoding.UTF8.GetBytes(googleRequest);

            // Create the request to google Vision
            var request = WebRequest.CreateHttp("https://vision.googleapis.com/v1/images:annotate?key=AIzaSyCHjy0lJr1ypQ9amlcW0H1EquJP7YdIZoU");
            request.ContentLength = googleRequestBytes.Length;
            request.ContentType = "application/json";
            request.Method = "POST";
            Stream requestStream = request.GetRequestStream();
            requestStream.Write(googleRequestBytes, 0, googleRequestBytes.Length);
            requestStream.Flush();
            requestStream.Close();

            // Get the response and convert to json
            var response = request.GetResponse();
            StreamReader reader = new StreamReader(response.GetResponseStream());
            var responseText = reader.ReadToEnd();
            var json = Newtonsoft.Json.Linq.JObject.Parse(responseText);

            // Extract and return first annotation (should be highest score)
            var bestAnnotation = json["responses"][0]["labelAnnotations"][0]["description"].ToString();

            return bestAnnotation;
        }

    }
}