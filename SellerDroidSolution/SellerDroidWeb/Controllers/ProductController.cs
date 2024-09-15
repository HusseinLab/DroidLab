using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace SellerDroidWeb.Controllers
{
    public class ProductController : Controller
    {
        // GET: Product
        public ActionResult View(int id)
        {
            var product = Utility.DB.Seller_Product.SingleOrDefault(p => p.Id == id);
            return View(product);
        }

        public static double UpdateProductRatingForCurrentUser(int id, double myRating)
        {
            var db = Utility.DB;

            var product = db.Seller_Product.SingleOrDefault(p => p.Id == id);
            var myId = Utility.CurrentUser.Id;
            var rating = db.Seller_Product_Rating.SingleOrDefault(r => r.User_Id == myId && r.Product_Id == id);

            if (rating == null && product != null)
            {
                rating = new Models.Seller_Product_Rating() { Product_Id = id, User_Id = myId, Created = DateTime.Now };
                db.Seller_Product_Rating.Add(rating);
            }


            rating.Rating = myRating;
            rating.Modified = DateTime.Now;

            db.SaveChanges();

            db.Update_Product_Average_Rating(id);

            db.Entry(product).Reload();

            return product.Average_Rating.Value;
        }

        public ActionResult UpdateMyRating(int id, double myRating)
        {
            return new JsonResult() { Data = UpdateProductRatingForCurrentUser(id, myRating) };
        }
    }
}