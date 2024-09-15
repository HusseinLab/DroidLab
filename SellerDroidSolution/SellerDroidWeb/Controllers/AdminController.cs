using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace SellerDroidWeb.Controllers
{
    public class AdminController : Controller
    {
        // GET: Admin
        public ActionResult Index()
        {
            var inValid = ValidateAdmin();
            if (inValid != null) return inValid;

            return View();
        }

        public ActionResult SetLockingStatus(string id, bool locked)
        {
            var inValid = ValidateAdmin();
            if (inValid != null) return inValid;

            var user = Utility.DB.AspNetUsers.SingleOrDefault(u => u.Id == id);
            user.Seller_User_Info.IsLocked = locked;
            user.Seller_User_Info.Modified = DateTime.Now;

            Utility.DB.SaveChanges();

            return new EmptyResult();
        }

        public ActionResult ChangeIsFeatured(int id, bool featured)
        {
            var inValid = ValidateAdmin();
            if (inValid != null) return inValid;

            var product = Utility.DB.Seller_Product.SingleOrDefault(x => x.Id == id);
            product.Is_Featured = featured;

            Utility.DB.SaveChanges();

            return new EmptyResult();
        }

        ActionResult ValidateAdmin()
        {
            if (!Utility.IsAdmin())
            {
                Session["ErrorMessage"] = "You do not have access to Admin Section";
                return RedirectToAction("Index", "Home");
            }
            else
                return null;
        }
    }
}