using System;
using System.Collections.Generic;
using System.Linq;
using System.Web;

namespace SellerDroidWeb.Models.DTO
{
    public class ProductSummary : Entity
    {
        public double averageRating { get; set; }
        public double currentUserRating { get; set; }
    }
}