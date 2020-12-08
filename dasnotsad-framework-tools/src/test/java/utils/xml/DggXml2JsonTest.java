//package utils.xml;
//
//import com.thoughtworks.xstream.XStream;
//import net.dgg.utils.xml.DggXmlJsonConversionUtil;
//import net.sf.json.JSONObject;
//import org.junit.Test;
//
///**
// * @ClassName: DggXml2JsonTest
// * @Description: TODO
// * @Author: jiangsh
// * @Date: 2019/10/29 16:56
// */
//public class DggXml2JsonTest {
//
//        String s = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//                "<business comment=\"监控数据查询\" id=\"Jksjcx\">\n" +
//                "  <body yylxdm=\"1\">\n" +
//                "    <output>\n" +
//                "      <curFpdm>当前发票代码</curFpdm>\n" +
//                "      <startFphm>发票起号</startFphm>\n" +
//                "      <endFphm>发票止号</endFphm>\n" +
//                "      <limit>开票限额</limit>\n" +
//                "      <offlineInvAmout>离线开票限额</offlineInvAmout>\n" +
//                "\t  <offlineTime>离线开票时间（单位小时）</offlineTime>\n" +
//                "\t  <isCsq>是否到抄税期</isCsq>\n" +
//                "\t  <isSsq>是否到锁死期</isSsq>\n" +
//                "\t  <repTime>抄税起始日期</repTime>\n" +
//                "\t  <lastRepTime>上次报税日期</lastRepTime>\n" +
//                "      <returncode>0</returncode>\n" +
//                "      <returnmsg>成功</returnmsg>\n" +
//                "    </output>\n" +
//                "  </body>\n" +
//                "</business>\n";
//
//        String s1 = "{\n" +
//                " \"@comment\": \"监控数据查询\",\n" +
//                " \"@id\": \"Jksjcx\",\n" +
//                " \"body\":  {\n" +
//                "  \"@yylxdm\": \"1\",\n" +
//                "  \"output\":   {\n" +
//                "   \"curFpdm\": \"当前发票代码\",\n" +
//                "   \"startFphm\": \"发票起号\",\n" +
//                "   \"endFphm\": \"发票止号\",\n" +
//                "   \"limit\": \"开票限额\",\n" +
//                "   \"offlineInvAmout\": \"离线开票限额\",\n" +
//                "   \"offlineTime\": \"离线开票时间（单位小时）\",\n" +
//                "   \"isCsq\": \"是否到抄税期\",\n" +
//                "   \"isSsq\": \"是否到锁死期\",\n" +
//                "   \"repTime\": \"抄税起始日期\",\n" +
//                "   \"lastRepTime\": \"上次报税日期\",\n" +
//                "   \"returncode\": \"0\",\n" +
//                "   \"returnmsg\": \"成功\"\n" +
//                "  }\n" +
//                " }\n" +
//                "}";
//
//        String s2 = "{\n" +
//                " \n" +
//                " \"business\":  {\n" +
//                "  \"@comment\": \"监控数据查询\",\n" +
//                "  \"@id\": \"Jksjcx\",\n" +
//                "  \"body\":   {\n" +
//                "   \"@yylxdm\": \"1\",\n" +
//                "   \"output\":    {\n" +
//                "    \"curFpdm\": \"当前发票代码\",\n" +
//                "    \"startFphm\": \"发票起号\",\n" +
//                "    \"endFphm\": \"发票止号\",\n" +
//                "    \"limit\": \"开票限额\",\n" +
//                "    \"offlineInvAmout\": \"离线开票限额\",\n" +
//                "    \"offlineTime\": \"离线开票时间（单位小时）\",\n" +
//                "    \"isCsq\": \"是否到抄税期\",\n" +
//                "    \"isSsq\": \"是否到锁死期\",\n" +
//                "    \"repTime\": \"抄税起始日期\",\n" +
//                "    \"lastRepTime\": \"上次报税日期\",\n" +
//                "    \"returncode\": \"0\",\n" +
//                "    \"returnmsg\": \"成功\"\n" +
//                "   }\n" +
//                "  }\n" +
//                " }\n" +
//                "}";
//
//    @Test
//    public void xmlTojson() {
////        System.out.println(DggXmlJsonConversionUtil.xml2json(s).replace("@", ""));
////        System.out.println(DggXmlJsonConversionUtil.xml2json(s));
////        System.out.println(DggXmlJsonConversionUtil.xmlToJson(s3).replace("\"@id\": \"aid\",", ""));
//        System.out.println(DggXmlJsonConversionUtil.xmlToJson(s3));
//    }
//
//    @Test
//    public void xmlTojson2() {
////        System.out.println(DggXmlJsonConversionUtil.xmlToJsonWithOut(s4));
//        System.out.println(DggXmlJsonConversionUtil.xmlToJsonWithOut(s));
//    }
//
//    @Test
//    public void jsonToxmlAndHead() {
//        System.out.println(DggXmlJsonConversionUtil.jsonToXmlAndHead(s1));
//    }
//
//
//    @Test
//    public void jsonToxml() {
//        System.out.println(DggXmlJsonConversionUtil.jsonToXml(s1));
//    }
//
//    @Test
//    public void jsonToxm2() {
//        System.out.println(DggXmlJsonConversionUtil.jsonToXmlHeadAndWithOut(s2));
//    }
//
//
//    @Test
//    public void beanToXml() {
//        User user1 = new User("lanweihong", "lwhhhp@gmail.com");
//        User user2 = new User("lanweihong333", "lwhhhp@gmail.com222");
//        System.out.println(DggXmlJsonConversionUtil.beanToXml(user1));
//        System.out.println(DggXmlJsonConversionUtil.beanToXml(user2));
//    }
//
//
//
//    @Test
//    public void beanToXml2() {
//        User user1 = new User("lanweihong", "lwhhhp@gmail.com");
//        JSONObject json = JSONObject.fromObject(user1);//将java对象转换为json对象
//        String str = json.toString();//将json对象转换为字符串
//        System.out.println("----------->" + str);
//        System.out.println(DggXmlJsonConversionUtil.jsonToXml(str));
//    }
//
//
//    @Test
//    public void parseObjectTest() {
//        XStream xStream = new XStream();
//        xStream.alias("User", User.class);
//        String xml = "<User>\n" +
//                "  <userName>lanweihong</userName>\n" +
//                "  <email>lwhhhp@gmail.com</email>\n" +
//                "</User>";
//        //转对象
//        User user = (User) xStream.fromXML(xml);
//        System.out.println(user.toString());
//    }
//
//    @Test
//    public void xmlToBean() {
//        String xml = "<User>\n" +
//                "  <userName>lanweihong</userName>\n" +
//                "  <email>lwhhhp@gmail.com</email>\n" +
//                "  <age>18</age>\n" +
//                "  <school>beijing</school>\n" +
//                "</User>";
//        System.out.println(DggXmlJsonConversionUtil.xmlToBean(User.class, xml));
//    }
//
//    String s3 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//            "<aaa id=\"aid\">" +
//            "<business comment=\"发票查询\" id=\"Fpcx\">\n" +
//            "  <body yylxdm=\"1\">\n" +
//            "    <output>\n" +
//            "      <Info>\n" +
//            "        <GFMC>购方名称</GFMC>\n" +
//            "        <GFSH>购方税号</GFSH>\n" +
//            "        <GFDZDH>购方地址电话</GFDZDH>\n" +
//            "        <GFYHZH>购方银行账号</GFYHZH>\n" +
//            "        <XFMC>销方名称</XFMC>\n" +
//            "        <XFSH>销方税号</XFSH>\n" +
//            "        <XFDZDH>销方地址电话</XFDZDH>\n" +
//            "        <XFYHZH>销方银行账号</XFYHZH>\n" +
//            "        <BZ>备注</BZ>\n" +
//            "        <KPR>开票人</KPR>\n" +
//            "        <SKR>收款人</SKR>\n" +
//            "        <FHR>复核人</FHR>\n" +
//            "        <KPSXH>序号</KPSXH>\n" +
//            "        <DZSYH>受理序号</DZSYH>\n" +
//            "        <QDBZ>清单标志</QDBZ>\n" +
//            "        <BMBBBH>编码版本号</BMBBBH>\n" +
//            "        <JE>金额</JE>\n" +
//            "        <SE>税额</SE>\n" +
//            "        <ZHSL>税率</ZHSL>\n" +
//            "        <MXS>\n" +
//            "          <MX>\n" +
//            "            <SPMC>商品名称</SPMC>\n" +
//            "            <GGXH>规格型号</GGXH>\n" +
//            "            <JLDW>计量单位</JLDW>\n" +
//            "            <SL>数量</SL>\n" +
//            "            <DJ>单价</DJ>\n" +
//            "            <HSJBZ>含税价标志</HSJBZ>\n" +
//            "            <FPHXZ>发票行性质</FPHXZ>\n" +
//            "            <JE>金额</JE>\n" +
//            "            <SLV>税率</SLV>\n" +
//            "            <SE>税额</SE>\n" +
//            "            <FLBM>分类编码</FLBM>\n" +
//            "            <LSLVBS>0税率标识</LSLVBS>\n" +
//            "            <XSYH>销售优惠标志</XSYH>\n" +
//            "            <YHSM>优惠说明</YHSM>\n" +
//            "          </MX>\n" +
//            "        </MXS>\n" +
//            "      </Info>\n" +
//            "      <returncode>0</returncode>\n" +
//            "      <returnmsg>成功</returnmsg>\n" +
//            "    </output>\n" +
//            "  </body>\n" +
//            "</business>\n" +"</aaa>";
//
//    String s4 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
//            "<business comment=\"发票查询\" id=\"Fpcx\">\n" +
//            "  <body yylxdm=\"1\">\n" +
//            "    <output>\n" +
//            "      <Info>\n" +
//            "        <GFMC>购方名称</GFMC>\n" +
//            "        <GFSH>购方税号</GFSH>\n" +
//            "        <GFDZDH>购方地址电话</GFDZDH>\n" +
//            "        <GFYHZH>购方银行账号</GFYHZH>\n" +
//            "        <XFMC>销方名称</XFMC>\n" +
//            "        <XFSH>销方税号</XFSH>\n" +
//            "        <XFDZDH>销方地址电话</XFDZDH>\n" +
//            "        <XFYHZH>销方银行账号</XFYHZH>\n" +
//            "        <BZ>备注</BZ>\n" +
//            "        <KPR>开票人</KPR>\n" +
//            "        <SKR>收款人</SKR>\n" +
//            "        <FHR>复核人</FHR>\n" +
//            "        <KPSXH>序号</KPSXH>\n" +
//            "        <DZSYH>受理序号</DZSYH>\n" +
//            "        <QDBZ>清单标志</QDBZ>\n" +
//            "        <BMBBBH>编码版本号</BMBBBH>\n" +
//            "        <JE>金额</JE>\n" +
//            "        <SE>税额</SE>\n" +
//            "        <ZHSL>税率</ZHSL>\n" +
//            "        <MXS>\n" +
//            "          <MX>\n" +
//            "            <SPMC>商品名称</SPMC>\n" +
//            "            <GGXH>规格型号</GGXH>\n" +
//            "            <JLDW>计量单位</JLDW>\n" +
//            "            <SL>数量</SL>\n" +
//            "            <DJ>单价</DJ>\n" +
//            "            <HSJBZ>含税价标志</HSJBZ>\n" +
//            "            <FPHXZ>发票行性质</FPHXZ>\n" +
//            "            <JE>金额</JE>\n" +
//            "            <SLV>税率</SLV>\n" +
//            "            <SE>税额</SE>\n" +
//            "            <FLBM>分类编码</FLBM>\n" +
//            "            <LSLVBS>0税率标识</LSLVBS>\n" +
//            "            <XSYH>销售优惠标志</XSYH>\n" +
//            "            <YHSM>优惠说明</YHSM>\n" +
//            "          </MX>\n" +
//            "        </MXS>\n" +
//            "      </Info>\n" +
//            "      <returncode>0</returncode>\n" +
//            "      <returnmsg>成功</returnmsg>\n" +
//            "    </output>\n" +
//            "  </body>\n" +
//            "</business>\n";
//}
