import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

/**
 * 执行 POST 请求并获取返回的 JSON 数据.
 */
public class HttpUtils {
    /**
     * 要发送 POST 请求的目标 URL.
     */
    static final String url = "http://127.0.0.1:5000/ ";
    /**
     * 用于执行 HTTP 请求
     */
    static CloseableHttpClient httpClient = HttpClients.createDefault();

    /**
     * 执行 POST 请求并返回 JSON 数据.
     * @param source 要发送的源代码字符串
     * @return
     * @throws Exception
     */
    public static JSONObject doPost(String source) throws Exception {
        HttpPost httpPost = new HttpPost(url);//创建了一个 HttpPost 对象，表示要发送的 POST 请求，并指定了目标 URL.
        httpPost.setHeader("Content-Type", "application/json");//设置请求头，指定请求体的内容类型为 JSON.
        httpPost.setEntity(new StringEntity(source, "utf-8"));//设置请求体，将源代码字符串转换为 StringEntity 对象，并设置字符编码为 UTF-8.
//        httpPost.addHeader("Accept", "application/json");
        // 使用HttpClient发起请求，返回response
        HttpResponse response = httpClient.execute(httpPost);//使用 HttpClient 执行 POST 请求，并返回响应对象 response.
        System.out.println(response.getStatusLine());//打印响应状态行，即 HTTP 状态码和原因.
        int statusCode = response.getStatusLine().getStatusCode();//获取响应状态码
        if(statusCode >= 200 && statusCode < 300){
            //状态码表示请求成功
            HttpEntity entity = response.getEntity();
            String results = EntityUtils.toString(entity,"UTF-8");
            try {
                return JSONObject.parseObject(results);//将 JSON 字符串解析为 JSONObject 对象，并返回.
            }catch (Exception e){
                return null;
            }
        }
        return null;
    }

    /**
     * 测试 doPost 方法
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String testSource = "public int[] twoSum(int[] nums, int target) {" +
                "PRED " +
                "for (int i = 0; i < n; ++i) {" +
                "for (int j = i + 1; j < n; ++j) {" +
                "if (nums[i] + nums[j] == target) {" +
                "return new int[]{i, j};}}}" +
                "return new int[0];}";
        JSONObject object = doPost(testSource);
        System.out.println(object);
    }
}
