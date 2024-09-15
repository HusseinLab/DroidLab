using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace SellerDroidWeb.Controllers
{
    public class SellerFileController : Controller
    {
        // GET: SellerFile
        public ActionResult Get(int id)
        {
            var file = Utility.DB.Seller_File.Single(f => f.Id == id);
            return new FileContentResult(file.Content, file.Content_Type) { FileDownloadName = file.Filename };
        }
    }
}