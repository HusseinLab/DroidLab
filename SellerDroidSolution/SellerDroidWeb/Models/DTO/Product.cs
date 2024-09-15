using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace SellerDroidWeb.Models.DTO
{
    public class Product : ProductSummary
    {
        public String longDescription { get; set; }

        public Supplier supplier { get; set; }
    }
}