package lb.edu.aust.sellerdroid.components;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.google.gson.GsonBuilder;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.GZIPInputStream;

import lb.edu.aust.sellerdroid.dto.LoginRequest;
import lb.edu.aust.sellerdroid.dto.Product;
import lb.edu.aust.sellerdroid.dto.ProductSummary;
import lb.edu.aust.sellerdroid.dto.RegistrationRequest;
import lb.edu.aust.sellerdroid.dto.Supplier;

public class SellerDroidClient {

    String _baseUrl;
    HttpClient defaultHttpClient = new DefaultHttpClient();

    public SellerDroidClient(String baseUrl) {
        setBaseUrl(baseUrl);
    }

    public String getBaseUrl() {
        return _baseUrl;
    }

    public void setBaseUrl(String newBaseUrl) {
        _baseUrl = newBaseUrl;
    }

    private <T> T GetObject(HttpResponse httpResponse, final Class<T> objectClass) {

        HttpEntity httpEntity = httpResponse.getEntity();
        try {
            if (httpEntity != null) {
                InputStream inputStream = httpEntity.getContent();
                Header contentEncoding = httpResponse.getFirstHeader("Content-Encoding");
                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
                    inputStream = new GZIPInputStream(inputStream);
                }
                String resultString = convertStreamToString(inputStream);
                inputStream.close();
                return new GsonBuilder().create().fromJson(resultString, objectClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String convertStreamToString(InputStream inputStream) {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line = null;
        try {
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stringBuilder.toString();
    }

    private <T> T Post(final String url, final Object postData, final Class<T> objectClass) {

        HttpPost httpPost = new HttpPost(_baseUrl + url);

        try {
            if(postData != null) {
                if (postData.getClass().equals(byte[].class)) {
                    byte[] bytes = (byte[]) postData;
                    StringBuilder sb = new StringBuilder(bytes.length);
                    for (int i = 0; i < bytes.length; i++) {
                        sb.append((char) bytes[i]);
                    }
                    StringEntity bytesStringEntity = new StringEntity(sb.toString());
                    httpPost.setEntity(bytesStringEntity);
                } else {
                    StringEntity stringEntity = new StringEntity(new GsonBuilder().create().toJson(postData));
                    httpPost.setEntity(stringEntity);
                }
            }

            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("Accept-Encoding", "gzip");

            HttpResponse httpResponse = defaultHttpClient.execute(httpPost);
            T result = GetObject(httpResponse, objectClass);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private <T> T Get(String url, List<NameValuePair> params, final Class<T> objectClass) {
        if (params != null) {
            String paramString = URLEncodedUtils.format(params, "utf-8");
            url += "?" + paramString;
        }

        HttpGet httpGet = new HttpGet(_baseUrl + url);

        try {
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Accept-Encoding", "gzip");
            HttpResponse httpResponse = defaultHttpClient.execute(httpGet);
            T result = GetObject(httpResponse, objectClass);
            return result;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public String doLogin(String username, String password) {
        String result = Post("/api/MobileAccess/Login", new LoginRequest(username, password), String.class);
        if("Success".equals(result)) {
            loggedInUser = username;
        }
        return result;
    }

    public boolean doLogout() {
        String result = Get("/api/MobileAccess/Logout", null, String.class);
        if("Success".equals(result)) {
            loggedInUser = null;
            return true;
        }
        return false;
    }

    public String doRegister(RegistrationRequest registrationRequest) {
        return Post("/api/MobileAccess/Register", registrationRequest, String.class);
    }

    public ProductSummary[] doSearchProducts(String searchText) {
        return Post("/api/MobileAccess/SearchProducts", searchText, ProductSummary[].class);
    }

    public Product doGetProduct(String id) {
        return Get("/api/MobileAccess/Product/" + id, null, Product.class);
    }

    public Supplier doGetSupplier(String id) {
        return Get("/api/MobileAccess/Supplier/" + id, null, Supplier.class);
    }

    String loggedInUser = null;

    public String getLoggedInUser() {
        return loggedInUser;
    }

    //
    // This relies on the user being logged-in
    public float UpdateMyProductRating(String productId, double myRating) {
        return Get("/api/MobileAccess/RateProduct/?id=" + productId + "&myRating=" + myRating, null, float.class);
    }

    public ProductSummary[] doSearchByImage(String searchMode, byte[] imageBytes) {
        String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return Post("/api/MobileAccess/SearchByImage/?searchMode=" + searchMode, imageBase64, ProductSummary[].class);
    }

    public ProductSummary[] doSearchByBarcode(String barcode) {
        return Get("/api/MobileAccess/SearchByBarcode/?barcode=" + barcode, null, ProductSummary[].class);
    }
}
