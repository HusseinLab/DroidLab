using Microsoft.AspNet.Identity.Owin;
using System.Collections.Generic;
using System.Net.Http.Headers;
using System.Web;
using System.Web.Http;
using System.Web.Security;
using System.Web.Http.Controllers;
using System.Linq;
using System.IO;
using System.Collections;
using System.Drawing;
using System;
using System.Net;
using System.Linq.Expressions;

namespace SellerDroidWeb.Controllers
{
    [Authorize]
    public class MobileAccessController : ApiController
    {
        #region Initializers and Requirements
        private ApplicationSignInManager _signInManager;
        private ApplicationUserManager _userManager;

        public MobileAccessController()
        {
        }

        public MobileAccessController(ApplicationUserManager userManager, ApplicationSignInManager signInManager)
        {
            UserManager = userManager;
            SignInManager = signInManager;
        }

        public ApplicationSignInManager SignInManager
        {
            get
            {
                return _signInManager ?? HttpContext.Current.GetOwinContext().Get<ApplicationSignInManager>();
            }
            private set
            {
                _signInManager = value;
            }
        }

        public ApplicationUserManager UserManager
        {
            get
            {
                return _userManager ?? HttpContext.Current.GetOwinContext().GetUserManager<ApplicationUserManager>();
            }
            private set
            {
                _userManager = value;
            }
        }

        #endregion

        [AcceptVerbs("POST")]
        [ActionName("Login")]
        [AllowAnonymous]
        public string Login(Models.DTO.LoginRequest login)
        {
            var result = SignInManager.PasswordSignIn(login.Username, login.Password, false, false);
            System.Diagnostics.Trace.WriteLine("Login Attempt for " + login.Username + ", " + result);
            return result.ToString();
        }

        [AcceptVerbs("POST", "GET")]
        [ActionName("Logout")]
        [AllowAnonymous]
        public string Logout()
        {
            System.Diagnostics.Trace.WriteLine("Logout for " + SignInManager.AuthenticationManager.User.Identity.Name);
            SignInManager.AuthenticationManager.SignOut();
            return "Success";
        }

        [AcceptVerbs("GET")]
        [ActionName("WhoAmI")]
        [AllowAnonymous]
        public string WhoAmI()
        {
            var user = SignInManager.AuthenticationManager.User;
            if (user == null) return "";
            else return user.Identity.Name;
        }

        [AcceptVerbs("POST")]
        [ActionName("Register")]
        [AllowAnonymous]
        public string Register(Models.RegisterViewModel model)
        {
            var result = AccountController.CreateUser(SignInManager, UserManager, model);
            return result.Succeeded ? "Success" : string.Join("\n", result.Errors);
        }

        [AcceptVerbs("GET", "POST")]
        [ActionName("Product")]
        [AllowAnonymous]
        public Models.DTO.Product Product(int id)
        {
            var product = Utility.DB.Seller_Product.SingleOrDefault(p => p.Id == id);

            if (product == null) return null;

            var rating = Utility.DB.Seller_Product_Rating
                .SingleOrDefault(r => r.Product_Id == id
                                        && r.AspNetUser.UserName == SignInManager.AuthenticationManager.User.Identity.Name);

            return new Models.DTO.Product()
            {
                id = id.ToString(),
                averageRating = (float)(product.Average_Rating ?? -1),
                currentUserRating = (float)(rating == null ? -1 : rating.Rating),
                description = product.Small_Description,
                longDescription = product.Long_Description,
                imageUri = product.Main_Image_Id.HasValue ? "/SellerFile/Get/?id=" + product.Main_Image_Id : null,
                name = product.Name,
                supplier = new Models.DTO.Supplier()
                {
                    id = product.Seller_User_Id,
                    name = product.AspNetUser.Seller_User_Info.Name
                }
            };
        }

        [AcceptVerbs("GET", "POST")]
        [ActionName("Supplier")]
        [AllowAnonymous]
        public Models.DTO.Supplier Supplier(string id)
        {
            var supplier = Utility.DB.Seller_User_Info.SingleOrDefault(x => x.Id == id);

            if (supplier == null) return null;

            return new Models.DTO.Supplier()
            {
                id = id.ToString(),
                description = supplier.Description,
                name = supplier.Name,
                imageUri = supplier.Picture_Id.HasValue ? "/SellerFile/Get/?id=" + supplier.Picture_Id : null,
            };
        }

