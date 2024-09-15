using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;
using System.Web.Mvc;

namespace SellerDroidWeb.Controllers
{
    public class SupplierController : Controller
    {
        // GET: Supplier
        public ActionResult Index()
        {
            return View();
        }

        public ActionResult DeleteProduct(int id)
        {
            try
            {
                var db = Utility.DB;

                var product = db.Seller_Product.SingleOrDefault(p => p.Id == id);

                if (product == null) throw new Exception("Product Not found");

                if(product.AspNetUser.Id != Utility.CurrentUser.Id) throw new Exception("You are not authorized to delete product #" + id);

                db.Seller_Product.Remove(product);

                Session["WarningMessage"] = "Product #" + id + " was deleted";

                db.SaveChanges();
            }
            catch (Exception ex)
            {
                Session["ErrorMessage"] = ex.Message;
            }

            return RedirectToAction("Index");
        }

        [ValidateInput(false)]
        public ActionResult AddProduct(Models.Seller_Product product, HttpPostedFileBase product_Image)
        {
            try
            {
                var db = Utility.DB;

                product.AspNetUser = Utility.CurrentUser;
                
                if(product_Image != null && product_Image.ContentLength > 0)
                {
                    Models.Seller_File file = new Models.Seller_File()
                    {
                        Content_Type = product_Image.ContentType,
                        Content_Length = product_Image.ContentLength,
                        Filename = product_Image.FileName,
                        Content = new byte[product_Image.ContentLength],
                        Created = DateTime.Now,
                        Modified = DateTime.Now
                    };
                    product_Image.InputStream.Read(file.Content, 0, product_Image.ContentLength);
                    product.Seller_File = file;
                }

                product.Created = DateTime.Now;
                product.Modifed = DateTime.Now;

                db.Seller_Product.Add(product);

                db.SaveChanges();

                Session["InfoMessage"] = "Product " + product.Name + " was saved";
            }
            catch(Exception ex)
            {
                Session["ErrorMessage"] = ex.Message; 
            }

            return RedirectToAction("Index");
        }

        public ActionResult ViewProfile (string id)
        {
            var supplier = Utility.DB.AspNetUsers.SingleOrDefault(s => s.Id == id);
            return View(supplier);
        }

        public ActionResult UpdateMyLocation(double lat, double lng)
        {
            var info = Utility.CurrentUser.Seller_User_Info;
            info.Map_Latitude = lat;
            info.Map_Longitude = lng;
            Utility.DB.SaveChanges();

            return new EmptyResult();
        }

        [ValidateInput(false)]
        public ActionResult UpdateMyDescription(string description)
        {
            var info = Utility.CurrentUser.Seller_User_Info;
            info.Description = description;
            Utility.DB.SaveChanges();

            return new EmptyResult();
        }

        public ActionResult ChangeProductVisiblity(int id, bool visible)
        {
            var message = "Product {0} Visibility Changed to {1}";

            try
            {
                var db = Utility.DB;

                var product = db.Seller_Product.SingleOrDefault(p => p.Id == id);

                if (product == null) throw new Exception("Product Not found");

                if (!Utility.IsAdmin() && product.AspNetUser.Id != Utility.CurrentUser.Id) throw new Exception("You are not authorized to change product #" + id);

                product.Is_Visible = visible;

                db.SaveChanges();

                message = string.Format(message, product.Name, visible ? "visible" : "hidden");
            }
            catch (Exception ex)
            {
                message = string.Format("Error: Could not change product visibility.<br/>" + ex.Message);
            }

            return new JsonResult() { Data = message };
        }
    }
}