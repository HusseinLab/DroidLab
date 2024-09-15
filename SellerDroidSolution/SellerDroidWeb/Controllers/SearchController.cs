using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace SellerDroidWeb.Controllers
{
    public class SearchController : Controller
    {
        // GET: Search
        public ActionResult Index(string q)
        {
            IEnumerable<Models.Seller_Product> products = new Models.Seller_Product[] { };

            if(!string.IsNullOrWhiteSpace(q))
            {
                products = from p in Utility.DB.Seller_Product
                               where p.Is_Visible
                    && (
                    p.Name.Contains(q)
                    ||
                    p.Small_Description.Contains(q)
                    ||
                    p.Long_Description.Contains(q)
                    )
                    orderby
                    p.Is_Featured descending,
                    p.Average_Rating descending
                    select p;
            }

            return View(products.ToList());
        }
    }
}