        Models.DTO.ProductSummary DTO(Models.Seller_Product p)
        {
            return new Models.DTO.ProductSummary()
            {
                id = p.Id.ToString(),
                averageRating = p.Average_Rating ?? 0.0,
                name = p.Name,
                description = p.Small_Description,
                imageUri = p.Main_Image_Id.HasValue ? "/SellerFile/Get/?id=" + p.Main_Image_Id : null
            };
        }


        [AcceptVerbs("POST")]
        [ActionName("SearchProducts")]
        [AllowAnonymous]
        public List<Models.DTO.ProductSummary> SearchProducts([FromBody] string searchText)
        {
            var results = new List<Models.DTO.ProductSummary>();

            var products = Utility.DB.Seller_Product.Where(p => p.Is_Visible)
                .Where(p => p.Name.Contains(searchText) || p.Small_Description.Contains(searchText) || p.Long_Description.Contains(searchText) || p.Keywords.Contains(searchText))
                .OrderByDescending(p => p.Is_Featured)
                .ThenByDescending(p => p.Average_Rating);

            foreach (var p in products)
            {
                results.Add(DTO(p));
            }

            return results;
        }

        [AcceptVerbs("GET")]
        [ActionName("RateProduct")]
        public double RateProduct(int id, double myRating)
        {
            return ProductController.UpdateProductRatingForCurrentUser(id, myRating);
        }

        [AcceptVerbs("GET")]
        [ActionName("SearchByBarcode")]
        [AllowAnonymous]
        public Models.DTO.ProductSummary[] SearchByBarcode(string barcode)
        {
            try
            {
                var products = Utility.DB.Seller_Product
                .Where(p => p.Is_Visible && p.Barcode == barcode)
                .OrderByDescending(p => p.Is_Featured)
                .OrderByDescending(p => p.Average_Rating)
                .ToList()
                .Select(p => DTO(p))
                .ToArray();
                return products;
            }
            catch (Exception ex)
            {
                System.Diagnostics.Debug.WriteLine("** ERROR in SearchByBarcode (" + barcode + ")");
                System.Diagnostics.Debug.WriteLine(ex.Message);
            }
            return null;
        }

        [AcceptVerbs("POST")]
        [ActionName("SearchByImage")]
        [AllowAnonymous]
        public Models.DTO.ProductSummary[] SearchByImage(string searchMode, [FromBody] string imageBase64)
        {
            try
            {
                var authKey = "ox6GXT3qGyrXDnsDy3YbHg";
                var response = CloudSightApi.Helper.PostImage(authKey, Convert.FromBase64String(imageBase64));
                if (response.IsValid)
                {

                    response = CloudSightApi.Helper.GetImageResponse(authKey, response.token);
                    if (response.IsValid)
                    {
                        System.Diagnostics.Trace.WriteLine("Search By Image, Response: " + response);

                        var query = Utility.DB.Seller_Product.Where(p => p.Is_Visible);

                        var unknown = "*.U.*.N.*.K.*.N.*.O.*.W.*.N.*";
                        var name = string.IsNullOrEmpty(response.name) ? unknown : response.name;
                        var category = response.categories != null && response.categories.Length > 0 ? response.categories[0] : unknown;
                        var flag = response.flags != null && response.flags.Length > 0 ? response.flags[0] : unknown;

                        query = query.Where(p =>
                            p.Name.Contains(name)
                            || p.Keywords.Contains(name)
                            || p.Small_Description.Contains(name)
                            || p.Long_Description.Contains(name)
                            || p.Keywords.Contains(category)
                            || p.Keywords.Contains(flag)
                            );

                        // Return results  
                        return query
                            .OrderByDescending(p => p.Is_Featured)
                            .OrderByDescending(p => p.Average_Rating)
                            .Take(50)
                            .ToList()
                            .Select(p => DTO(p))
                                .ToArray();
                    }
                }
            }
            catch (Exception ex)
            {
                System.Diagnostics.Trace.WriteLine("Search By Image, Error: " + ex.Message);
                System.Diagnostics.Debug.WriteLine("** Error Searching by Image **");
                System.Diagnostics.Debug.WriteLine(ex.ToString());
            }

            return null;
        }
    }
}