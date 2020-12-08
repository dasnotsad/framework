package utils.string;

import net.dgg.utils.string.DggStringUtil;
import org.junit.Assert;
import org.junit.Test;

import static org.hamcrest.core.Is.is;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description:
 */
public class DggStringUtilTest {

    private static final String TEST_STR = "My test String001";
    private static final String TEST_STR_SPACE = " My test String001 ";


    @Test
    public void isEmptyTest() {
        boolean result = DggStringUtil.isEmpty(TEST_STR);
        //Assert.assertTrue("当前字符串不为空!", result);
    }


    @Test
    public void hasTextTest() {
        // DggStringUtil.hasText(" ");
        boolean result = DggStringUtil.hasText(TEST_STR);
        Assert.assertTrue("当前字符串没有内容!", result);
    }

    @Test
    public void hasLengthTest() {
        // DggStringUtil.hasLength(null|"");
        boolean result = DggStringUtil.hasLength(TEST_STR);
        Assert.assertTrue("当前字符串没值!", result);
    }

    @Test
    public void trimWhitespaceTest() {
        String result = DggStringUtil.trimWhitespace(TEST_STR_SPACE);
        Assert.assertEquals("与指定字符串不匹配!", TEST_STR, result);
    }

    @Test
    public void trimLeadingWhitespaceTest() {
        String result = DggStringUtil.trimLeadingWhitespace(TEST_STR_SPACE);
        Assert.assertThat("与指定长度不同!", TEST_STR_SPACE.length() - 1, is(result.length()));
    }

    @Test
    public void trimTrailingWhitespaceTest() {
        String result = DggStringUtil.trimTrailingWhitespace(TEST_STR_SPACE);
        Assert.assertThat("与指定长度不同!", TEST_STR_SPACE.length() - 1, is(result.length()));
    }

    @Test
    public void startsWithIgnoreCaseTest() {
        boolean result = DggStringUtil.startsWithIgnoreCase(TEST_STR, "my");
        Assert.assertTrue("与指定前缀不匹配!", result);
    }

    @Test
    public void endsWithIgnoreCaseTest() {
        boolean result = DggStringUtil.endsWithIgnoreCase(TEST_STR, "001");
        Assert.assertTrue("与指定后缀不匹配!", result);
    }

    @Test
    public void replaceTest() {
        String result = DggStringUtil.replace(TEST_STR, "es", "22");
        Assert.assertEquals("与指定字符串不匹配!", "My t22t String001", result);
    }

    @Test
    public void quoteTest() {
        String result = DggStringUtil.quote(TEST_STR);
        Assert.assertEquals("与指定字符串不匹配!", "'My test String001'", result);
    }

    @Test
    public void capitalizeTest() {
        String uncapResult = DggStringUtil.uncapitalize(TEST_STR);
        Assert.assertEquals("与指定字符串不匹配!", "my test String001", uncapResult);

        String capResult = DggStringUtil.capitalize(uncapResult);
        Assert.assertEquals("与指定字符串不匹配!", TEST_STR, capResult);
    }
}
