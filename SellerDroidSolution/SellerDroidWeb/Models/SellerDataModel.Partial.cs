using System;
using System.Collections.Generic;
using System.ComponentModel.DataAnnotations;
using System.Linq;
using System.Web;

namespace SellerDroidWeb.Models
{
    public partial class Seller_Product
    {
        [Display(Name="Featured")]
        public bool Is_Featured { get; set; }
    }
}