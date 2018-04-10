package com.howietian.chenyan;

import android.content.ClipboardManager;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main3Activity extends AppCompatActivity {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("https://mp.weixin.qq.com/s?__biz=MjM5NDgxNTQwNQ==&mid=2650721478&idx=1&sn=52e42e90377057bb4b70132f183b171f&chksm=be88600489ffe9121def4bcc880999473479ce7eacaf21ccfe3868873c06e0837479f7533ac1&mpshare=1&scene=23&srcid=0204naMtmqVWSN5sezs3xwJT#rd");
    }

//    public void test() {
//        /**
//         * 设置剪贴板的内容
//         */
//        ClipboardManager cbm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
//        cbm.setText("http://howietian.top/2018/03/23/nginx-%E5%8F%8D%E5%90%91%E4%BB%A3%E7%90%86%E9%85%8D%E7%BD%AE%E4%BA%8C%E7%BA%A7%E5%9F%9F%E5%90%8D/");
//
//
//        /**
//         * 获取剪贴板的内容
//         */
//        String content = (String) cbm.getText();
//
//        String url1 = "http://www.xx.com";
//        String url2 = "w.xx.com";
//        String url3 = "http://w.xx.com";
//        String url4 = "ssss";
//        Pattern pattern = Pattern
//                .compile("^([hH][tT]{2}[pP]://|[hH][tT]{2}[pP][sS]://)(([A-Za-z0-9-~]+).)+([A-Za-z0-9-~\\/])+$");
//        System.out.println(pattern.matcher(url1).matches());
//        System.out.println(pattern.matcher(url2).matches());
//        System.out.println(pattern.matcher(url3).matches());
//        System.out.println(pattern.matcher(url4).matches());
//
//        // 获取页面内容
//        view.loadUrl("javascript:window.java_obj.showSource("
//                + "document.getElementsByTagName('html')[0].innerHTML);");
//
//    }
//
//}
//
//    private void button1_Click(object sender, EventArgs e) {
//        string s1 = this.textBox1.Text;
//        //正则表达式内容
//        String match1 = "^(http|https|ftp)\://[a-zA-Z0-9\-\.]+\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\-\._\?\,\'/\\\+&%\$#\=~])*$";
//        String match2 = "[a-zA-Z0-9\-\.]+\.[a-zA-Z]{2,3}(:[a-zA-Z0-9]*)?/?([a-zA-Z0-9\-\._\?\,\'/\\\+&%\$#\=~])*$";
//        String match3 = "[a-zA-z]+://[^\s]*";
//
//
//        //初始化正则表达式实例
//        Regex reg = new Regex(match);
//        //开始验证
//        bool HasValidate = reg.IsMatch(s1);
//
//        if (HasValidate) {
//            //MessageBox.Show("这是网站有效URL格式。");
//            try {
//                string tmp = GetHtml(s1);
//                string tmpend = StripHTML(tmp);
//
//            } catch (Exception) {
//                //MessageBox.Show("3.该网站只能手动查询！");
//            }
//        }
//
//        String HTMLSource = null;
//        String regMatchTag = "<[^>]*>";
//        String regMatchEnter = "\\s*|\t|\r|\n";
//        Pattern p = Pattern.compile(regMatchEnter);
//        Matcher m = p.matcher(HTMLSource);
//        HTMLSource = m.replaceAll("");
//    }


}
