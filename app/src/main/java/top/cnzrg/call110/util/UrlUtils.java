package top.cnzrg.call110.util;

import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class UrlUtils {
    private static final String TAG = "UrlUtils";

    /**
     * Function : 发送Post请求到服务器 Param : params请求体内容，encode编码格式
     */
    public static String submitPostData(String urlString, Map<String, String> params, String encode) {
        byte[] data = getRequestData(params, encode).toString().getBytes();// 获得请求体

        try {
            URL url = new URL(urlString);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setConnectTimeout(3000); // 设置连接超时时间
            httpURLConnection.setDoInput(true); // 打开输入流，以便从服务器获取数据
            httpURLConnection.setDoOutput(true); // 打开输出流，以便向服务器提交数据
            httpURLConnection.setRequestMethod("POST"); // 设置以Post方式提交数据
            httpURLConnection.setUseCaches(false); // 使用Post方式不能使用缓存

            // 设置请求体的类型是文本类型
            httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            // 设置请求体的长度
            httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));

            // 获得输出流，向服务器写入数据
            OutputStream outputStream = httpURLConnection.getOutputStream();
            outputStream.write(data);

            int response = httpURLConnection.getResponseCode(); // 获得服务器的响应码
            if (response == HttpURLConnection.HTTP_OK) {
                InputStream inptStream = httpURLConnection.getInputStream();
                return dealResponseResult(inptStream); // 处理服务器的响应结果
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Function : 封装请求体信息 Param : params请求体内容，encode编码格式
     */
    private static StringBuffer getRequestData(Map<String, String> params, String encode) {
        StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                stringBuffer.append(entry.getKey()).append("=")
                        .append(URLEncoder.encode(entry.getValue(), encode))
                        .append("&");
            }
            stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer;
    }

    /**
     * Function : 处理服务器的响应结果（将输入流转化成字符串） Param : inputStream服务器的响应输入流
     */
    private static String dealResponseResult(InputStream inputStream) {
        String resultData = null; // 存储处理结果
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] data = new byte[1024];
        int len = 0;
        try {
            while ((len = inputStream.read(data)) != -1) {
                byteArrayOutputStream.write(data, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        resultData = new String(byteArrayOutputStream.toByteArray());
        return resultData;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url   发送请求的URL
     * @param param 请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, String param) {
        return sendGet(url + "?" + param);
    }

    /**
     * @param url
     * @return
     */
    public static String httpGet(String url) {
        HttpClient httpCilent = new DefaultHttpClient();
        // RequestConfig requestConfig = RequestConfig.custom()
        // .setConnectTimeout(5000) //设置连接超时时间
        // .setConnectionRequestTimeout(5000) // 设置请求超时时间
        // .setSocketTimeout(5000)
        // .setRedirectsEnabled(true)//默认允许自动重定向
        // .build();
        HttpGet httpGet = new HttpGet(url);
        // httpGet.setConfig(requestConfig);
        String srtResult = "";
        try {
            HttpResponse httpResponse = httpCilent.execute(httpGet);

            if (httpResponse.getStatusLine().getStatusCode() == 200) {
                srtResult = EntityUtils.toString(httpResponse.getEntity());// 获得返回的结果
                System.out.println(srtResult);
                return srtResult;
            } else if (httpResponse.getStatusLine().getStatusCode() == 400) {
                // ..........
            } else if (httpResponse.getStatusLine().getStatusCode() == 500) {
                // .............
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 将指定字符串编码转换为UTF-8
     *
     * @param txt
     * @return
     */
    public static String encode(String txt) {
        try {
            return URLEncoder.encode(txt, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        return null;
    }

    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url 发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url) {
        String result = null;
        BufferedReader in = null;
        try {
            String urlNameString = url;
            URL realUrl = new URL(urlNameString);

            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();

            connection.setConnectTimeout(10_000);
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("user-agent",
                    "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");

            // 建立实际的连接
            connection.connect();

            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String line = "";
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            LogUtils.e("sendGet: 发送GET请求出现异常");
//            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result;
    }
}
