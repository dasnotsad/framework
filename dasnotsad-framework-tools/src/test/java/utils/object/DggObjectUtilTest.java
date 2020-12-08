package utils.object;

import net.dgg.utils.object.DggObjectUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;

/**
 * @Author: dgg-linhongda
 * @Date: 2019/6/24 0024
 * @Description:
 */
public class DggObjectUtilTest {


    private static final String[] TEST_ARRAY = {"test01", "test02"};

    @Test
    public void isArrayTest() {
        boolean result = DggObjectUtil.isArray(TEST_ARRAY);
        Assert.assertTrue("数组验证失败!", result);
    }

    @Test
    public void isEmptyArrayTest() {
        boolean result = DggObjectUtil.isEmpty(TEST_ARRAY);
        //Assert.assertTrue("数组为空验证失败!", result);
    }

    @Test
    public void isEmptyTest() {
        List<String> stringList = new ArrayList<>();
        boolean result = DggObjectUtil.isEmpty(stringList);
        Assert.assertTrue("对象为空验证失败!", result);
    }

    @Test
    public void containsElementTest() {
        boolean result = DggObjectUtil.containsElement(TEST_ARRAY, "test02");
        Assert.assertTrue("包含指定元素验证失败!", result);
    }

    @Test
    public void toObjectArrayTest() {
        Object[] objects = DggObjectUtil.toObjectArray(TEST_ARRAY);
        Assert.assertThat("转换成功!", objects.length, is(TEST_ARRAY.length));
    }
}