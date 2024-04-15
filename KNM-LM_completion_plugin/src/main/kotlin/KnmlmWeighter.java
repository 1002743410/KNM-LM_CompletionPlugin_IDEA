import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementWeigher;

/**
 * 为代码补全建议项进行加权排序.
 */
public class KnmlmWeighter extends LookupElementWeigher {
    KnmlmWeighter(){
        super("KnmlmWeighter",false,false);//权重器的名称、是否反向排序和是否逐字符比较
    }

    /**
     * 计算建议项的权重。
     * 如果建议项是 KnmlmLookupElement 的实例，则返回其索引作为权重；否则返回整数最大值，以确保其他建议项排在它之前。
     * @param element
     * @return
     */
    @Override
    public Integer weigh(LookupElement element){
        if (element instanceof  KnmlmLookupElement){
            return ((KnmlmLookupElement) element).index;
        }
        return Integer.MAX_VALUE;
    }
}
